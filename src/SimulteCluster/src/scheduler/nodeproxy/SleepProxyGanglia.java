package scheduler.nodeproxy;

import java.util.ArrayList;
import java.util.List;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.SleepCommand;
import scheduler.command.WakeCommand;

import node.Node;

public class SleepProxyGanglia implements NodeProxy {

	private List<Node> nodes;
	
	private Node masterNode; 
	
	// nodes which are sleeping
	private List<Node> sleepingNodes;
	
	// nodes which are waking
	private List<Node> wakingNodes;
	
	// nodes that are on
	private List<Node> onNodes;
	
	// nodes that are on but do not have a job running
	private List<Node> availableNodes;
	
	// commands that need to be executed
	private List<Command> commands;

	// maximum cpu usage that a node can have to be considered
	// available
	private double cpuThreshold;
	
	public SleepProxyGanglia(List<Node> nodes, double cpuThreshold) {
		this.nodes = nodes;
		if (!nodes.isEmpty()) {
			masterNode = nodes.get(0);
		}
		this.cpuThreshold = cpuThreshold;
		wakingNodes = new ArrayList<Node>();
		sleepingNodes = new ArrayList<Node>();
		onNodes = new ArrayList<Node>();
		availableNodes = new ArrayList<Node>();
		commands = new ArrayList<Command>();
		
		// all nodes are initially assumed to be on and
		// not running any jobs
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
		// add all sleep commands to the list of current commands
		commands.addAll(getSleepCommands(time));
		List<Command> returnCommands = commands;
		// reset the commands list
		commands = new ArrayList<Command>();
		return returnCommands;
	}

	public List<Command> getSleepCommands(int time) {
		List<Command> commands = new ArrayList<Command>();
		List<Node> nodesPutSleep = new ArrayList<Node>();
		// the available nodes are ones which are not being utilized
		for (Node node : availableNodes) {
			// sanity check
			if (node.isOn() && node.getNumberOfJobs(time) == 0
					&& node != masterNode) {
				// put node to sleep
				commands.add(new SleepCommand(node, time));
				nodesPutSleep.add(node);
			}
		}
		// update appropriate lists
		onNodes.removeAll(nodesPutSleep);
		availableNodes.removeAll(nodesPutSleep);
		sleepingNodes.addAll(nodesPutSleep);
		return commands;
	}

	@Override
	// the main aspect of the node proxy. when the scheduler needs nodes,
	// it calls this function. this function returns available nodes, but
	// also starts to wake nodes if there are not enough nodes currently 
	// available
	public List<Node> getAvailableNodes(int numNodesRequired, int time) {
		// update the waking nodes list
		updateWakingNodes();
		// update the available nodes list
		updateAvailableNodes(time);

		if (numNodesRequired == 0) {
			return new ArrayList<Node>();
		}
		
		// get a list of the current nodes that are available to be used
		List<Node> returnNodes = new ArrayList<Node>();
		for (Node node : availableNodes) {
			returnNodes.add(node);
			if (returnNodes.size() >= numNodesRequired)
				break;
		}

		// calculate number of nodes that need to be awakened
		if (numNodesRequired > returnNodes.size()) {
			int numNodesToWake = numNodesRequired - returnNodes.size()
					- wakingNodes.size();
			addWakeCommands(numNodesToWake, time);
		}

		// update available nodes list
		return returnNodes;
	}

	public void addWakeCommands(int numNodesToWake, int time) {
		if (numNodesToWake <= 0) {
			return;
		}
		
		if (numNodesToWake > sleepingNodes.size()) {
			numNodesToWake = sleepingNodes.size();
		}

		// add commands to wake nodes
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
		availableNodes.clear();
		for (Node node : onNodes) {
			if (node.isOn()) {
				double cpu = 0;
				for (SimulatedJob job : node.getJobs()) {
					cpu += job.getCpu(time);
				}
				
				if (cpu < cpuThreshold) {
					availableNodes.add(node);
				}
			}
		}
	}
	
	@Override
	public List<Node> getNodes() {
		return nodes;
	}

}
