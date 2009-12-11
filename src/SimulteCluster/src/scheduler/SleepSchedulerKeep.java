package scheduler;

import java.util.List;

import node.Node;

import scheduler.nodeproxy.SleepProxy;
import scheduler.nodeproxy.SleepProxyKeep;
import trace.TraceJob;

public class SleepSchedulerKeep extends SimpleScheduler {

	public SleepSchedulerKeep(List<TraceJob> jobs, List<Node> nodes,
			int generationTime, double perKeep) {
		super(jobs, nodes, generationTime);
		nodeProxy = new SleepProxyKeep(this.nodes, perKeep);
	}
}