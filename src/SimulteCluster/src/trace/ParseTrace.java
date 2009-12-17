package trace;


public class ParseTrace {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//TraceList x = new TraceList("/Users/himanshu/Downloads/anon_jobs_gwf-1/grid5000_clean_trace.log",5000,"gwf");
		TraceList x = new TraceList("/Users/himanshu/20090801",30000,"torque");
		//TraceList x = new TraceList("/Users/himanshu/Downloads/HPC2N-2002-0",5000,"muai");
		// TraceList(filename,heaplimit,traceformat)
		//anon_jobs is in gwf format, HPC@N-2002-0 is in muai format; use "torque" for consolidated torque logs .
		for(int i=0;i<3000;i++){
			System.out.println("SubmitTime:"+x.getTraceList().get(i).getSubmitTime());
	//		System.out.println("DispatchTime:"+x.getTracelist().get(i).dispatchTime);
			System.out.println("StartTime:"+x.getTraceList().get(i).getStartTime());
			System.out.println("EndTime:"+x.getTraceList().get(i).endTime);
			System.out.println("JobId:"+x.getTraceList().get(i).getJobId());
	//		System.out.println("Nodes:"+x.getTracelist().get(i).nodes.get(0));
			System.out.println("nproc:"+x.getTraceList().get(i).getNodes());
	//		System.out.println("cpuUse:"+x.getTracelist().get(i).cpuUse);
			System.out.println("*"+i+"*");
		}
	}

}
