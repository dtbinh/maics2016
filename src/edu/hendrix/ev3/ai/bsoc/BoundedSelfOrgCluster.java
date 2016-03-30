package edu.hendrix.ev3.ai.bsoc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.hendrix.ev3.ai.cluster.Clusterable;
import edu.hendrix.ev3.ai.cluster.Clusterer;
import edu.hendrix.ev3.ai.cluster.DistanceFunc;
import edu.hendrix.ev3.util.DeepCopyable;
import edu.hendrix.ev3.util.Duple;
import edu.hendrix.ev3.util.Util;

import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.function.Function;

// This data structure is an adaptation of the idea of Agglomerative Clustering.
// 
// Traditional agglomerative clustering is concerned with finding a hierarchical relationship
// among the data elements.
//
// Our goal here is to create an online learning algorithm with fast and predictable 
// runtime performance, suitable for both supervised and unsupervised learning.

public class BoundedSelfOrgCluster<T extends Clusterable<T> & DeepCopyable<T>> implements Clusterer<T>, DeepCopyable<BoundedSelfOrgCluster<T>> {
	// Object state
	private TreeMap<Integer,Node<T>> nodes;
	private ArrayList<TreeSet<Edge<T>>> nodes2edges;
	private TreeSet<Edge<T>> edges;
	private BitSet availableNodes;
	private int maxNumNodes;
	
	// Higher-order function
	private DistanceFunc<T> dist;
	
	// Notification
	private ArrayList<BSOCListener> listeners = new ArrayList<>();
	
	public final static boolean BASIC_VERSION_MAICS = false;

	@Override
	public BoundedSelfOrgCluster<T> deepCopy() {
		BoundedSelfOrgCluster<T> result = new BoundedSelfOrgCluster<>(maxNumNodes, dist);
		result.availableNodes = this.availableNodes.get(0, this.availableNodes.size());
		for (Edge<T> edge: this.edges) {
			result.edges.add(edge.deepCopy());
		}
		DeepCopyable.copyFromInto(this.nodes, result.nodes);
		return result;
	}

	public BoundedSelfOrgCluster(int maxNumNodes, DistanceFunc<T> dist) {
		setupBasic(dist);
		setupAvailable(maxNumNodes);
	}
	
	private void setupBasic(DistanceFunc<T> dist) {
		this.dist = dist;
		this.nodes = new TreeMap<>();
		this.edges = new TreeSet<>();		
		this.nodes2edges = new ArrayList<>();
	}
	
	private void setupAvailable(int maxNumNodes) {
		this.maxNumNodes = maxNumNodes;
		availableNodes = new BitSet(maxNumNodes);
		availableNodes.set(0, maxNumNodes);
		Util.assertState(size() == 0, "size() should be zero, but is " + size());
	}
	
	public BoundedSelfOrgCluster(String src, Function<String,T> extractor, DistanceFunc<T> dist) {
		setupBasic(dist);
		ArrayList<String> topLevel = Util.debrace(src);
		rebuildAvailable(topLevel.get(0));
		if (topLevel.size() > 1) {
			rebuildNodes(topLevel.get(1), extractor);
		}
		if (topLevel.size() > 2) {
			rebuildEdges(topLevel.get(2));
		}
	}
	
	public boolean nodeExists(int node) {
		return nodes.containsKey(node);
	}
	
	public void addListener(BSOCListener listener) {
		listeners.add(listener);
	}
	
	private void rebuildAvailable(String availStr) {
		ArrayList<String> availability = Util.debrace(availStr);
		maxNumNodes = Integer.parseInt(availability.get(0));
		availableNodes = new BitSet(maxNumNodes);
		availableNodes.clear();
		if (availability.size() > 1) {
			for (String av: availability.get(1).split(", ")) {
				availableNodes.set(Integer.parseInt(av));
			}		
		}
	}
	
	private void rebuildNodes(String nodeStr, Function<String,T> extractor) {
		for (String node: Util.debrace(nodeStr)) {
			Node<T> newNode = new Node<>(node, extractor);
			nodes.put(newNode.getID(), newNode);
		}
	}
	
	private void rebuildEdges(String edgeStr) {
		for (String edge: Util.debrace(edgeStr)) {
			edges.add(new Edge<T>(edge));
		}
	}
	
	public int size() {return maxNumNodes - availableNodes.cardinality();}
	
	public int getStartingLabel() {return 0;}
	
	public long distanceToClosestMatch(T example) {
		return getNodeRanking(example).get(0).getSecond();
	}
	
	private long distance(Node<T> n1, Node<T> n2) {
		if (BASIC_VERSION_MAICS) {
			return dist.distance(n1.getCluster(), n2.getCluster());			
		} else {
			return Math.max(n1.getNumInputs(), n2.getNumInputs()) * dist.distance(n1.getCluster(), n2.getCluster());
		}
	}
	
