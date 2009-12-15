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
		// go through the list of nodes and find nodes which have no jobs
		// and are on
		for (Node node : nodes) {
			if (node.isOn() && node.getNumberOfJobs(time) == 0) {
				nodesAvailable.add(node);
			}
			// once enough nodes are found, break from the loop
			if (nodesAvailable.size() >= numNodesRequired) {
				break;
			}
		}
		return nodesAvailable;
	}

	@Override
	// no commands since no nodes are put to sleep
	public List<Command> getCommands(int time) {
		return new ArrayList<Command>();
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

}
