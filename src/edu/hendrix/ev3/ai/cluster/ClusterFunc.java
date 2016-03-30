package edu.hendrix.ev3.ai.cluster;

public interface ClusterFunc<T> {
	public T clusterfy(T one, T two, double scale);
}
