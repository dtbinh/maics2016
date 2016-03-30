package edu.hendrix.ev3.ai.som;

import java.util.ArrayList;
import java.util.function.Supplier;

import edu.hendrix.ev3.ai.cluster.Clusterable;
import edu.hendrix.ev3.ai.cluster.Clusterer;
import edu.hendrix.ev3.ai.cluster.DistanceFunc;

public class SOM<T extends Clusterable<T>> implements Clusterer<T> {

	private ArrayList<T> nodes = new ArrayList<>();
	private int width;
	private DistanceFunc<T> func;
	
	private final static int SIGNIFICANCE = 1000;
	
	public SOM(DistanceFunc<T> func, Supplier<T> defaultMaker, int numNodes, int width) {
		if (numNodes % width != 0) {
			throw new IllegalArgumentException(String.format("Illegal SOM: %d nodes, %d width", numNodes, width));
		}
		for (int i = 0; i < numNodes; i++) {
			nodes.add(defaultMaker.get());
		}
		this.width = width;
		this.func = func;
	}
	
	ArrayList<Integer> neighborsOf(int node) {
		ArrayList<Integer> result = new ArrayList<>();
		for (SOMDir dir: SOMDir.values()) {
			if (!dir.atEdge(node, getWidth(), size())) {
				result.add(dir.neighborOf(node, getWidth()));
			}
		}
		return result;
	}
	
	int getWidth() {return width;}
	
	private void updateNode(int node, T example, double rate) {
		//nodes.set(node, nodes.get(node).train(example, rate));
		nodes.set(node, nodes.get(node).weightedCentroidWith(example, (int)(rate * SIGNIFICANCE), SIGNIFICANCE));
	}
	
	@Override
	public int train(T example) {
		int best = getNodeRanking(example).get(0).getFirst();
		updateNode(best, example, 0.9);
		for (SOMDir dir: SOMDir.values()) {
			if (!dir.atEdge(best, getWidth(), nodes.size())) {
				int neighbor = dir.neighborOf(best, getWidth());
				updateNode(neighbor, example, 0.4);
			}
		}
		return best;
	}

	@Override
	public T getIdealInputFor(int node) {
		return nodes.get(node);
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public ArrayList<Integer> getClusterIds() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int i = 0; i < size(); i++) {result.add(i);}
		return result;
	}

	@Override
	public DistanceFunc<T> getDistanceFunc() {
		return func;
	}

	// The function-call overhead turned out to be pretty slow in practice.
	// It is useful to have here for reference.
	public static double trainValue(double node, double input, double scale) {
		return node + scale * (input - node);
	}
}
