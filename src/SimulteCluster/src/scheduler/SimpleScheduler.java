package scheduler;

import java.util.ArrayList;
import java.util.List;

import node.Node;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.nodeproxy.NodeProxy;
import scheduler.nodeproxy.SimpleNodeProxy;
import trace.TraceJob;

public class SimpleScheduler implements Scheduler {

	protected List<TraceJob> jobs;
	protected List<SimulatedJob> queuedJobs;
	protected List<Node> nodes;
	protected int generationTime;
	protected int lastScheduledTime;
	protected int timeDifference;
	protected NodeProxy nodeProxy;
	protected int numJobs;

	public SimpleScheduler(List<TraceJob> jobs, List<Node> nodes,
			int generationTime) {
		this.jobs = new ArrayList<TraceJob>(jobs);
		numJobs = jobs.size();
		queuedJobs =  new ArrayList<SimulatedJob>();
		this.nodes = nodes;
		nodeProxy = new SimpleNodeProxy(nodes);
		this.generationTime = generationTime;
		this.lastScheduledTime = 0;

		if (jobs.size() > 0) {
			timeDifference = jobs.get(0).getStartTime();
		}
	}

	@Override
	public List<Command> getCommands(int time) {
		if (time - lastScheduledTime < generationTime) {
			return null;
		}
		lastScheduledTime = time;

		if (queuedJobs.size() > 0) {
			System.out.print("");
		}
		
		while (jobs.size() > 0 && jobs.get(0).getStartTime() < (time + timeDifference)) {
			TraceJob job = jobs.remove(0);
			for (int i = 0; i < job.getProcsRequired(); i++) {
				SimulatedJob simJob = new SimulatedJob(time, job);
				queuedJobs.add(simJob);
			}
		}
		
		List<Node> nodesAvailable = nodeProxy
				.getAvailableNodes(queuedJobs.size(), time);
		
		List<Command> commands = new ArrayList<Command>();
		
		while (queuedJobs.size() > 0 && nodesAvailable.size() > 0) {
			SimulatedJob job = queuedJobs.remove(0);
			Node node = nodesAvailable.remove(0);
			ScheduleCommand com = new ScheduleCommand(node, job, time);
			commands.add(com);
		}
		
		commands.addAll(nodeProxy.getCommands(time));
		return commands;
	}

	@Override
	public boolean isFinished() {
		return jobs.isEmpty() && queuedJobs.isEmpty();
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public int numJobs() {
		return numJobs;
	}
}