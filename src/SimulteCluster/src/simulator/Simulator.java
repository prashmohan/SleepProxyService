package simulator;


import java.util.ArrayList;
import java.util.List;
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
		queuedCommands = new ArrayList<Command>();
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
	
		time++;
		
		List<Command> commands = scheduler.getCommands(time);
		if (commands != null) {
			for (Command command : commands) {
				queuedCommands.add(command);
			}
		}
	}

	private void processJobsOnNodes() {
		for(Node node : nodes) {
			if (!node.isOn()) 
				continue;
			
			List<SimulatedJob> nodeJobs = node.getJobs();
			if (nodeJobs.size() == 0) 
				continue;
			
			ArrayList<SimulatedJob> finishedJobs = new ArrayList<SimulatedJob>();
			for (SimulatedJob job : nodeJobs) {
				job.update(time);
				if(job.isFinished(time)) {
					finishedJobs.add(job);
				}
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
				System.out.println("" + time + ":" + command);
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

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("java Simulator [sample load file]");
		}
		String traceFile = "traces/smalltrace";
		TraceList trace = new TraceList(traceFile, 3, "muai");
		List<Node> nodes = new ArrayList<Node>();
		int numNodes = 35;
		for (int i = 0; i < numNodes; i++) {
			nodes.add(new SimpleNode("" + i, 60, 60));
		}
		Scheduler scheduler = new SleepScheduler(trace.getTracelist(), nodes, 5);
		Simulator simulator = new Simulator(scheduler);
		simulator.simulateUntilFinish();
	}
}