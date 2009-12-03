package scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scheduler.command.Command;

import node.Node;
import node.NodeStatus;

public class SleepProxy {
	private Map<Node, NodeStatus> nodes;

	public SleepProxy(List<Node> nodes) {
		this.nodes = new HashMap<Node, NodeStatus>();
		for(Node node : nodes) {
			this.nodes.put(node, new NodeStatus());
		}
	}
	
	public List<Node> getNodes(int numNodes) {
		return new ArrayList<Node>();
	}
	
	public List<Command> getCommands(int time) {
		return new ArrayList<Command>();
	}
}