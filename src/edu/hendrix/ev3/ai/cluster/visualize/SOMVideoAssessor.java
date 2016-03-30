package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import edu.hendrix.ev3.ai.Move;
import edu.hendrix.ev3.ai.VideoEvalRobotLearner;
import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;
import edu.hendrix.ev3.ai.cluster.YUYVDistanceFuncs;
import edu.hendrix.ev3.util.Duple;

public class SOMVideoAssessor extends SOMAssessor<AdaptedYUYVImage> {
	public SOMVideoAssessor(ArrayList<AdaptedYUYVImage> inputs, int numNodes, int width) {
		super(inputs, YUYVDistanceFuncs::euclideanAllChannels, () -> new AdaptedYUYVImage(160,120), numNodes, width);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 3) {
			System.out.println("Usage: java BSOCVideoAssessor numNodes width storageInt [-shuffle]");
			System.exit(1);
		}
		
		System.out.println("SOMVideoAssessor");
		System.out.println("numNodes: " + args[0]);
		System.out.println("width:    " + args[1]);
		System.out.println("File:     " + args[2]);
		
		int numNodes = Integer.parseInt(args[0]);
		int width = Integer.parseInt(args[1]);
		ArrayList<AdaptedYUYVImage> inputs = getVideosFrom(args[2]);
		if (args.length > 3 && args[3].equals("-shuffle")) {
			Collections.shuffle(inputs);
			System.out.println("Shuffled");
		}
		SOMVideoAssessor assessor = new SOMVideoAssessor(inputs, numNodes, width);
		System.out.println("Num inputs:     " + assessor.getNumInputs());
		System.out.println("Input SSD:      " + (double)(assessor.ssdInputs2nodes()));
		System.out.println("Node2Node SSD:  " + (double)(assessor.ssdNodes2Nodes()));
	}
	
	public static ArrayList<AdaptedYUYVImage> getVideosFrom(String storageInt) throws FileNotFoundException {
		ArrayList<AdaptedYUYVImage> result = new ArrayList<>();
		for (Duple<Move, AdaptedYUYVImage> img: VideoEvalRobotLearner.retrieveVideos(storageInt)) {
			result.add(img.getSecond());
		}
		return result;
	}
}
