package scheduler;

import java.util.ArrayList;
import java.util.List;

import node.Node;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.command.WakeCommand;
import scheduler.nodeproxy.SleepProxy;
import trace.TraceJob;

public class SleepScheduler extends SimpleScheduler {

	public SleepScheduler(List<TraceJob> jobs, List<Node> nodes,
			int generationTime) {
		super(jobs, nodes, generationTime);
		nodeProxy = new SleepProxy(this.nodes);
	}
}