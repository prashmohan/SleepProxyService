package job;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import trace.TraceJob;

public class SimulatedJob {
 
	// the job in the trace this simulated job was based on
	private TraceJob traceJob;

	// time the scheduler submitted job in the simulator
	private int submitTime;
	
	// time the job started executing on a node
	private int startTime;
	
	// time job finished
	private int endTime;
	
	// time required to finish, derived from the traceJob
	private int requiredTime;

	public SimulatedJob(int submitTime, TraceJob traceJob) {
		this.traceJob = traceJob;
		this.submitTime = submitTime;
		requiredTime = traceJob.getEndTime() - traceJob.getStartTime();
		startTime = -1;
	}

	public boolean isFinished(int time) {
		assert startTime != -1;
		return time - startTime >= requiredTime;
	}

	public String getJobId() {
		return traceJob.getJobId();
	}

	public void start(int time) {
		startTime = time;
		endTime = startTime + requiredTime;
	}
	
	public String toString() {
		return "<Job " + traceJob.getJobId() + ", start " + startTime + ", req " + requiredTime + ">";
	}
	
	public int getTimeToCompletion() {
		return endTime - submitTime;
	}
	
	public int getTimeExec() {
		return endTime - startTime;
	}
	
	// returns the hour (in military time) that traceJob was started. gives
	// the time in epoch format, so it should be adjusted to account for time
	// zone differences
	public String getTimeOfDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((long) traceJob.getStartTime() * 100);
		return new SimpleDateFormat("HH").format(cal.getTime());
	}

	public double getCpu(int time) {
		return 100;
	}
}
