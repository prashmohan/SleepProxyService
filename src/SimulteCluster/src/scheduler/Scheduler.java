package scheduler;

import java.util.List;

import node.Node;

import scheduler.command.Command;

public interface Scheduler {
	
	abstract public List<Command> getCommands(int time) ;

	abstract public boolean isFinished();

	public abstract List<Node> getNodes();
}