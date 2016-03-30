package edu.hendrix.ev3.ai;

import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;

public interface RobotLearner {
	public void train(AdaptedYUYVImage img, Move current);
	public Move bestMatchFor(AdaptedYUYVImage img);
	public boolean isTrained();
}
