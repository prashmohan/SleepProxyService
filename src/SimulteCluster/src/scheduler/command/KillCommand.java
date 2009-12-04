package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import trace.TraceJob;

import job.SimulatedJob;
import node.Node;

public class KillCommand extends Command {

	private TraceJob job;
	
	public KillCommand(Node node, TraceJob job, int startTime) {
		super(node, startTime);
		this.job = job;
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus, int time) {
		ArrayList<SimulatedJob> nodeJobs = nodeStatus.get(node);
		nodeJobs.remove(job);
		jobStatus.remove(job);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > 2;
	}

	@Override
	public boolean shouldExecute(int time) {
		return time - startTime == 2;
	}

	public String toString() {
		return "Kill(" + startTime + "): node " + node.getNodeId() + "," + job.getJobId();
	}
}
