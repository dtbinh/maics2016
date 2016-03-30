package edu.hendrix.ev3.ai.cluster.visualize;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.hendrix.ev3.ai.cluster.Clusterable;
import edu.hendrix.ev3.ai.cluster.DistanceFunc;
import edu.hendrix.ev3.ai.som.SOM;

public class SOMAssessor<T extends Clusterable<T>> extends ClusterAssessor<T> {
	private DistanceFunc<T> func;
	
	public SOMAssessor(ArrayList<T> inputs, DistanceFunc<T> func, Supplier<T> defaultMaker, int numNodes, int width) {
		super(inputs, new SOM<>(func, defaultMaker, numNodes, width));
		this.func = func;
	}
	
	public double ssdInputs2nodes() {
		return super.ssdInputs2nodes(func);
	}
	
	public double ssdNodes2Nodes() {
		return super.ssdNodes2Nodes(func);
	}
	
	public ArrayList<Long> squaredDiffs() {
		return super.squaredDiffs(func);
	}
}
