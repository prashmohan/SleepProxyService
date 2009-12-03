package job;

import trace.TraceJob;

public class SimulatedJob {

	private TraceJob traceJob;
	private int startTime;
	private int requiredTime;

	public SimulatedJob(TraceJob traceJob) {
		this.traceJob = traceJob;
		requiredTime = traceJob.getEndTime() - traceJob.getStartTime();
		startTime = -1;
	}

	public boolean isFinished(int time) {
		return startTime != -1 && time - startTime > requiredTime;
	}

	public void update(int time) {
		
	}

}
