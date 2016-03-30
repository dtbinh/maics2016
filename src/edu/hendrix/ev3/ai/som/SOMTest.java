package edu.hendrix.ev3.ai.som;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class SOMTest {
	SOM<SOMTestee> som;
	
	@Before
	public void setup() {
		som = new SOM<SOMTestee>((x1,x2) -> (long)Math.floor(x2.getD() - x1.getD()),SOMTestee::new,16,4);
	}
	
	public void testEdge(SOMDir dir) {
		HashSet<Integer> truths = dir.edgeNodesFor(som);
		for (int i = 0; i < som.size(); i++) {
			boolean at = dir.atEdge(i, som.getWidth(), som.size());
			if (truths.contains(i)) {
				assertTrue(at);
			} else {
				assertFalse(at);
			}
		}
	}

	@Test
	public void testTop() {
		testEdge(SOMDir.NORTH);
	}

	@Test
	public void testBottom() {
		testEdge(SOMDir.SOUTH);
	}
	
	@Test
	public void atLeft() {
		testEdge(SOMDir.EAST);
	}
	
	@Test
	public void atRight() {
		testEdge(SOMDir.WEST);
	}
}
