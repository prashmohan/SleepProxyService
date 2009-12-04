package simulator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import node.Node;
import node.SimpleNode;

import scheduler.SleepScheduler;
import scheduler.Scheduler;
import scheduler.command.Command;
import trace.TraceList;
import job.SimulatedJob;

public class Simulator {

	private Scheduler scheduler;
	private int time;
	private Map<Node, ArrayList<SimulatedJob>> jobsOnNode;
	private List<Node> nodes;
	private List<SimulatedJob> jobStatus;
	private List<Command> queuedCommands;

	public Simulator(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.nodes = scheduler.getNodes();
		initialize();
	}
	
	public void initialize() {
		jobStatus = new ArrayList<SimulatedJob>();
		jobsOnNode = new HashMap<Node, ArrayList<SimulatedJob>>();
		queuedCommands = new ArrayList<Command>();
		for(Node node : nodes) {
			jobsOnNode.put(node, new ArrayList<SimulatedJob>());
		}
		time = 0;
	}

	public void simulateUntilFinish() {
		while(!finished()) {
			simulate();
		}
	}
	
	private void simulate() {
		processQueuedCommands();
		processJobsOnNodes();
		
		List<Command> commands = scheduler.getCommands(time);
		if (commands != null) {
			for (Command command : commands) {
				queuedCommands.add(command);
			}
		}
		time++;
	}

	private void processJobsOnNodes() {
		for(Node node : jobsOnNode.keySet()) {
			if (!node.isOn()) 
				continue;
			
			ArrayList<SimulatedJob> nodeJobs = jobsOnNode.get(node);
			if (nodeJobs.size() == 0) 
				continue;
			
			ArrayList<SimulatedJob> finishedJobs = new ArrayList<SimulatedJob>();
			for (SimulatedJob job : nodeJobs) {
				job.update(time);
				if(job.isFinished(time)) {
					finishedJobs.add(job);
				}
			}
			
			if (nodeJobs.size() == finishedJobs.size()) {
				node.setAvailable(true);
			}
			
			for (SimulatedJob finishedJob : finishedJobs) {
				nodeJobs.remove(finishedJob);
				jobStatus.remove(finishedJob);
				System.out.println(finishedJob.getJobId() + " finished at " + time);
			}
		}
	}

	private void processQueuedCommands() {
		List<Command> finishedCommands = new ArrayList<Command>();
		for (Command command : queuedCommands) {
			if (command.shouldExecute(time)) {
				System.out.println(command);
				command.execute(jobsOnNode, jobStatus, time);
			}
			if (command.isFinished(time)) {
				finishedCommands.add(command);
			}
		}
		for (Command command : finishedCommands) {
			queuedCommands.remove(command);
		}
	}

	private boolean finished() {
		return scheduler.isFinished() && jobStatus.isEmpty();
	}

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("java Simulator [sample load file]");
		}
		String traceFile = "traces/smalltrace";
		TraceList trace = new TraceList(traceFile, 3, "muai");
		List<Node> nodes = new ArrayList<Node>();
		int numNodes = 35;
		for (int i = 0; i < numNodes; i++) {
			nodes.add(new SimpleNode("" + i));
		}
		Scheduler scheduler = new SleepScheduler(trace.getTracelist(), nodes, 5);
		Simulator simulator = new Simulator(scheduler);
		simulator.simulateUntilFinish();
	}
}