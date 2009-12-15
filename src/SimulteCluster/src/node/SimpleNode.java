package node;

import java.util.ArrayList;
import java.util.List;

import job.SimulatedJob;

public class SimpleNode implements Node {

	private PowerState state;
	private final String nodeId;
	private List<SimulatedJob> jobs;
	// number of seconds this node has been on
	private int upTime;
	
	final private int timeToSleep, timeToWake;
	
	// energy in W
	final private int energyIdle, energyRunning, energyWaking, energySleep;
	// energy in W * sec
	private double totalEnergy;
	
	// should somehow be added, but currently each node is assumed to have 1 processer
	//final private int numProcs;
	
	public SimpleNode(String nodeId, int timeToSleep, int timeToWake, int numProcs,
			int energyIdle, int energyRunning, int energyWaking, int energySleep) {
		this.nodeId = nodeId;
		this.timeToSleep = timeToSleep;
		this.timeToWake = timeToWake;
		this.energyIdle = energyIdle;
		this.energyRunning = energyRunning;
		this.energyWaking = energyWaking;
		this.energySleep = energySleep;
			//this.numProcs = numProcs;
		state = PowerState.ON;
		jobs = new ArrayList<SimulatedJob>();
		upTime = 0;
		totalEnergy = 0;
	}
	
	// Constant values for parameters
	public SimpleNode(String nodeId) {
		this(nodeId, 9, 4, 1, 100, 130, 150, 4);
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
	public boolean isWaking() {
		return state == PowerState.WAKING;
	}

	
	@Override
	public boolean isSleeping() {
		return state == PowerState.SLEEP;
	}

	@Override
	public int getNumberOfJobs(int time) {
		// want to only count non-finished jobs, but cannot remove
		// the finished jobs from the list since this needs to be done
		// by the simulator.
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
			boolean hasJobs = false;
			for (SimulatedJob job : jobs) {
				if (!job.isFinished(time)) {
					hasJobs = true;
				}
			}
			if (hasJobs) {
				return energyRunning;
			} else {
				return energyIdle;
			}
		case SLEEP:
			return energySleep;
		case WAKING:
			return energyWaking;		
		}
		throw new RuntimeException("Unimplemented energy state.");
	}
	
	@Override
	public int getUpTime() {
		return upTime;
	}

	@Override
	public double getTotalEnergyUsed() {
		return totalEnergy;
	}
	
	@Override
	public void updateStats(int time, int timeLength) {
		if (state == PowerState.ON || state == PowerState.WAKING) {
			upTime += timeLength;
		}
		totalEnergy += getEnergyUsed(time);
	}

}
