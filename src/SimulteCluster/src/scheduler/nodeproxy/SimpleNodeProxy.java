package scheduler.nodeproxy;

import java.util.ArrayList;
import java.util.List;

import node.Node;
import scheduler.command.Command;

public class SimpleNodeProxy implements NodeProxy {

	private List<Node> nodes;

	public SimpleNodeProxy(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public List<Node> getAvailableNodes(int numNodesRequired, int time) {
		List<Node> nodesAvailable = new ArrayList<Node>();
		for (Node node : nodes) {
			if (node.isAvailable()) {
				nodesAvailable.add(node);
			}
			if (nodesAvailable.size() >= numNodesRequired) {
				break;
			}
		}
	
		return nodesAvailable;
	}

	@Override
	public List<Command> getCommands(int time) {
		return new ArrayList<Command>();
	}

}
