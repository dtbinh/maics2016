package edu.hendrix.ev3.ai.som;

import edu.hendrix.ev3.ai.cluster.Clusterable;

public class SOMTestee implements Clusterable<SOMTestee> {
	private double d;
	
	public SOMTestee() {this(0);}
	
	public SOMTestee(double d) {this.d = d;}
	
	public double getD() {return d;}

	@Override
	public SOMTestee weightedCentroidWith(SOMTestee other, int thisCount, int otherCount) {
		return new SOMTestee(other.d * thisCount / otherCount);
	}
}
