package scheduler.nodeproxy;

import java.util.ArrayList;
import java.util.List;
import scheduler.command.Command;
import scheduler.command.SleepCommand;
import scheduler.command.WakeCommand;

import node.Node;

public class SleepProxyKeepOn implements NodeProxy {

	private List<Node> nodes;
	private Node masterNode;
	private List<Node> sleepingNodes;
	private List<Node> wakingNodes;
	private List<Node> onNodes;
	private List<Node> availableNodes;
	
	private List<Command> commands;
	int numKeepOn;
	
	// same as SleepProxy except that a certain threshold of nodes must be
	// kept on
	public SleepProxyKeepOn(List<Node> nodes, double perKeepOn) {
		this.nodes = nodes;
		if (!nodes.isEmpty()) {
			masterNode = nodes.get(0);
		}
		// number of nodes that must be kept on
		numKeepOn = (int)(perKeepOn * nodes.size());
		wakingNodes = new ArrayList<Node>();
		sleepingNodes = new ArrayList<Node>();
		onNodes = new ArrayList<Node>();
		availableNodes = new ArrayList<Node>();
		commands = new ArrayList<Command>();
		
		for (Node node : nodes) {
			if (node.isOn()) {
				availableNodes.add(node);
				onNodes.add(node);
			} else if (node.isSleeping()) {
				sleepingNodes.add(node);
			}
		}
	}

	@Override
	public List<Command> getCommands(int time) {
		commands.addAll(getSleepCommands(time));
		List<Command> returnCommands = commands;
		commands = new ArrayList<Command>();
		return returnCommands;
	}

	public List<Command> getSleepCommands(int time) {
		List<Command> commands = new ArrayList<Command>();
		List<Node> nodesPutSleep = new ArrayList<Node>();
		for (Node node : availableNodes) {
			// ensure that numKeep nodes are on
			if (onNodes.size() - nodesPutSleep.size() < numKeepOn) {
				break;
			}
			if (node.isOn() && node.getNumberOfJobs(time) == 0
					&& node != masterNode) {
				commands.add(new SleepCommand(node, time));
				nodesPutSleep.add(node);
			}
		}
		onNodes.removeAll(nodesPutSleep);
		availableNodes.removeAll(nodesPutSleep);
		sleepingNodes.addAll(nodesPutSleep);
		return commands;
	}

	@Override
	public List<Node> getAvailableNodes(int numNodesRequired, int time) {

		updateWakingNodes();
		updateAvailableNodes(time);
		
		// scheduler always requires numKeepOn nodes on
		if (onNodes.size() < numKeepOn) {
			numNodesRequired = Math.max(numNodesRequired, numKeepOn);
		}
		
		if (numNodesRequired == 0) {
			return new ArrayList<Node>();
		}
		
		List<Node> returnNodes = new ArrayList<Node>();
		for (Node node : availableNodes) {
			returnNodes.add(node);
			if (returnNodes.size() >= numNodesRequired)
				break;
		}

		if (numNodesRequired > returnNodes.size()) {
			int numNodesToWake = numNodesRequired - returnNodes.size()
					- wakingNodes.size();
			addWakeCommands(numNodesToWake, time);
		}

		availableNodes.removeAll(returnNodes);
		return returnNodes;
	}

	public void addWakeCommands(int numNodesToWake, int time) {
		if (numNodesToWake <= 0) {
			return;
		}
		
		if (numNodesToWake > sleepingNodes.size()) {
			numNodesToWake = sleepingNodes.size();
		}

		for (int i = 0; i < numNodesToWake; i++) {
			Node sleepingNode = sleepingNodes.get(i);
			commands.add(new WakeCommand(sleepingNode, time));
			wakingNodes.add(sleepingNode);
		}
		sleepingNodes.removeAll(wakingNodes);
	}

	private void updateWakingNodes() {
		List<Node> nodesTurnedOn = new ArrayList<Node>();
		for (Node node : wakingNodes) {
			if (node.isOn()) {
				nodesTurnedOn.add(node);
			}
		}

		wakingNodes.removeAll(nodesTurnedOn);
		onNodes.addAll(nodesTurnedOn);
	}
	
	private void updateAvailableNodes(int time) {	
		for (Node node : onNodes) {
			if (node.isOn() && node.getNumberOfJobs(time) == 0 && 
					!availableNodes.contains(node)) {
				availableNodes.add(node);
			}
		}
	}
	
	@Override
	public List<Node> getNodes() {
		return nodes;
	}

}
