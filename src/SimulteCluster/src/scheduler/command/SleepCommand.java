package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class SleepCommand extends Command {

	public SleepCommand(Node node) {
		super(node);
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus) {
		assert(nodeStatus.containsKey(node) && nodeStatus.get(node).isEmpty());
		node.setState(PowerState.SLEEP);
	}

	@Override
	public int getTimeToExecute() {
		return 6;
	}

}
