package trace;


public class ParseTrace {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TraceList x = new TraceList("/Users/himanshu/Downloads/DAS2/anon_jobs.gwf",5000,"gwf");
		//or TraceList x = new TraceList("/Users/himanshu/Downloads/HPC2N-2002-0",5000,"muai");
		// TraceList(filename,heaplimit,traceformat)
		//anon_jobs is in gwf format and HPC@N-2002-0 is in muai format.
		for(int i=0;i<100;i++){
			System.out.println("SubmitTime:"+x.getTracelist().get(i).getSubmitTime());
			System.out.println("DispatchTime:"+x.getTracelist().get(i).getStartTime());
			System.out.println("JobId:"+x.getTracelist().get(i).getJobId());
			System.out.println("nproc:"+x.getTracelist().get(i).getNodesRequired());
			System.out.println("**********************");
		}
	}

}
