package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;

public class ScheduleCommand extends Command {

	private SimulatedJob job;

	public ScheduleCommand(Node node, SimulatedJob job, int startTime) {
		super(node, startTime);
		this.job = job;
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus, int time) {
		ArrayList<SimulatedJob> nodeJobs = nodeStatus.get(node);
		nodeJobs.add(job);
		jobStatus.add(job);
		job.start(time);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > 4;
	}

	@Override
	public boolean shouldExecute(int time) {
		return time - startTime == 4;
	}
	
	public String toString() {
		return "Schedule(" + startTime + "): node " + node.getNodeId() + "," + job.getJobId();
	}
}
