package node;

import java.util.List;

import job.SimulatedJob;

public interface Node {
	
	public String getNodeId();
	public void setState(PowerState s);
	public PowerState getState();
	int getNumberOfJobs(int time);
	public void addJob(SimulatedJob job);
	public void incNumScheduledJobs();
	public List<SimulatedJob> getJobs();
	public int getTimeToSleep();
	public int getTimeToWake();
	
	public boolean isOn();
	public boolean isSleeping();

	enum PowerState {
		ON,
		OFF,
		SLEEP,
		WAKING
	}

}

