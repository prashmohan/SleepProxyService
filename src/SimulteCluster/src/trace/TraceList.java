package trace;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class TraceList {
	private ArrayList<TraceJob> tracelist = new ArrayList<TraceJob>();
	int n;

	public TraceList(String filename, int n, String traceformat) {
		Parse(filename, n, traceformat);
	}

	public void Parse(String filename, int n, String traceformat) {
		if (traceformat.equals("muai")) {
			File file = new File(filename);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				BufferedReader d = new BufferedReader(
						new InputStreamReader(fis));
				String readLine;
				int heaplimit = 0;
				while ((readLine = d.readLine()) != null & heaplimit < n) {
					if (!readLine.equals("")) {
						String[] x = Pattern.compile(" ").split(readLine);
						TraceJob temp = new TraceJob();
						temp.jobId = x[0]; // x[0]:Name of job
						temp.timeLimit = getOnlyNumerics(x[5]); //x[5]:Maximum allowed job duration in seconds
						temp.submitTime = getOnlyNumerics(x[8]); //x[8]:Epoch time when job was submitted
						temp.dispatchTime = getOnlyNumerics(x[9]);//x[9]:Epoch time when scheduler requested job begin executing
						temp.startTime = getOnlyNumerics(x[10]);//x[10]:Epoch time when job began executingÊ 
						temp.endTime = getOnlyNumerics(x[11]);//x[11]:Epoch time when job completed execution

						int runTime = getOnlyNumerics(x[11])-getOnlyNumerics(x[10]);
						Double actualCPUtimeUtilised = Double.parseDouble(x[29]);//x[29]:Number of processor seconds actually utilized by job
						
						Double CpuUse;
						if(runTime!=0){
							CpuUse = (actualCPUtimeUtilised * 100)
							/ (runTime * getOnlyNumerics(x[31]) * getOnlyNumerics(x[2]));
							//x[31]:Number of processors required per task
							//x[2]:Number of tasks requested  
						} else {
							CpuUse = 100.0;
						}
						temp.cpuUse = CpuUse;
						temp.nproc = getOnlyNumerics(x[31])
						* getOnlyNumerics(x[2]);

						String[] y = Pattern.compile(":").split(x[37]);
						for (int i = 0; i < y.length; i++) {
							temp.nodes.add(i, y[i]);
						}
						if (temp.cpuUse > 100) {
							temp.cpuUse = 100;
						}
						if (temp.cpuUse < 0) {
							temp.cpuUse = 0;
						}
						if (temp.nproc > 0 && temp.submitTime>=0 && temp.startTime>=0 && temp.endTime>=temp.startTime) {
							getTracelist().add(temp);
							heaplimit++;
						}
					}
				}
				fis.close();
				d.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(traceformat.equals("gwf")){
			File file = new File(filename);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				BufferedReader d = new BufferedReader(
						new InputStreamReader(fis));
				String readLine;
				int heaplimit = 0;
				while ((readLine = d.readLine()) != null & heaplimit < n) {
					if (!(readLine.startsWith("#") || readLine.equals(""))) {
						String[] x = readLine.trim().split("\\s+");
						TraceJob temp = new TraceJob();
						temp.jobId = x[0]; // x[0]:Name of job
						temp.timeLimit = (int) Double.parseDouble(x[8]); //x[8]: User runtime estimate(upper bound)
						temp.submitTime = (int) Double.parseDouble(x[1]);//x[1]:Time when job was submitted (in secs)
						temp.dispatchTime = (int) (Double.parseDouble(x[1])+Double.parseDouble(x[2]));//x[2]:Wait Time(in secs)
						
							int runTime = getOnlyNumerics(x[3]);//x[3]: Run Time (in secs)
							Double ProcessorTimeUtilised = Double.parseDouble(x[5]);//x[5]:Average CPUTime Used 
							Double CpuUse;
							if(runTime!=0){
								CpuUse = (ProcessorTimeUtilised * 100) / (runTime);  
							} else {
								CpuUse = 100.0;
							}

						temp.cpuUse = CpuUse;
						temp.nproc = getOnlyNumerics(x[4]);//x[4]: No. of allocated processors
						
						temp.startTime = temp.dispatchTime; 
						temp.endTime = temp.startTime+runTime;
						
						if (temp.cpuUse > 100) {
							temp.cpuUse = 100;
						}
						if (temp.cpuUse < 0) {
							temp.cpuUse = 0;
						}
						if (temp.nproc > 0 && temp.submitTime>=0 && temp.startTime>=0 && temp.endTime>=temp.startTime) {
							getTracelist().add(temp);
							heaplimit++;
						}
					}
				}
				fis.close();
				d.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getOnlyNumerics(String str) {

		if (str == null) {
			return 0;
		}

		StringBuffer strBuff = new StringBuffer();
		char c;

		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			if (Character.isDigit(c)) {
				strBuff.append(c);
			}
		}
		if (strBuff.toString().equals("")) {
			return 0;
		}

		return Integer.parseInt(strBuff.toString());
	}

	public ArrayList<TraceJob> getTracelist() {
		return tracelist;
	}

}
