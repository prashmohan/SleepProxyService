package scheduler;

import java.util.ArrayList;
import java.util.List;

import job.GangliaSimulatedJob;
import job.SimulatedJob;
import node.Node;

import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.nodeproxy.NodeProxy;
import trace.TraceJob;
import trace.ganglia.GangliaTrace;

public class GangliaScheduler extends SimpleScheduler {

	GangliaTrace gangliaTrace;
	
	public GangliaScheduler(List<TraceJob> jobs, NodeProxy nodeProxy,
			int generationTime, String dir) {
		super(jobs, nodeProxy, generationTime);
		gangliaTrace = new GangliaTrace(dir);
	}

	@Override
	public List<Command> getCommands(int time) {
		// if generation has passed, then continue
		if (time - lastScheduledTime < generationTime) {
			return null;
		}
		lastScheduledTime = time;

		// remove jobs from the list of jobs from traces, if the start time
		// is past the current time
		while (jobs.size() > 0 && jobs.get(0).getStartTime() < (time + timeDifference)) {
			TraceJob job = jobs.remove(0);
			// create the required simulator jobs and enqueue them onto the
			// queuedJobs list
			for (int i = 0; i < job.getProcsRequired(); i++) {
				SimulatedJob simJob = new GangliaSimulatedJob(time, job, gangliaTrace);
				queuedJobs.add(simJob);
			}
		}
		
		// node proxy provides a list of nodes to use to schedule jobs
		List<Node> nodesAvailable = nodeProxy
				.getAvailableNodes(queuedJobs.size(), time);
		
		List<Command> commands = new ArrayList<Command>();
		
		// add commands to schedule jobs on the given nodes
		while (queuedJobs.size() > 0 && nodesAvailable.size() > 0) {
			SimulatedJob job = queuedJobs.remove(0);
			Node node = nodesAvailable.remove(0);
			ScheduleCommand com = new ScheduleCommand(node, job, time);
			commands.add(com);
		}
		
		// add commands to put nodes to sleep / wake up nodes
		// this must be done after the jobs are scheduled
		commands.addAll(nodeProxy.getCommands(time));
		return commands;
	}
}
