package node;

import java.util.ArrayList;
import java.util.List;

import job.SimulatedJob;

public class SimpleNode implements Node {

	PowerState state;
	final String nodeId;
	boolean isAvailable;
	List<SimulatedJob> jobs;
	int timeToSleep, timeToWake;
	int numScheduledJobs;
	
	public SimpleNode(String nodeId, int timeToSleep, int timeToWake) {
		this.nodeId = nodeId;
		this.timeToSleep = timeToSleep;
		this.timeToWake = timeToWake;
		state = PowerState.ON;
		isAvailable = true;
		numScheduledJobs = 0;
		jobs = new ArrayList<SimulatedJob>();
	}

	@Override
	public PowerState getState() {
		return state;
	}

	@Override
	public void setState(PowerState s) {
		state = s;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}
	
	@Override
	public boolean isOn() {
		return state == PowerState.ON;
	}
	
	@Override
	public boolean isSleeping() {
		return state == PowerState.SLEEP;
	}

	@Override
	public int getNumberOfJobs(int time) {
		List<SimulatedJob> finishedJobs = new ArrayList<SimulatedJob>();
		for (SimulatedJob job : jobs) {
			if (job.isFinished(time)) {
				
			}
		}
		jobs.removeAll(finishedJobs);
		return jobs.size() + numScheduledJobs;
	}

	@Override
	public int getTimeToSleep() {
		return timeToSleep;
	}

	@Override
	public int getTimeToWake() {
		return timeToWake;
	}

	@Override
	public void addJob(SimulatedJob job) {
		jobs.add(job);
		numScheduledJobs--;
	}

	@Override
	public List<SimulatedJob> getJobs() {
		return jobs;
	}

	@Override
	public void incNumScheduledJobs() {
		numScheduledJobs++;
	}
}
