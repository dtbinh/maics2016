package edu.hendrix.ev3.ai.cluster;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AdaptedYUYVImageTest {
	
	AdaptedYUYVImage img, small1, small2, small3;
	
	@Before
	public void setup() {
		img = new AdaptedYUYVImage(160, 120);
		small1 = new AdaptedYUYVImage(new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 10, 20, 30, 40, 50, 60, 70, 80, 10, 20, 30, 40, 50, 60, 70, 80}, 4, 3);
		small2 = new AdaptedYUYVImage(new byte[]{90, 80, 70, 60, 50, 40, 30, 20, 90, 80, 70, 60, 50, 40, 30, 20, 90, 80, 70, 60, 50, 40, 30, 20}, 4, 3);
		small3 = new AdaptedYUYVImage(new byte[]{-10, -20, -30, -40, -50, -60, -70, -80, -10, -20, -30, -40, -50, -60, -70, -80, -10, -20, -30, -40, -50, -60, -70, -80}, 4, 3);
	}

	@Test
	public void test() {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int i = y * img.getWidth() * 2 + x;
				assertEquals(y, img.getRow(i));
				assertEquals(x, img.getColumn(i));
			}
		}
	}

	@Test
	public void centroidTest1() {
		AdaptedYUYVImage mean = small1.weightedCentroidWith(small2, 1, 1);
		for (int i = 0; i < mean.getBytes().length; i++) {
			assertEquals(50, mean.getBytes()[i]);
		}
	}
	
	@Test
	public void centroidTest2() {
		AdaptedYUYVImage mean = small1.weightedCentroidWith(small2, 0, 1);
		for (int i = 0; i < mean.getBytes().length; i++) {
			assertEquals(small2.getBytes()[i], mean.getBytes()[i]);
		}
	}
	
	@Test
	public void centroidTest3() {
		AdaptedYUYVImage mean = small1.weightedCentroidWith(small2, 1, 0);
		for (int i = 0; i < mean.getBytes().length; i++) {
			assertEquals(small1.getBytes()[i], mean.getBytes()[i]);
		}
	}
	
	@Test
	public void centroidTest4() {
		AdaptedYUYVImage mean = small1.weightedCentroidWith(small2, 2, 8);
		for (int i = 0; i < mean.getBytes().length; i++) {
			byte column = (byte)(i % 8);
			byte target = (byte)(74 - column * 6);
			assertEquals(target, mean.getBytes()[i]);
		}
	}
	
	@Test
	public void centroidTest5() {
		AdaptedYUYVImage mean = small1.weightedCentroidWith(small3, 1, 1);
		for (int i = 0; i < mean.getBytes().length; i++) {
			assertEquals(-128, mean.getBytes()[i]);
		}
	}
	
	public void distanceTest1() {
		assertEquals(30400, YUYVDistanceFuncs.euclideanAllChannels(small1, small2));
		assertEquals(30400, YUYVDistanceFuncs.euclideanAllChannels(small2, small1));
	}
}
