package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.hendrix.ev3.ai.cluster.ClusterableSonarState;

public class CmdLineVisualizer {
	
	public static ArrayList<ClusterableSonarState> getSonarValues(File file) throws FileNotFoundException {
		ArrayList<ClusterableSonarState> inputs = new ArrayList<>();
		Scanner s = new Scanner(file);
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (isTrainingLine(line)) {
				if (s.hasNextLine()) {
					ClusterableSonarState state = new ClusterableSonarState(s.nextLine());
					inputs.add(state);
				}
			}
		}
		s.close();
		return inputs;
	}
	
	public static boolean isTrainingLine(String line) {
		return line.startsWith("Training");
	}
}
