package job;

import trace.TraceJob;
import trace.ganglia.GangliaTrace;

public class GangliaSimulatedJob extends SimulatedJob{

	private GangliaTrace gangliaTrace;
	
	public GangliaSimulatedJob(int submitTime, TraceJob traceJob, GangliaTrace gangliaTrace) {
		super(submitTime, traceJob);
		this.gangliaTrace = gangliaTrace;
	}
	
	@Override
	public double getCpu(int time) {
		return gangliaTrace.getCpuUtil(traceJob, startTime, time);
	}

}
