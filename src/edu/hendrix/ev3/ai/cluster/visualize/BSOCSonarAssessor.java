package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import edu.hendrix.ev3.ai.bsoc.BoundedSelfOrgCluster;
import edu.hendrix.ev3.ai.cluster.ClusterableSonarState;

public class BSOCSonarAssessor extends BSOCAssessor<ClusterableSonarState> {
	public BSOCSonarAssessor(ArrayList<ClusterableSonarState> inputs, int numNodes) {
		super(inputs, ClusterableSonarState::distance, numNodes);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 2) {
			System.out.println("Usage: java BSOCSonarAssessor numNodes filename [-shuffle|-all]");
			System.exit(1);
		}
		
		System.out.println("BSOCSonarAssessor");
		System.out.println("Version: " + BoundedSelfOrgCluster.WHICH);
		System.out.println("numNodes: " + args[0]);
		System.out.println("File:     " + args[1]);
		
		int numNodes = Integer.parseInt(args[0]);
		File input = new File(args[1]);
		ArrayList<ClusterableSonarState> inputs = CmdLineVisualizer.getSonarValues(input);
		if (args.length == 3 && args[2].equals("-shuffle")) {
			Collections.shuffle(inputs);
			System.out.println("Shuffled");
		}
		BSOCSonarAssessor assessor = new BSOCSonarAssessor(inputs, numNodes);
		System.out.println("Num inputs:     " + assessor.getNumInputs());
		System.out.println("Input SSD:      " + assessor.ssdInputs2nodes());
		System.out.println("Input sq d:     " + toSqMeters(assessor.ssdInputs2nodes()));
		if (args.length > 2 && args[2].equals("-all")) {
			System.out.println("input,squaredDiff");
			ClusterAssessor.printAll(assessor.squaredDiffs());
		}
	}
	
	public static double toSqMeters(double ssd) {
		return ssd / Math.pow(ClusterableSonarState.DISTANCE_PRECISION, 2);
	}
}
