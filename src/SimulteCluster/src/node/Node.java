package node;

import java.util.List;

import job.SimulatedJob;

public interface Node {
	
	public String getNodeId();
	public int getTimeToSleep();
	public int getTimeToWake();
	
	public void setState(PowerState s);

	public PowerState getState();
	public List<SimulatedJob> getJobs();
	public int getNumberOfJobs(int time);
	public int getEnergyUsed(int time);
	
	public boolean isOn();
	public boolean isSleeping();

	public void addJob(SimulatedJob job);
	public void incUpTime();
	
	public int getUpTime();
	
	enum PowerState {
		ON,
		OFF,
		SLEEP,
		WAKING
	}

}