	private void removeAllEdgesFor(int node) {
		for (Edge<T> edge: nodes2edges.get(node)) {
			edges.remove(edge);
			nodes2edges.get(edge.getOtherNode(node)).remove(edge);
		}
		nodes2edges.get(node).clear();
	}
	
	private void createEdgesFor(int node) {
		for (Entry<Integer, Node<T>> nodeEntry: nodes.entrySet()) {
			int i = nodeEntry.getKey();
			if (i != node) {
				long distance = distance(nodeEntry.getValue(), nodes.get(node));
				Edge<T> edge = new Edge<>(Math.min(i, node), Math.max(i, node), distance);
				edges.add(edge);
				nodes2edges.get(node).add(edge);
				nodes2edges.get(i).add(edge);
			}
		}
	}
	
	@Override
	public int train(T example) {
		if (availableNodes.cardinality() > 0) {
			return addNewNode(example);
		} else {
			return incorporateNewNode(example);
		}
	}
	
	private int addNewNode(T example) {
		int where = availableNodes.nextSetBit(0);
		nodes.put(where, new Node<>(where, example));
		availableNodes.clear(where);
		nodes2edges.add(new TreeSet<>());
		createEdgesFor(where);
		notifyAdd(where);
		return where;		
	}
	
	private int incorporateNewNode(T example) {
		Duple<Integer,Long>	closest = this.getClosestNodeDistanceFor(example);
		Edge<T> smallest = edges.first();
		if (closest.getSecond() < smallest.getDistance()) {
			int closestIndex = closest.getFirst();
			merge(closestIndex, n -> n.mergedWith(example));
		} else {
			purge(smallest);
			int available = availableNodes.nextSetBit(0);
			Util.assertState(available >= 0, "No nodes available");
			insert(available, new Node<>(available, example));
			notifyAdd(available);
		} 
		return getClosestMatchFor(example);		
	}
	
	private void purge(Edge<T> purgee) {
		int oldSize = size();
		int removed = purgee.getNode2();
		int absorber = purgee.getNode1();
		removeAllEdgesFor(removed);
		merge(absorber, n -> n.mergedWith(nodes.remove(removed)));
		notifyReplace(removed, absorber);
		availableNodes.set(removed);
		Util.assertState(oldSize - 1 == size(), "Did not shrink");
	}
	
	private void insert(int target, Node<T> node) {
		Util.assertArgument(availableNodes.get(target), target + " not available");
		removeAllEdgesFor(target);
		nodes.put(target, node);
		availableNodes.clear(target);
		createEdgesFor(target);
	}
	
	private void merge(int absorber, UnaryOperator<Node<T>> merger) {
		availableNodes.set(absorber);
		insert(absorber, merger.apply(nodes.get(absorber)));
	}
	
	private void notifyAdd(int added) {
		for (BSOCListener listener: listeners) {
			listener.addingNode(added);
		}
	}
	
	private void notifyReplace(int original, int replacement) {
		for (BSOCListener listener: listeners) {
			listener.replacingNode(original, replacement);
		}
	}
	
	public boolean edgeRepresentationConsistent() {
		for (int i = 0; i < nodes2edges.size(); i++) {
			if (nodeExists(i)) {
				for (Edge<T> edge: nodes2edges.get(i)) {
					if (!edges.contains(edge)) {
						return false;
					}
				}
			} else {
				if (!nodes2edges.get(i).isEmpty()) {
					return false;
				}
			}
		}
		
		for (Edge<T> edge: edges) {
			if (!nodeExists(edge.getNode1())) {return false;}
			if (!nodes2edges.get(edge.getNode1()).contains(edge)) {return false;}
			if (!nodeExists(edge.getNode2())) {return false;}
			if (!nodes2edges.get(edge.getNode2()).contains(edge)) {return false;}
		}
		
		return true;
	}
	
	public void shrink() {
		purge(edges.first());
	}
	
	@Override
	public T getIdealInputFor(int node) {
		Util.assertArgument(nodes.containsKey(node), "Node " + node + " not present");
		return nodes.get(node).getCluster();
	}

	@Override
	public ArrayList<Integer> getClusterIds() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodeExists(i)) {
				result.add(i);
			}
		}
		return result;
	}

	@Override
	public DistanceFunc<T> getDistanceFunc() {
		return dist;
	}
	
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("{");
		result.append(maxNumNodes);
		result.append(availableNodes);
		result.append("}\n{");
		for (Node<T> node: nodes.values()) {
			result.append('{');
			result.append(node);
			result.append('}');
		}
		result.append("}\n{");
		for (Edge<T> edge: edges) {
			result.append('{');
			result.append(edge.toString());
			result.append('}');
		}
		result.append("}");
		return result.toString();
	}
}
