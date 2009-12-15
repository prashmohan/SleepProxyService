package job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import trace.TraceJob;

public class SimulatedJob {

	private TraceJob traceJob;
	private int submitTime;
	private int startTime;
	private int endTime;
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
		return "<" + traceJob.getJobId() + "," + startTime + "," + requiredTime + ">";
	}
	
	public int getTimeToCompletion() {
		return endTime - submitTime;
	}
	
	public int getExecTime() {
		return endTime - startTime;
	}
	
	public String getTimeOfDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((long) traceJob.getStartTime() * 100);
		return new SimpleDateFormat("HH").format(cal.getTime());
	}
}
