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
	 
	// get the energy used in watts
	public int getEnergyUsed(int time);
	
	public boolean isOn();
	public boolean isSleeping();
	public boolean isWaking();

	// add a job to the list of jobs running on this node
	public void addJob(SimulatedJob job);
	// increment the time this node is on
	public void updateStats(int time, int timeLength);
	
	public int getUpTime();
	
	public double getTotalEnergyUsed();

	enum PowerState {
		ON,
		OFF,
		SLEEP,
		WAKING
	}

}

