package trace;

import java.util.ArrayList;
import java.util.List;

public class TraceJob implements Comparable{
	//Raw Job Trace
	//ArrayList<String> list = new ArrayList<String>(); 
	
	//Job ID
	String jobId; 
	
	// Allowed Time Duration in secs. (Upper bound on run time estimation)
	int timeLimit; 
	
	//Submit Time(UNIX)
	int submitTime;
	
	//Dispatch Time(UNIX)
	int dispatchTime;
	
	//Actual Start Time (secs)
	int startTime;
	
	//Actual End Time (secs)
	int endTime;
	
	//Average CPU utilization(Average CPU time/Run Time) 
	double cpuUse;
	
	//no. of processors reqd.
	int nproc;
	
	//hosts allocated
	ArrayList<String> nodes; 
	
	public TraceJob() {
		nodes = new ArrayList<String>(); 
	}

	public String getJobId() {
		return jobId;
	}
	
	public int getSubmitTime() {
		return submitTime;	
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getEndTime() {
		return endTime;
	}

	public int getProcsRequired() {
		return nproc;
	}
	
	public List<String> getNodes() {
		return new ArrayList<String>(nodes);
	}
	
	public int getNumNodes() {
		return nodes.size();
	}
	
	@Override
	public int compareTo(Object o) {
		if (! (o instanceof TraceJob)) {
			throw new ClassCastException();
		}
		TraceJob job = (TraceJob) o;
		if (job.startTime < startTime) {
			return 1;
		} else if (job.startTime == startTime){
			return 0;
		} else {
			return -1;
		}
	}
	
	public String toString() {
		return "<" + jobId + ":" + startTime + ">"; 
	}
}
