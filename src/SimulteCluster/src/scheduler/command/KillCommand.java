package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import trace.TraceJob;

import job.SimulatedJob;
import node.Node;

public class KillCommand extends Command {

	private TraceJob job;
	
	public KillCommand(Node node, TraceJob job) {
		super(node);
		this.job = job;
	}

	

	@Override
	public int getTimeToExecute() {
		return 4;
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus) {
		ArrayList<SimulatedJob> nodeJobs = nodeStatus.get(node);
		nodeJobs.remove(job);
		jobStatus.remove(job);
	}

}
