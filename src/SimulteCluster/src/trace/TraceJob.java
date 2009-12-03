package trace;

import java.util.ArrayList;

public class TraceJob {
	ArrayList<String> list = new ArrayList<String>(); //Raw Job Trace
	String jobId; //Job ID
	int timeLimit; // Allowed Time Duration in secs.
	int submitTime; //Submit Time(UNIX)
	int dispatchTime; //Dispatch Time(UNIX)
	double cpuUse; //Average CPU utilization %
	int diskUse; // Disk used per processor in KB
	int memUse; //Memory used per processor in KB 
	int vMemUse; //Virtual Memory used per processor in KB
	int nproc; //no. of processors reqd.
	Double networkUse; // Networ Use per processor in KB/sec
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
		return dispatchTime;
	}
	
	public int getEndTime() {
		return dispatchTime + timeLimit;
	}

	public int getNodesRequired() {
		return nodes.size();
	}
}
