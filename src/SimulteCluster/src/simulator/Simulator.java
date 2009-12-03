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
	private Map<Node, ArrayList<SimulatedJob>> nodeStatus;
	private List<Node> nodes;
	private List<SimulatedJob> jobStatus;
	private Map<Command, Integer> queuedCommands;

	public Simulator(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.nodes = scheduler.getNodes();
		initialize();
	}
	
	public void initialize() {
		jobStatus = new ArrayList<SimulatedJob>();
		nodeStatus = new HashMap<Node, ArrayList<SimulatedJob>>();
		for(Node node : nodes) {
			nodeStatus.put(node, new ArrayList<SimulatedJob>());
		}
		time = 0;
	}

	public void simulateUntilFinish() {
		while(!finished()) {
			simulate();
		}
	}
	
	private void simulate() {
		List<Command> commands = scheduler.getCommands(time);
		for (Command command : commands) {
			queuedCommands.put(command, time);
		}
		processQueuedCommands();
		processJobsOnNodes();
		time++;
	}

	private void processJobsOnNodes() {
		for(Node node : nodeStatus.keySet()) {
			if (node.isSleeping()) 
				continue;
			
			ArrayList<SimulatedJob> nodeJobs = nodeStatus.get(node);
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
			}
		}
	}

	private void processQueuedCommands() {
		List<Command> finishedCommands = new ArrayList<Command>();
		for (Command command : queuedCommands.keySet()) {
			int queuedTime = queuedCommands.get(command);
			if (time - queuedTime > command.getTimeToExecute()) {
				finishedCommands.add(command);
			}
		}
		for (Command command : finishedCommands) {
			queuedCommands.remove(command);
			command.execute(nodeStatus, jobStatus);
		}
	}

	private boolean finished() {
		return scheduler.isFinished();
	}

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("java Simulator [sample load file]");
		}
		String traceFile = "/Users/himanshu/Downloads/DAS2/anon_jobs.gwf";
		TraceList trace = new TraceList(traceFile, 5000, "gwf");
		List<Node> nodes = new ArrayList<Node>();
		int numNodes = 1000;
		for (int i = 0; i < numNodes; i++) {
			nodes.add(new SimpleNode("" + i));
		}
		Scheduler scheduler = new SleepScheduler(trace.getTracelist(), nodes, 5);
		Simulator simulator = new Simulator(scheduler);
		simulator.simulateUntilFinish();
	}
}