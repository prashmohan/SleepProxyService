package scheduler.command;

import java.util.List;

import job.SimulatedJob;
import node.Node;

public class ScheduleCommand extends Command {

	private SimulatedJob job;

	public ScheduleCommand(Node node, SimulatedJob job, int startTime) {
		super(node, startTime);
		this.job = job;
	}

	@Override
	public void execute(List<SimulatedJob> jobStatus, int time) {
		// should only schedule on nodes without any jobs
		assert node.getNumberOfJobs(time) == 0;
		node.addJob(job);
		jobStatus.add(job);
		// update the job start time, which is different from the submit time
		job.start(time);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > 0;
	}

	@Override
	public boolean shouldExecute(int time) {
		return time - startTime == 0;
	}
	
	public String toString() {
		return "Schedule(" + startTime + "): node " + node.getNodeId() + ",job " + job.getJobId();
	}
}
