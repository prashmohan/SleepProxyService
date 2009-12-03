package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;
import node.Node.PowerState;

public class WakeCommand extends Command {

	public WakeCommand(Node node) {
		super(node);
	}

	@Override
	public void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus) {
		assert(nodeStatus.containsKey(node));
		node.setState(PowerState.ON);
	}

	@Override
	public int getTimeToExecute() {
		return 6;
	}

}
