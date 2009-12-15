package simulator;


import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import node.Node;
import node.SimpleNode;

import scheduler.SimpleScheduler;
import scheduler.SleepScheduler;
import scheduler.Scheduler;
import scheduler.SleepSchedulerKeep;
import scheduler.command.Command;
import trace.TraceJob;
import trace.TraceList;
import job.SimulatedJob;

public class Simulator {

	// scheduler used, it should give commands which are processed
	private Scheduler scheduler;
	
	// current jobs that are being processed on the nodes
	private List<SimulatedJob> jobStatus;
	
	// commands that have been issued, but the time is not great enough
	// to simulate them
	private List<Command> queuedCommands;
	
	// list of jobs which are finished. each job contains information 
	// its runtime
	private List<SimulatedJob> allFinishedJobs;
	
	// prints to a debug file
	private PrintStream debug;
	
	// logs the number of nodes running at time n
	private PrintStream nodesUp;

	// stores maximum number of nodes up at any time since the last
	// output to PrintStream nodesUp
	int maxNodes;
	
	private int time;
	private List<Node> nodes;
	
	public Simulator(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.nodes = scheduler.getNodes();
		initialize();
	}
	
	public void initialize() {
		jobStatus = new ArrayList<SimulatedJob>();
		queuedCommands = new ArrayList<Command>();
		allFinishedJobs = new ArrayList<SimulatedJob>();
		time = 0;
		maxNodes = 0;
		
		try {
			debug = new PrintStream("debug");
			nodesUp = new PrintStream("nodesup");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot open output files.");
		}
	}
	
	public void cleanUp() {
		debug.close();
		nodesUp.close();	
	}

	public void simulateUntilFinish() {
		while(!finished()) {
			simulate();
		}
		cleanUp();
	}
	
	public void printNodesUp(int time) {
		int numNodes = 0;
		for (int i = 0 ; i < nodes.size(); i++) {
			if (nodes.get(i).isOn()) {
				numNodes++;
			}
		}
		// update maxNodes
		maxNodes = Math.max(numNodes, maxNodes);
		
		// output to PrintStream nodesup the maximum nodes up during the
		// last n seconds. currently n = 60, but this can be changed
		if (time % 60 == 0) {
			nodesUp.println(time + " " + numNodes);
			maxNodes = 0;
		}
	}
	
	private void simulate() {
		
		List<Command> commands = scheduler.getCommands(time);
		if (commands != null) {
			queuedCommands.addAll(commands);
		}
		
		// see if any commands can be issued, and issue them
		processQueuedCommands();
		// update the jobs on each node. if a job is finished, then
		// it will be removed
		processJobsOnNodes();		
		
		incTime();
		
		printNodesUp(time);
	}
	
	private void incTime() {
		time++;
	}

	private void processJobsOnNodes() {
		//update jobs on each node
		for(Node node : nodes) {
			if (!node.isOn()) 
				continue;
			
			node.incUpTime();
			// get all active jobs on node
			List<SimulatedJob> nodeJobs = node.getJobs();
			if (nodeJobs.size() == 0) 
				continue;
			
			// find all finished jobs on the node
			ArrayList<SimulatedJob> finishedJobs = new ArrayList<SimulatedJob>();
			for (SimulatedJob job : nodeJobs) {
				if(job.isFinished(time)) {
					finishedJobs.add(job);
				}
			}
			
			// remove the finished jobs from the appropriate list
			// add the finished jobs to the allFinishedJobs list
			for (SimulatedJob finishedJob : finishedJobs) {
				nodeJobs.remove(finishedJob);
				jobStatus.remove(finishedJob);
				allFinishedJobs.add(finishedJob);
				
				debug.println("" + time + ":Finish (" + node.getNodeId() + ")" + finishedJob.getJobId());
			}
		}
	}

	private void processQueuedCommands() {
		List<Command> finishedCommands = new ArrayList<Command>();
		// go through all of the queued commands
		for (Command command : queuedCommands) {
			if (command.shouldExecute(time)) {
				debug.println("" + time + ":" + command);
				command.execute(jobStatus, time);
			}
			if (command.isFinished(time)) {
				finishedCommands.add(command);
			}
		}
		queuedCommands.removeAll(finishedCommands);
	}

	private boolean finished() {
		return scheduler.isFinished() && jobStatus.isEmpty();
	}
	
