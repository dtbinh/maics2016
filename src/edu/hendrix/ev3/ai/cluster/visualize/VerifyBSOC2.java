package edu.hendrix.ev3.ai.cluster.visualize;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;

public class VerifyBSOC2 {

	@Test
	public void testVideo1() throws FileNotFoundException {
		System.out.println("Opening video 1");
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom("1");
		BSOCVideoAssessor assessor1 = new BSOCVideoAssessor(inputs, 16);
		assertEquals(1.75420400756394035E18, assessor1.ssdInputs2nodes(), 1.0E12);
		BSOCVideoAssessor assessor2 = new BSOCVideoAssessor(inputs, 32);
		assertEquals(1.1126449106146112E18, assessor2.ssdInputs2nodes(), 1.0E12);
		BSOCVideoAssessor assessor3 = new BSOCVideoAssessor(inputs, 64);
		assertEquals(6.7649859735513485E17, assessor3.ssdInputs2nodes(), 1.0E12);
	}

	@Test
	public void testVideo2() throws FileNotFoundException {
		System.out.println("Opening video 2");
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom("2");
		BSOCVideoAssessor assessor1 = new BSOCVideoAssessor(inputs, 16);
		assertEquals(1.19931101054433306E18, assessor1.ssdInputs2nodes(), 1.0E12);
		BSOCVideoAssessor assessor2 = new BSOCVideoAssessor(inputs, 32);
		assertEquals(6.7451474841415078E17, assessor2.ssdInputs2nodes(), 1.0E12);
		BSOCVideoAssessor assessor3 = new BSOCVideoAssessor(inputs, 64);
		assertEquals(2.83849047368547744E17, assessor3.ssdInputs2nodes(), 1.0E12);
	}

}
