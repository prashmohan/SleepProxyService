package node;

public class SimpleNode implements Node {

	PowerState state;
	final String nodeId;
	boolean isAvailable;
	
	public SimpleNode(String nodeId) {
		this.nodeId = nodeId;
		state = PowerState.ON;
		isAvailable = true;
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
	public boolean isAvailable() {
		return isAvailable && state == PowerState.ON;
	}

	@Override
	public void setAvailable(boolean b) {
		isAvailable = b;
	}
	
	@Override
	public boolean isOn() {
		return state == PowerState.ON;
	}
	
	@Override
	public boolean isSleeping() {
		return state == PowerState.SLEEP;
	}
}
