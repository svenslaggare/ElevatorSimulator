package tests;
import static org.junit.Assert.*;

import org.junit.Test;

import elevatorsimulator.SimulatorClock;
import elevatorsimulator.TrafficProfile;

/**
 * Unit test for the TrafficProfile class
 * @author Anton Jansson
 *
 */
public class TestTrafficProfile {
	@Test
	public void testGetArrivalRate() {
		int num = 6 * 24;
		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[num];
		
		for (int i = 0; i < num; i++) {
			arrivalRates[i] = new TrafficProfile.Interval(i, 0.0, 0.0);
		}
		
		TrafficProfile traficProfile = new TrafficProfile(arrivalRates);
		
		for (int day = 0; day < 2; day++) {
			for (int i = 0; i < arrivalRates.length; i++) {
				long time = i * 10 * 60 * SimulatorClock.NANOSECONDS_PER_SECOND + day * 24 * 60 * 60 * SimulatorClock.NANOSECONDS_PER_SECOND;
				assertEquals(i, traficProfile.getIntervalData(time).getAverageArrivalRatio(), 0);
			}
		}
	}
}
