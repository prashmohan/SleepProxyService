package trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import node.Node;
import node.SimpleNode;

public class TraceList {
	private ArrayList<TraceJob> tracelist;
	private ArrayList<Node> nodeList;

	int n;

	public TraceList(String filename, int n, String traceformat) {
		tracelist = new ArrayList<TraceJob>();
		nodeList = new ArrayList<Node>();
		
		Parse(filename, n, traceformat);
		Collections.sort(tracelist);
		ExtractNodes();
	}
	public void Parse(String filename, int n, String traceformat) {
		if (traceformat.equals("muai")) {
			ParseMuai(filename, n);
		} else if (traceformat.equals("gwf")) {
			ParseGwf(filename, n);
		} else if (traceformat.equals("torque")) {
			ParseTorque(filename, n);
		}
	}

	public void ParseMuai(String filename, int n) {
		File file = new File(filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			String readLine;
			int heaplimit = 0;
			while ((readLine = d.readLine()) != null & heaplimit < n) {
				if (!readLine.equals("")) {
					String[] x = Pattern.compile(" ").split(readLine);
					TraceJob temp = new TraceJob();
					temp.jobId = x[0]; // x[0]:Name of job
					temp.timeLimit = getOnlyNumerics(x[5]); // x[5]:Maximum
					// allowed job
					// duration in
					// seconds
					temp.submitTime = getOnlyNumerics(x[8]); // x[8]:Epoch time
					// when job was
					// submitted
					temp.dispatchTime = getOnlyNumerics(x[9]);// x[9]:Epoch time
					// when
					// scheduler
					// requested job
					// begin
					// executing
					temp.startTime = getOnlyNumerics(x[10]);// x[10]:Epoch time
					// when job began
					// executing�
					temp.endTime = getOnlyNumerics(x[11]);// x[11]:Epoch time
					// when job
					// completed
					// execution

					int runTime = getOnlyNumerics(x[11])
							- getOnlyNumerics(x[10]);
					Double actualCPUtimeUtilised = Double.parseDouble(x[29]);// x[29]:Number
					// of
					// processor
					// seconds
					// actually
					// utilized
					// by
					// job

					Double CpuUse;
					if (runTime != 0) {
						CpuUse = (actualCPUtimeUtilised * 100)
								/ (runTime * getOnlyNumerics(x[31]) * getOnlyNumerics(x[2]));
						// x[31]:Number of processors required per task
						// x[2]:Number of tasks requested
					} else {
						CpuUse = 100.0;
					}
					temp.cpuUse = CpuUse;

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
					//temp.nproc = getOnlyNumerics(x[31]) * getOnlyNumerics(x[2]);
					temp.nproc = temp.nodes.size();
					if (temp.nproc > 0 && temp.submitTime >= 0
							&& temp.startTime >= 0
							&& temp.endTime >= temp.startTime) {
						tracelist.add(temp);
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

	public void ParseGwf(String filename, int n) {
		File file = new File(filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			String readLine;
			int heaplimit = 0;
			while ((readLine = d.readLine()) != null & heaplimit < n) {
				if (!(readLine.startsWith("#") || readLine.equals(""))) {
					String[] x = readLine.trim().split("\\s+");
					TraceJob temp = new TraceJob();
					temp.jobId = x[0]; // x[0]:Name of job
					temp.timeLimit = (int) Double.parseDouble(x[8]); // x[8]:
					// User
					// runtime
					// estimate(upper
					// bound)
					temp.submitTime = (int) Double.parseDouble(x[1]);// x[1]:Time
					// when
					// job
					// was
					// submitted
					// (in
					// secs)
					temp.dispatchTime = (int) (Double.parseDouble(x[1]) + Double
							.parseDouble(x[2]));// x[2]:Wait Time(in secs)

					int runTime = getOnlyNumerics(x[3]);// x[3]: Run Time (in
					// secs)
					Double ProcessorTimeUtilised = Double.parseDouble(x[5]);// x[5]:Average
					// CPUTime
					// Used
					Double CpuUse;
					if (runTime != 0) {
						CpuUse = (ProcessorTimeUtilised * 100) / (runTime);
					} else {
						CpuUse = 100.0;
					}

					temp.cpuUse = CpuUse;
					temp.nproc = getOnlyNumerics(x[4]);// x[4]: No. of allocated
					// processors

					temp.startTime = temp.dispatchTime;
					temp.endTime = temp.startTime + runTime;

					if (temp.cpuUse > 100) {
						temp.cpuUse = 100;
					}
					if (temp.cpuUse < 0) {
						temp.cpuUse = 0;
					}
					if (temp.nproc > 0 && temp.submitTime >= 0
							&& temp.startTime >= 0
							&& temp.endTime >= temp.startTime) {
						tracelist.add(temp);
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

	private void ParseTorque(String filename, int n) {
		File file = new File(filename);
		FileInputStream fis = null;
		Hashtable<String, TraceJob> hash = new Hashtable<String, TraceJob>();
		Hashtable<Integer, String> hash2 = new Hashtable<Integer, String>();
		try {
			fis = new FileInputStream(file);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			String readLine;
			int heaplimit = 0;
			while ((readLine = d.readLine()) != null & heaplimit < n) {
				if (!(readLine.startsWith("#") || readLine.equals(""))) {
					String[] x = readLine.trim().split(";");
					if (x[1].equals("S")) {

					} else if (x[1].equals("E")) {
						TraceJob temp = new TraceJob();
						temp.jobId = x[2];
						String[] z = x[3].trim().split("\\s+");
						String[] y = new String[21];
						for (int i = 0; i < z.length; i++) {
							String[] value = z[i].trim().split("=");
							if (value[0].equals("ctime")) {
								y[4] = value[1];// submit time
							} else if (value[0].equals("qtime")) {
								y[5] = value[1];// dispatch time
							} else if (value[0].equals("etime")) {
								y[6] = value[1];// time job was queued
							} else if (value[0].equals("start")) {
								y[7] = value[1];// start time
							} else if (value[0].equals("exec_host")) {
								y[8] = value[1];// list of node_name/no_of_cpu
												// assigned
							} else if (value[0].equals("end")) {
								y[15] = value[1];// end time
							} else if (value[0]
									.equals("Resource_List.walltime")) {
								y[13] = value[1];// Max time allowed
							} else if (value[0].equals("resources_used.cput")) {
								y[17] = value[1];// Cpu Time used in secs
							}
						}
						temp.startTime = getOnlyNumerics((y[7]));
						temp.submitTime = getOnlyNumerics((y[4]));
						temp.endTime = getOnlyNumerics((y[15]));
						temp.dispatchTime = getOnlyNumerics((y[5]));
						temp.timeLimit = getTime((y[13]));
						String[] nodestring = (y[8]).trim().split("/");
						if (nodestring[1].equals("0"))
							nodestring[1] = "1";
						temp.nproc = getOnlyNumerics(nodestring[1]);
						temp.nodes.add(nodestring[0]);
						int ProcessorTimeUtilised = getTime((y[17]));
						Double CpuUse;
						int runTime = temp.endTime - temp.startTime;
						if (runTime != 0) {
							CpuUse = (ProcessorTimeUtilised * 100.0)
									/ (runTime * temp.nproc);
						} else {
							CpuUse = 100.0;
						}

						temp.cpuUse = CpuUse;
						if (temp.cpuUse > 100) {
							temp.cpuUse = 100;
						}
						if (temp.cpuUse < 0) {
							temp.cpuUse = 0;
						}
						if (temp.nproc > 0 && temp.submitTime >= 0
								&& temp.startTime >= 0
								&& temp.endTime >= temp.startTime) {

							tracelist.add(temp);
							heaplimit++;

						}

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

	private void ExtractNodes() {
		Map<String, Node> nameToNode = new HashMap<String, Node>();
		for (TraceJob job : tracelist) {
			List<String> nodes = job.getNodes();
			for (String nodeName : nodes) {
				if (!nameToNode.containsKey(nodeName)) {
					nameToNode.put(nodeName, new SimpleNode(nodeName));
				}
			}
		}
		nodeList = new ArrayList<Node>(nameToNode.values());
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

	public static int getTime(String str) {

		String[] time = str.trim().split(":");
		int timeinsec = Integer.parseInt(time[2]) + 60
				* Integer.parseInt(time[1]) + 3600 * Integer.parseInt(time[0]);
		return timeinsec;
	}

	public ArrayList<TraceJob> getTraceList() {
		return tracelist;
	}

	public ArrayList<Node> getNodeList(int n) {
		if (nodeList.size() < n) {
			int i = 0;
			while (nodeList.size() < n) {
				nodeList.add(new SimpleNode("newnode"+i));
			}
		}
		return nodeList;
	}

	public ArrayList<Node> getNodeList() {
		return nodeList;
	}
}
