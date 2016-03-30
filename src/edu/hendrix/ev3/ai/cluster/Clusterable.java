package edu.hendrix.ev3.ai.cluster;

public interface Clusterable<T> {
	public T weightedCentroidWith(T other, int thisCount, int otherCount);
}
