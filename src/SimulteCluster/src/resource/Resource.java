package resource;

public class Resource {
	final private String resourceId, units;
	
	final private int requiredAmount, maxRate;
	
	public Resource(String resourceId, String units, int requiredAmount,
			int maxRate) {
		super();
		this.resourceId = resourceId;
		this.units = units;
		this.requiredAmount = requiredAmount;
		this.maxRate = maxRate;
	}

	public int getMaxRate() {
		return maxRate;
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}
}
