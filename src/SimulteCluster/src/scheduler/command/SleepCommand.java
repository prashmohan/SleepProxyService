package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class SleepCommand extends Command {

	public SleepCommand(Node node, int startTime) {
		super(node, startTime);
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus, int time) {
		assert(nodeStatus.containsKey(node) && nodeStatus.get(node).isEmpty());
		node.setState(PowerState.SLEEP);
	}

	@Override
	public boolean isFinished(int time) {
		return time - startTime > 6;
	}

	@Override
	public boolean shouldExecute(int time) {
		return time - startTime == 6;
	}
	
	public String toString() {
		return "Sleep(" + startTime + "): node " + node.getNodeId();
	}
}
