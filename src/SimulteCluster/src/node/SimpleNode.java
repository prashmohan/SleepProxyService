package node;

import java.util.ArrayList;
import java.util.List;

import job.SimulatedJob;

public class SimpleNode implements Node {

	private PowerState state;
	private final String nodeId;
	private List<SimulatedJob> jobs;
	private int upTime;
	
	final private int timeToSleep, timeToWake;
	final private int numProcs;
	
	public SimpleNode(String nodeId, int timeToSleep, int timeToWake, int numProcs) {
		this.nodeId = nodeId;
		this.timeToSleep = timeToSleep;
		this.timeToWake = timeToWake;
		this.numProcs = numProcs;
		state = PowerState.ON;
		jobs = new ArrayList<SimulatedJob>();
		upTime = 0;
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
				finishedJobs.add(job);
			}
		}
		return jobs.size() - finishedJobs.size();
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
	}

	@Override
	public List<SimulatedJob> getJobs() {
		return jobs;
	}

	@Override
	public int getEnergyUsed(int time) {
		switch(state) {
		case OFF:
			return 0;
		case ON:
			return 50;
		case SLEEP:
			return 10;
		case WAKING:
			return 50;		
		}
		return -1;
	}
	
	@Override
	public int getUpTime() {
		return upTime;
	}

	@Override
	public void incUpTime() {
		upTime++;
	}
}
