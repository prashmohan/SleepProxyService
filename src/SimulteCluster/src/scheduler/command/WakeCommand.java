package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class WakeCommand extends Command {

	public WakeCommand(Node node, int startTime) {
		super(node, startTime);
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus, int time) {
		assert(nodeStatus.containsKey(node));
		if (time - startTime == 0)
			node.setState(PowerState.WAKING);
		
		if (time - startTime == 6)
			node.setState(PowerState.ON);
	}

	
	
	@Override
	public boolean isFinished(int time) {
		return time - startTime > 6;
	}

	@Override
	public boolean shouldExecute(int time) {
		int timeDif = time - startTime;
		return timeDif == 0 || timeDif == 6;
	}

	public String toString() {
		return "Wake(" + startTime + "): node " + node.getNodeId();
	}
}
