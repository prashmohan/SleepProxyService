package scheduler;

import java.util.ArrayList;
import java.util.List;

import node.Node;

import job.SimulatedJob;
import scheduler.command.Command;
import scheduler.command.ScheduleCommand;
import scheduler.nodeproxy.NodeProxy;
import scheduler.nodeproxy.SimpleNodeProxy;
import trace.TraceJob;

public class SimpleScheduler implements Scheduler {

	// original list of jobs to schedule
	protected List<TraceJob> jobs;
	
	// list of simulated jobs which can be executed on a node
	// because the start time has passed
	protected List<SimulatedJob> queuedJobs;
	
	// time between batches of jobs being scheduled
	protected int generationTime;
	
	// last time the scheduler was called
	protected int lastScheduledTime;
	
	// time difference between simulated time and the time of the trace
	protected int timeDifference;
	
	// node proxy which provides nodes to execute jobs on
	protected NodeProxy nodeProxy;

	public SimpleScheduler(List<TraceJob> jobs, NodeProxy nodeProxy,
			int generationTime) {
		// jobs list is cloned since jobs are removed from the list as
		// they are executed
		this.jobs = new ArrayList<TraceJob>(jobs);
		queuedJobs =  new ArrayList<SimulatedJob>();
		
		this.nodeProxy = nodeProxy;
		this.generationTime = generationTime;
		this.lastScheduledTime = 0;

		// trace of jobs is assumed to be chronologically sorted, so the
		// first job in the trace starts at simulator time 0
		if (jobs.size() > 0) {
			timeDifference = jobs.get(0).getStartTime();
		}
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
				SimulatedJob simJob = new SimulatedJob(time, job);
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

	@Override
	// scheduler is finished if there are no more trace jobs to 
	// simulate and the queue jobs have all been scheduled to nodes
	public boolean isFinished() {
		return jobs.isEmpty() && queuedJobs.isEmpty();
	}

	@Override
	public List<Node> getNodes() {
		return nodeProxy.getNodes();
	}

}