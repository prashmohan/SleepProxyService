package scheduler;

import java.util.ArrayList;
import java.util.List;

import node.Node;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.command.WakeCommand;
import scheduler.nodeproxy.NodeProxy;
import scheduler.nodeproxy.SimpleNodeProxy;
import trace.TraceJob;

public class SimpleScheduler implements Scheduler {

	protected List<TraceJob> jobs;
	protected List<Node> nodes;
	protected int generationTime;
	protected int lastScheduledTime;
	protected int timeDifference;
	protected NodeProxy nodeProxy;

	public SimpleScheduler(List<TraceJob> jobs, List<Node> nodes,
			int generationTime) {
		this.jobs = new ArrayList<TraceJob>(jobs);
		this.nodes = nodes;
		nodeProxy = new SimpleNodeProxy(nodes);
		this.generationTime = generationTime;
		this.lastScheduledTime = 0;

		if (jobs.size() > 0) {
			timeDifference = jobs.get(0).getSubmitTime();
		}
	}

	@Override
	public List<Command> getCommands(int time) {
		if (time - lastScheduledTime < generationTime) {
			return null;
		}
		lastScheduledTime = time;

		int numJobsCanIssue = 0;
		while (numJobsCanIssue + 1 <= jobs.size()
				&& jobs.get(numJobsCanIssue).getSubmitTime() < (time + timeDifference)) {
			numJobsCanIssue++;
		}

		int numNodesRequired = 0;
		for (int i = 0; i < numJobsCanIssue; i++) {
			numNodesRequired += jobs.get(i).getNodesRequired();
		}

		List<Node> nodesAvailable = nodeProxy
				.getAvailableNodes(numNodesRequired, time);
		
		List<Command> commands = new ArrayList<Command>();
		
		while (jobs.size() > 0 && numJobsCanIssue > 0
				&& nodesAvailable.size() >= jobs.get(0).getNodesRequired()) {
			TraceJob job = jobs.remove(0);
			numJobsCanIssue--;
			for (int i = 0; i < job.getNodesRequired(); i++) {
				SimulatedJob simJob = new SimulatedJob(job);
				Node node = nodesAvailable.remove(0);
				node.setAvailable(false);
				ScheduleCommand com = new ScheduleCommand(node, simJob, time);
				commands.add(com);
			}
		}
		
		commands.addAll(nodeProxy.getCommands(time));
		
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