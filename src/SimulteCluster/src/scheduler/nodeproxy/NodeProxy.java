package scheduler.nodeproxy;

import java.util.List;

import scheduler.command.Command;

import node.Node;

public interface NodeProxy {

	public List<Command> getCommands(int time);
	public List<Node> getAvailableNodes(int numNodesRequired, int time);
}