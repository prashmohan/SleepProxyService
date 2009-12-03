package node;

public interface Node {
	
	public String getNodeId();
	/*
	public int getCpuUsage(TraceJob job, List<TraceJob> jobs);
	public int getMemUsage(TraceJob job, List<TraceJob> jobs);
	public int getDiskUsage(TraceJob job, List<TraceJob> jobs);
	public int getNetworkUsage(TraceJob job, List<TraceJob> jobs);
	*/
	public void setState(PowerState s);
	public PowerState getState();
	
	enum PowerState {
		ON,
		OFF,
		SLEEP
	}

	public boolean isAvailable();

	public boolean isSleeping();

	public void setAvailable(boolean b);
}

