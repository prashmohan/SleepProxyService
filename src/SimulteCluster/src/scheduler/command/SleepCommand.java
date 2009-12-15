package scheduler.command;

import java.util.List;
import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class SleepCommand extends Command {

	int timeToSleep;
	
	public SleepCommand(Node node, int startTime) {
		super(node, startTime);
		timeToSleep = node.getTimeToSleep();
	}

	@Override
	public void execute(List<SimulatedJob> jobStatus, int time) {
		// should only put to sleep nodes without any jobs
		assert(node.getJobs().isEmpty());
		node.setState(PowerState.SLEEP);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > timeToSleep;
	}

	@Override
	public boolean shouldExecute(int time) {
		return time - startTime == timeToSleep;
	}
	
	public String toString() {
		return "Sleep(" + startTime + "): node " + node.getNodeId();
	}
}
