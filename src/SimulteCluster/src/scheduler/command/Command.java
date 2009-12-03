package scheduler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import job.SimulatedJob;
import node.Node;

public abstract class Command {
	protected final Node node;

	public Command(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}

	abstract public int getTimeToExecute();

	public abstract void execute(Map<Node, ArrayList<SimulatedJob>> nodeStatus,
			List<SimulatedJob> jobStatus);
}
