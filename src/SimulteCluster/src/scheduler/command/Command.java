package scheduler.command;

import java.util.List;
import job.SimulatedJob;
import node.Node;

public abstract class Command {
	protected final Node node;
	protected final int startTime;
	
	public Command(Node node, int startTime) {
		this.node = node;
		this.startTime = startTime;
	}
	
	public Node getNode() {
		return node;
	}

	public abstract void execute(List<SimulatedJob> jobStatus, int time);

	public abstract boolean isFinished(int time);
	
	public abstract boolean shouldExecute(int time);
	
	public abstract String toString();
}
