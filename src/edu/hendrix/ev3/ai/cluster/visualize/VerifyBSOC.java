package edu.hendrix.ev3.ai.cluster.visualize;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.hendrix.ev3.ai.bsoc.BoundedSelfOrgCluster;
import edu.hendrix.ev3.ai.bsoc.Version;
import edu.hendrix.ev3.ai.cluster.AdaptedYUYVImage;

public class VerifyBSOC {
	
	double[][] targets;
	
	@Before
	public void setup() {
		targets = new double[2][3];
		if (BoundedSelfOrgCluster.WHICH == Version.MAICS_1) {
			targets[0][0] = 4.7488291410958735E18;
			targets[0][1] = 2.5237083539540956E18;
			targets[0][2] = 1.1247892162177376E18;
			targets[1][0] = 3.7385099987214351E18;
			targets[1][1] = 1.80642523564683341E18;
			targets[1][2] = 1.05781460194489856E18;			
		}
		
		if (BoundedSelfOrgCluster.WHICH == Version.MAICS_2) {
			targets[0][0] = 1.75420400756394035E18;
			targets[0][1] = 1.1126449106146112E18;
			targets[0][2] = 6.7649859735513485E17;
			targets[1][0] = 1.19931101054433306E18;
			targets[1][1] = 6.7451474841415078E17;
			targets[1][2] = 2.83849047368547744E17;
		}
		
		if (BoundedSelfOrgCluster.WHICH == Version.POST_MAICS_2) {
			targets[0][0] = 8.7846081325292173E17;
			targets[0][1] = 5.2541838688893453E17;
			targets[0][2] = 2.67185273213525696E17;
			targets[1][0] = 6.6862946318216218E17;
			targets[1][1] = 3.4054702742091674E17;
			targets[1][2] = 1.32567388093082496E17;			
		}
	}
	
	void test(int video) throws FileNotFoundException {
		String videoName = Integer.toString(video + 1);
		System.out.println("Opening video " + videoName);
		ArrayList<AdaptedYUYVImage> inputs = BSOCVideoAssessor.getVideosFrom(videoName);
		int max = 16;
		for (int i = 0; i < targets[video].length; i++) {
			BSOCVideoAssessor assessor = new BSOCVideoAssessor(inputs, max);
			assertEquals(targets[video][i], assessor.ssdInputs2nodes(), 1.0E12);
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
