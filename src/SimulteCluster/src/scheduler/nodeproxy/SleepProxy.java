package scheduler.nodeproxy;

import java.util.ArrayList;
import java.util.List;
import scheduler.command.Command;
import scheduler.command.SleepCommand;
import scheduler.command.WakeCommand;

import node.Node;

public class SleepProxy implements NodeProxy {

	private List<Node> nodes;
	private List<Node> sleepingNodes;
	private List<Node> wakingNodes;
	private List<Node> onNodes;

	private List<Command> commands;

	public SleepProxy(List<Node> nodes) {
		this.nodes = nodes;
		wakingNodes = new ArrayList<Node>();
		sleepingNodes = new ArrayList<Node>();
		onNodes = new ArrayList<Node>();
		commands = new ArrayList<Command>();

		for (Node node : nodes) {
			if (node.isOn()) {
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
		for (Node node : onNodes) {
			if (node.isOn() && node.getNumberOfJobs(time) == 0) {
				commands.add(new SleepCommand(node, time));
				nodesPutSleep.add(node);
			}
		}
		onNodes.removeAll(nodesPutSleep);
		sleepingNodes.addAll(nodesPutSleep);
		return commands;
	}

	@Override
	public List<Node> getAvailableNodes(int numNodesRequired, int time) {
		if (numNodesRequired == 0) {
			return new ArrayList<Node>();
		}

		updateWakingNodes();
		List<Node> availableNodes = new ArrayList<Node>();
		for (Node node : onNodes) {
			if (node.isOn() && node.getNumberOfJobs(time) == 0) {
				availableNodes.add(node);
			}
			if (availableNodes.size() >= numNodesRequired) {
				break;
			}
		}

		if (numNodesRequired > availableNodes.size()) {
			int numNodesToWake = numNodesRequired - availableNodes.size()
					- wakingNodes.size();
			addWakeCommands(numNodesToWake, time);
		}

		return availableNodes;
	}

	public void addWakeCommands(int numNodesToWake, int time) {
		if (numNodesToWake > sleepingNodes.size()) {
			numNodesToWake = sleepingNodes.size();
		}

		for (int i = 0; i < numNodesToWake; i++) {
			Node sleepingNode = sleepingNodes.get(i);
			commands.add(new WakeCommand(sleepingNode, time));
			wakingNodes.add(sleepingNode);
		}
		sleepingNodes = sleepingNodes.subList(numNodesToWake, sleepingNodes
				.size());
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
}
