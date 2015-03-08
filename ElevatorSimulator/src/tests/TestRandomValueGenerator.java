package tests;

import java.util.Random;
import org.junit.Test;
import elevatorsimulator.RandomValueGenerator;

public class TestRandomValueGenerator {
	@Test
	public void testGenerate() {
		RandomValueGenerator<Integer> random = new RandomValueGenerator<>(new Random());
		random.addValue(0.3, 0);
		random.addValue(0.4, 1);
		random.addValue(0.3, 2);
		
		int numTests = 100000;
		
		int[] values = new int[3];
		for (int i = 0; i < numTests; i++) {
			values[random.randomValue()]++;
		}
		
		for (int i = 0; i < values.length; i++) {
			System.out.println(i + ": " + values[i] / (double)numTests);
		}
	}
}
