package job;

class Status {
/*
	private int cpu, mem, disk, network;
	private boolean isFinished;
	private TraceJob job;
	
	public Status(TraceJob job) {
		this.job = job;
		cpu = mem = disk = network = 0;
		isFinished = false;
	}
	
	public boolean isFinished() {
		if (!isFinished &&
				job.getCpu().getRequiredAmount() < cpu &&
				job.getMem().getRequiredAmount() < mem &&
				job.getDisk().getRequiredAmount() < disk &&
				job.getNetwork().getRequiredAmount() < network ) {
			isFinished = true;
		}
		return isFinished;
	}
	
	public void update(int time) {
		// TODO Auto-generated method stub
		
	}

	public void update(int cpuUpdate, int memUpdate, int diskUpdate, int networkUpdate) {
		if (cpuUpdate > job.getCpu().getMaxRate()) {
			cpuUpdate = job.getCpu().getMaxRate();
		}
		if (memUpdate > job.getMem().getMaxRate()) {
			memUpdate = job.getMem().getMaxRate();
		}
		if (diskUpdate > job.getDisk().getMaxRate()) {
			diskUpdate = job.getDisk().getMaxRate();
		}
		if (networkUpdate > job.getNetwork().getMaxRate()) {
			networkUpdate = job.getNetwork().getMaxRate();
		}
		
		cpu += cpuUpdate;
		mem += memUpdate;
		disk += diskUpdate;
		network += networkUpdate;
	}
*/
	
}
