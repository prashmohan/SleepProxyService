package node;

public class SimpleNode implements Node {

	PowerState state;
	final int cpuResource, memResource, diskResource, networkResource;
	final String nodeId;
	boolean isAvailable;
	
	public SimpleNode(String nodeId) {
		this.cpuResource = 1;
		this.memResource = 1;
		this.diskResource = 1;
		this.networkResource = 1;

		this.nodeId = nodeId;
		state = PowerState.ON;
		isAvailable = true;
	}
/*
	@Override
	public int getCpuUsage(TraceJob job, List<TraceJob> jobs) {
		if (state == PowerState.ON && jobs.contains(job)) {
			return cpuResource / jobs.size();
		}
		return 0;
	}

	@Override
	public int getDiskUsage(TraceJob job, List<TraceJob> jobs) {
		if (state == PowerState.ON && jobs.contains(job)) {
			return diskResource / jobs.size();
		}
		return 0;
	}

	@Override
	public int getMemUsage(TraceJob job, List<TraceJob> jobs) {
		if (state == PowerState.ON && jobs.contains(job)) {
			return memResource / jobs.size();
		}
		return 0;
	}

	@Override
	public int getNetworkUsage(TraceJob job, List<TraceJob> jobs) {
		if (state == PowerState.ON && jobs.contains(job)) {
			return networkResource / jobs.size();
		}
		return 0;
	}
*/
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
	public boolean isAvailable() {
		return isAvailable && state == PowerState.ON;
	}

	@Override
	public boolean isSleeping() {
		return state == PowerState.SLEEP;
	}

	@Override
	public void setAvailable(boolean b) {
		isAvailable = b && state == PowerState.ON;
	}
}
