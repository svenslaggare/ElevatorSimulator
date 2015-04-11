package elevatorsimulator.reinforcementlearning;

import static org.junit.Assert.*;

import org.junit.Test;

public class ElevatorSystemStateTest {
	@Test
	public void testEpsilons() {
		assertEquals(0, ElevatorSystemState.hashCode(0.0));
		assertEquals(1, ElevatorSystemState.hashCode(0.05));
		assertEquals(1, ElevatorSystemState.hashCode(0.1));
		assertEquals(2, ElevatorSystemState.hashCode(0.15));
		assertEquals(2, ElevatorSystemState.hashCode(0.2));
		assertEquals(2, ElevatorSystemState.hashCode(0.24));
		
		assertEquals(0, ElevatorSystemState.hashCode(49));
		assertEquals(1, ElevatorSystemState.hashCode(51));
		assertEquals(1, ElevatorSystemState.hashCode(100));
		assertEquals(2, ElevatorSystemState.hashCode(150));
	}
}
