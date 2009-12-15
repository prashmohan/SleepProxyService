package trace.ganglia;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import trace.TraceJob;

public class GangliaTrace {

	private String dir;
	
	public GangliaTrace(String dir) {
		this.dir = dir;
	}
	
	public double getCpuUtil(TraceJob traceJob, int startTime, int time) {
		List<String> nodeList = traceJob.getNodes();
		if (nodeList.isEmpty()) {
			return 100;
		}
		
		// This needs to be the number of the node, so more parsing required
		String nodeId = nodeList.get(0);	
		int traceTime = traceJob.getStartTime() + (time - startTime);
		String fileName = getFileNameFromId(nodeId);
		
		return getCpuUtilFromFile(fileName, traceTime);
	}

	private String getFileNameFromId(String nodeId) {
		return "";
	}

	private double getCpuUtilFromFile(String fileName, int traceTime) {
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(fileName));
			String line = in.readLine();
			int spacePoint = line.indexOf(' ');
			int time = -1;
			if (spacePoint > 0) {
				time = Integer.parseInt(
						line.substring(0, spacePoint));
			}
			if (traceTime == time) {
				double cpu = Double.parseDouble(
						line.substring(spacePoint + 1));
				if (0 <= cpu && cpu <= 100) {
					return cpu;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 100;
	}
}
