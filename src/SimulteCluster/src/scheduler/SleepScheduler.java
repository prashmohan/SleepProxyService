package scheduler;

import java.util.List;

import node.Node;

import scheduler.nodeproxy.SleepProxy;
import trace.TraceJob;

public class SleepScheduler extends SimpleScheduler {

	public SleepScheduler(List<TraceJob> jobs, List<Node> nodes,
			int generationTime) {
		super(jobs, nodes, generationTime);
		// change the node proxy to the SleepProxy
		nodeProxy = new SleepProxy(this.nodes);
	}
}