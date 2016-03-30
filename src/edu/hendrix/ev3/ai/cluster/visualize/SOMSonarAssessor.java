package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import edu.hendrix.ev3.ai.cluster.ClusterableSonarState;
import edu.hendrix.ev3.sonar.SonarPosition;

public class SOMSonarAssessor extends SOMAssessor<ClusterableSonarState> {
	public SOMSonarAssessor(ArrayList<ClusterableSonarState> inputs, int numNodes, int width) {
		super(inputs, ClusterableSonarState::distance, () -> {
			ClusterableSonarState css = new ClusterableSonarState();
			for (SonarPosition pos: SonarPosition.values()) {
				css.setReading(pos, 1.0f);
			}
			return css;
		}, numNodes, width);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 3) {
			System.out.println("Usage: java SOMSonarAssessor numNodes width filename [-shuffle]");
			System.exit(1);
		}
		
		System.out.println("SOMSonarAssessor");
		System.out.println("numNodes: " + args[0]);
		System.out.println("width:    " + args[1]);
		System.out.println("File:     " + args[2]);
		
		int numNodes = Integer.parseInt(args[0]);
		int width = Integer.parseInt(args[1]);
		File input = new File(args[2]);
		ArrayList<ClusterableSonarState> inputs = CmdLineVisualizer.getSonarValues(input);
		if (args.length == 4 && args[3].equals("-shuffle")) {
			Collections.shuffle(inputs);
			System.out.println("Shuffled");
		}
		SOMSonarAssessor assessor = new SOMSonarAssessor(inputs, numNodes, width);
		System.out.println("Num inputs:     " + assessor.getNumInputs());
		System.out.println("Input SSD:      " + assessor.ssdInputs2nodes());
		System.out.println("Input sq d:     " + BSOCSonarAssessor.toSqMeters(assessor.ssdInputs2nodes()));
	}
}
