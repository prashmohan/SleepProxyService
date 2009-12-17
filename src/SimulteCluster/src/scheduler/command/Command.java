package scheduler.command;

import java.util.List;
import job.SimulatedJob;
import node.Node;

// main class to pass info from the scheduler to the simulator
public abstract class Command {
	protected final Node node;
	
	// time command was issued
	protected final int startTime;
	
	public Command(Node node, int startTime) {
		this.node = node;
		this.startTime = startTime;
	}
	
	public Node getNode() {
		return node;
	}

	// called to edit the state of the simulator
	public abstract void execute(List<SimulatedJob> jobStatus, int time);

	public abstract boolean isFinished(int time);
	
	public abstract boolean shouldExecute(int time);
	
	public abstract String toString();
}
