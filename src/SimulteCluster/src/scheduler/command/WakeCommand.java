package scheduler.command;

import java.util.List;
import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class WakeCommand extends Command {

	int timeToWake;
	
	public WakeCommand(Node node, int startTime) {
		super(node, startTime);
		timeToWake = node.getTimeToWake();
	}

	@Override
	public void execute(List<SimulatedJob> jobStatus, int time) {
		if (time - startTime == 0)
			node.setState(PowerState.WAKING);
		
		if (time - startTime == timeToWake)
			node.setState(PowerState.ON);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > timeToWake;
	}

	@Override
	public boolean shouldExecute(int time) {
		int timeDif = time - startTime;
		return timeDif == 0 || timeDif == timeToWake;
	}

	public String toString() {
		return "Wake(" + startTime + "): node " + node.getNodeId();
	}
}
