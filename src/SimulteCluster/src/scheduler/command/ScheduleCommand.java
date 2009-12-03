package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;

public class ScheduleCommand extends Command {

	private SimulatedJob job;

	public ScheduleCommand(Node node, SimulatedJob job) {
		super(node);
		this.job = job;
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus) {
		ArrayList<SimulatedJob> nodeJobs = nodeStatus.get(node);
		nodeJobs.add(job);
		jobStatus.add(job);
	}

	@Override
	public int getTimeToExecute() {
		return 4;
	}


}
