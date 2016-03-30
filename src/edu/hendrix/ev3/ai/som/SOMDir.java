package edu.hendrix.ev3.ai.som;

import java.util.HashSet;
import java.util.function.IntUnaryOperator;

import edu.hendrix.ev3.ai.cluster.Clusterable;

public enum SOMDir {
	NORTH {
		@Override
		public int neighborOf(int node, int width) {
			return node - width;
		}

		@Override
		public boolean atEdge(int node, int width, int numNodes) {
			return node - width < 0;
		}

		@Override
		public <T extends Clusterable<T>> HashSet<Integer> edgeNodesFor(SOM<T> som) {
			return edgesFromPattern(0, som.getWidth() - 1, x -> x+1);
		}
	}, SOUTH {
		@Override
		public int neighborOf(int node, int width) {
			return node + width;
		}

		@Override
		public boolean atEdge(int node, int width, int numNodes) {
			return node + width >= numNodes;
		}

		@Override
		public <T extends Clusterable<T>> HashSet<Integer> edgeNodesFor(SOM<T> som) {
			return edgesFromPattern(som.size() - som.getWidth(), som.size() - 1, x -> x+1);
		}
	}, EAST {
		@Override
		public int neighborOf(int node, int width) {
			return node - 1;
		}

		@Override
		public boolean atEdge(int node, int width, int numNodes) {
			return node % width == 0;
		}

		@Override
		public <T extends Clusterable<T>> HashSet<Integer> edgeNodesFor(SOM<T> som) {
			return edgesFromPattern(0, som.size() - 1, x -> x+som.getWidth());
		}
	}, WEST {
		@Override
		public int neighborOf(int node, int width) {
			return node + 1;
		}

		@Override
		public boolean atEdge(int node, int width, int numNodes) {
			return node % width == width - 1;
		}

		@Override
		public <T extends Clusterable<T>> HashSet<Integer> edgeNodesFor(SOM<T> som) {
			return edgesFromPattern(som.getWidth() - 1, som.size() - 1, x -> x+som.getWidth());
		}
	};
	
	abstract public int neighborOf(int node, int width);
	abstract public boolean atEdge(int node, int width, int numNodes);
	abstract public <T extends Clusterable<T>> HashSet<Integer> edgeNodesFor(SOM<T> som);
	
	private static HashSet<Integer> edgesFromPattern(int start, int end, IntUnaryOperator incr) {
		HashSet<Integer> result = new HashSet<>();
		for (int i = start; i <= end; i = incr.applyAsInt(i)) {
			result.add(i);
		}
		return result;
	}
}
