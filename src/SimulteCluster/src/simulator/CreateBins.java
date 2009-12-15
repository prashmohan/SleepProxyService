package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class CreateBins {
	public static void main(String args[]) {
		ArrayList<Integer> bins = new ArrayList<Integer>();
		int binSize = 30;
		String file = "";
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			while (bf.ready()) {
				String line = bf.readLine();
				int length = Integer.parseInt(line);
				while (bins.size() * binSize < length) {
					bins.add(0);
				}
				bins.set(length/binSize, bins.get(length/binSize)+1);
			}
			PrintStream ps = new PrintStream(file+"-bin");
			for (int i = 0; i < bins.size(); i++) {
				ps.println(i*binSize + "\t" + bins.get(i));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
