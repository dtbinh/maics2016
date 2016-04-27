package edu.hendrix.ev3.ai.cluster.visualize;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;

public class AssessBSOC2 {

	@Test
	public void testVideo1() throws FileNotFoundException {
		System.out.println("Opening video 1");
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom("1");
		BSOCVideoAssessor assessor1 = new BSOCVideoAssessor(inputs, 16);
		System.out.println(assessor1.ssdInputs2nodes());
		BSOCVideoAssessor assessor2 = new BSOCVideoAssessor(inputs, 32);
		System.out.println(assessor2.ssdInputs2nodes());
		BSOCVideoAssessor assessor3 = new BSOCVideoAssessor(inputs, 64);
		System.out.println(assessor3.ssdInputs2nodes());
	}

	@Test
	public void testVideo2() throws FileNotFoundException {
		System.out.println("Opening video 2");
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom("2");
		BSOCVideoAssessor assessor1 = new BSOCVideoAssessor(inputs, 16);
		System.out.println(assessor1.ssdInputs2nodes());
		BSOCVideoAssessor assessor2 = new BSOCVideoAssessor(inputs, 32);
		System.out.println(assessor2.ssdInputs2nodes());
		BSOCVideoAssessor assessor3 = new BSOCVideoAssessor(inputs, 64);
		System.out.println(assessor3.ssdInputs2nodes());
	}

}
