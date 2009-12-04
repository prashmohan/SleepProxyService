package trace;

import java.util.ArrayList;

public class TraceJob {
//	ArrayList<String> list = new ArrayList<String>(); //Raw Job Trace
	String jobId; //Job ID
	int timeLimit; // Allowed Time Duration in secs. (Upper bound on run time estimation)
	int submitTime; //Submit Time(UNIX)
	int dispatchTime; //Dispatch Time(UNIX)
	int startTime; //Actual Start Time (secs)
	int endTime; //Actual End Time (secs)
	double cpuUse; //Average CPU utilization(Average CPU time/Run Time) %
	int nproc; //no. of processors reqd.
	
//	int diskUse; // Disk used per processor in KB
//	int memUse; //Memory used per processor in KB 
//	int vMemUse; //Virtual Memory used per processor in KB
//	Double networkUse; // Networ Use per processor in KB/sec
	ArrayList<String> nodes = new ArrayList<String>(); //hosts allocated
	
	public TraceJob() {
		// TODO Auto-generated constructor stub
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

	public int getNodesRequired() {
		return nproc;
	}
}
