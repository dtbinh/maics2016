package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;

public class AssessBSOC {
	
	void test(int video) throws FileNotFoundException {
		String videoName = Integer.toString(video + 1);
		System.out.println("Opening video " + videoName);
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom(videoName);
		int max = 16;
		for (int i = 0; i < 3; i++) {
			BSOCVideoAssessor assessor = new BSOCVideoAssessor(inputs, max);
			System.out.println(assessor.ssdInputs2nodes());
			max *= 2;
		}
	}

	@Test
	public void testVideo1() throws FileNotFoundException {
		test(0);
	}

	@Test
	public void testVideo2() throws FileNotFoundException {
		test(1);
	}

}
