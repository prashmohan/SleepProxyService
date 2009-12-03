package scheduler;

import java.util.ArrayList;
import java.util.List;

import node.Node;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.command.WakeCommand;
import trace.TraceJob;

public class SleepScheduler implements Scheduler {

	private List<TraceJob> jobs;
	private List<Node> nodes;
	private int generationTime;
	private int lastScheduledTime;

	public SleepScheduler(List<TraceJob> jobs, List<Node> nodes,
			int generationTime) {
		this.jobs = new ArrayList<TraceJob>(jobs);
		this.nodes = nodes;
		this.generationTime = generationTime;
		this.lastScheduledTime = 0;
	}

	@Override
	public List<Command> getCommands(int time) {
		if (time - lastScheduledTime < generationTime) {
			return new ArrayList<Command>();
		}
		lastScheduledTime = time;

		int numJobsCanIssue = 0;
		while (!jobs.isEmpty()
				&& jobs.get(numJobsCanIssue).getSubmitTime() < time) {
			numJobsCanIssue++;
		}

		int numNodesRequired = 0;
		for (int i = 0; i < numJobsCanIssue; i++) {
			numNodesRequired += jobs.get(i).getNodesRequired();
		}

		List<Node> nodesAvailable = new ArrayList<Node>();
		List<Node> nodesSleeping = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.isAvailable()) {
				nodesAvailable.add(node);
			}
			if (node.isSleeping()) {
				nodesSleeping.add(node);
			}
		}

		int numNodesToWake = numNodesRequired - nodesAvailable.size();
		if (numNodesToWake > nodesSleeping.size()) {
			numNodesToWake = nodesSleeping.size();
		}

		List<Command> commands = new ArrayList<Command>();
		for (int i = 0; i < numNodesToWake; i++) {
			commands.add(new WakeCommand(nodesSleeping.get(i)));
		}

		while (jobs.size() > 0
				&& nodesAvailable.size() >= jobs.get(0).getNodesRequired()) {
			TraceJob job = jobs.remove(0);
			for (int i = 0; i < job.getNodesRequired(); i++) {
				SimulatedJob simJob = new SimulatedJob(job);
				Node node = nodesAvailable.remove(0);
				node.setAvailable(false);
				ScheduleCommand com = new ScheduleCommand(node, simJob);
				commands.add(com);
			}
		}
		return commands;
	}

	@Override
	public boolean isFinished() {
		return jobs.isEmpty();
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}
}
