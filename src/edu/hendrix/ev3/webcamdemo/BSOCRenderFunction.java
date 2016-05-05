package edu.hendrix.ev3.webcamdemo;

import java.awt.image.BufferedImage;

public interface BSOCRenderFunction {
	public void render(BufferedImage img, ClusterableImage ref, double frameRate, double trainingTime, int cluster);
}