	public void outputInfo() {
		try {
			PrintStream ps = new PrintStream("nodeuptime");
			debug.println("Total time:" + time);
			ps.println("Total\t" + time);
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.get(i);
				debug.println(node.getNodeId() + ":" + node.getUpTime());
				ps.println("" + node.getNodeId() + "\t" + node.getUpTime());
			}
			ps.close();
			
			ps = new PrintStream("jobinfo");
			ps.println("id,runtime,completiontime,timeofday");
	
			Map<String, ArrayList<SimulatedJob>> finishedJobs = new HashMap<String, ArrayList<SimulatedJob>>();
			for (int i = 0; i < allFinishedJobs.size(); i++) {
				SimulatedJob j = allFinishedJobs.get(i);
				if (finishedJobs.containsKey(j.getJobId())) {
					ArrayList<SimulatedJob> j2 = finishedJobs.get(j.getJobId());
					j2.add(j);
				} else {
					ArrayList<SimulatedJob> j2 = new ArrayList<SimulatedJob>();
					j2.add(j);
					finishedJobs.put(j.getJobId(), j2);
				}
			}
			
			double avgIncTTC = 0;
			double avgPerIncTTC = 0;
			
			for (String jobId : finishedJobs.keySet()) {
				ArrayList<SimulatedJob> jobs = finishedJobs.get(jobId);
				int runtime = 0;
				int comptime = 0;
				for (int i = 0;  i < jobs.size(); i++) {
					SimulatedJob j = jobs.get(i);
					runtime = Math.max(runtime, j.getExecTime());
					comptime = Math.max(comptime, j.getTimeToCompletion());
				}
				
				avgIncTTC += ((double)(comptime - runtime));
				if (runtime != 0)
					avgPerIncTTC += 100 * (((double)(comptime - runtime)) / runtime);
				else 
					avgPerIncTTC += 100;
				
				ps.println(jobId + " " + runtime + " " + comptime +
						" " + jobs.get(0).getTimeOfDay());
			}
			avgIncTTC /= finishedJobs.size();
			avgPerIncTTC /= finishedJobs.size();
			
			ps.close();

			ps = new PrintStream("jobtime");
			
			ArrayList<String> timeOfDay = new ArrayList<String>();
			for (String jobId : finishedJobs.keySet()) {
				ArrayList<SimulatedJob> jobs = finishedJobs.get(jobId);
				timeOfDay.add(jobs.get(0).getTimeOfDay());
			}
			
			for (int i = 0; i < 24; i++) {
				int num = 0;
				for (int j = 0; j < timeOfDay.size(); j++) {
					String tof = "" + i;
					if (tof.length() == 1) {
						tof = "0" + tof;
					}
					if (timeOfDay.get(j).equals(tof) ) {
						num++;
					}
				}
				ps.println("" + i + "\t" + num);
			}

			ps.close();
			
			ps = new PrintStream("stats");
			ps.println("Num nodes: " + nodes.size());
			ps.println("Num jobs: " + finishedJobs.size());
			int days = ((time / 60) / 60) / 24;
			int hours = ((time / 60) / 60) % 24;
			ps.println("Total time: " + days + "days, " + hours + " hours");
			double avgUptime = 0;
			for (Node node : nodes) {
				avgUptime +=  node.getUpTime();
			}
			avgUptime /= nodes.size();
			double percentUptime = (100 * avgUptime) / time;
			ps.println("Average uptime per node (%): " + percentUptime);
			ps.println("Average uptime per node (sec): " + avgUptime);
			ps.println("Avg Inc TTC (sec): " + avgIncTTC);
			ps.println("Avg Inc TTC (%): " + avgPerIncTTC);
			
			ps.close();
			
			ps = new PrintStream("joblength");
			for (String jobId : finishedJobs.keySet()) {
				SimulatedJob j = finishedJobs.get(jobId).get(0);
				ps.println(j.getExecTime());
			}
			ps.close();
			

 		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Calls to retrieve a list of jobs from a trace
	public static TraceList runGwf(String traceFile, int jobSize, int nodeSize) {
		TraceList trace = new TraceList(traceFile, jobSize,"gwf");
		System.out.println("Trace read in from " + traceFile);
		trace.getNodeList(nodeSize);
		return trace;
	} 
	
	public static TraceList runMaui(String traceFile, int size) {
		TraceList trace = new TraceList(traceFile, size, "muai");
		return trace;
	}
	
	public static TraceList runTorque(String traceFile, int size) {
		TraceList trace = new TraceList(traceFile, 200, "torque");
		return trace;
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		if (args.length < 1) {
			System.out.println("java Simulator [sample load file]");
		}
		
		//String traceFile = "traces/grid5000_clean_trace.log";
		//String traceFile = "traces/HPC2N";
		
		//String traceFile = "traces/nordu.gwf";
		//TraceList trace = runGwf(traceFile);
		
		String traceFile = "traces/nordu.gwf";
		TraceList trace = runTorque(traceFile,10);
		
		ArrayList<TraceJob> tl = trace.getTraceList();
		ArrayList<Node> nl = trace.getNodeList();
		Scheduler scheduler = new SleepScheduler(tl, nl, 1);
		
		System.out.println("Simulating " + tl.size() + " jobs on " + nl.size() + " nodes");
		
		Simulator simulator = new Simulator(scheduler);
		simulator.simulateUntilFinish();
		System.out.println("Simulation done");
		simulator.outputInfo();
		System.out.println("Output done");
	}
}