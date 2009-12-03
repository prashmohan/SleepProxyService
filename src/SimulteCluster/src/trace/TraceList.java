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
		if (!traceformat.equals("gwf")) {
			File file = new File(filename);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				BufferedReader d = new BufferedReader(
						new InputStreamReader(fis));
				String readLine;
				int heaplimit = 0;
				String lastjobId;
				while ((readLine = d.readLine()) != null & heaplimit < n) {
					if (!readLine.equals("")) {
						String[] x = Pattern.compile(" ").split(readLine);
						TraceJob temp = new TraceJob();
						for (int i = 0; i < x.length; i++) {
							temp.list.add(i, x[i]);
						}
						lastjobId = x[0];
						temp.jobId = x[0];
						temp.timeLimit = getOnlyNumerics(x[5]);
						temp.submitTime = getOnlyNumerics(x[8]);
						temp.dispatchTime = getOnlyNumerics(x[9]);
						int TotalRunTime = getOnlyNumerics(x[11])
								- getOnlyNumerics(x[10]);
						Double ProcessorTimeUtilised = Double
								.parseDouble(x[29]);
						Double CpuUse = (ProcessorTimeUtilised * 100)
								/ (TotalRunTime * getOnlyNumerics(x[31]) * getOnlyNumerics(x[2]));
						temp.cpuUse = CpuUse;
						temp.nproc = getOnlyNumerics(x[31])
								* getOnlyNumerics(x[2]);

						String[] y = Pattern.compile(":").split(x[37]);
						for (int i = 0; i < y.length; i++) {
							temp.nodes.add(i, y[i]);
						}
						temp.diskUse = (int) ((getOnlyNumerics(x[33]) * 1.0 / getOnlyNumerics(x[31])) * 1024);
						temp.memUse = (int) ((getOnlyNumerics(x[32]) * 1.0 / getOnlyNumerics(x[31])) * 1024);
						temp.vMemUse = (int) ((getOnlyNumerics(x[34]) * 1.0 / getOnlyNumerics(x[31])) * 1024);
						if (temp.cpuUse < 100 && temp.cpuUse > 0 && temp.nproc > 0) {
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
		} else {
			File file = new File(filename);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				BufferedReader d = new BufferedReader(
						new InputStreamReader(fis));
				String readLine;
				int heaplimit = 0;
				String lastjobId;
				while ((readLine = d.readLine()) != null & heaplimit < n) {
					if (!readLine.startsWith("#")) {
						String[] x = readLine.trim().split("\\s+");
						TraceJob temp = new TraceJob();
						for (int i = 0; i < x.length; i++) {
							temp.list.add(i, x[i]);
						}
						lastjobId = x[0];
						temp.jobId = x[0];
						temp.timeLimit = (int) Double.parseDouble(x[8]);
						temp.submitTime = getOnlyNumerics(x[1]);
						temp.dispatchTime = getOnlyNumerics(x[1])+getOnlyNumerics(x[2]);
						int TotalRunTime = getOnlyNumerics(x[3]);
						Double ProcessorTimeUtilised = Double
								.parseDouble(x[5]);
						Double CpuUse = (ProcessorTimeUtilised * 100)
								/ (TotalRunTime);
						temp.cpuUse = CpuUse;
						temp.nproc = getOnlyNumerics(x[4]);
						//temp.DiskUse = (int) Double.parseDouble(x[23]) * 1024;
						if(!filename.contains("anon_jobs.gwf")){
							temp.diskUse = (int)Double.parseDouble(x[23])*1024;
						}else{
							temp.diskUse = 0;
						}
						temp.memUse = (int) Double.parseDouble((x[6]));
						if(!filename.contains("anon_jobs.gwf")){
							temp.networkUse = Double.parseDouble(x[22]);
						}else{
							temp.networkUse = 0.0;
						}
						temp.vMemUse = 0;
						if (temp.cpuUse < 100 && temp.cpuUse > 0 && temp.nproc > 0) {
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
