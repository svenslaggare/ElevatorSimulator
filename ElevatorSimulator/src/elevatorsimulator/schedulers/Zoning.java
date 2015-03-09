package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.List;

import elevatorsimulator.Building;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCar.State;
import elevatorsimulator.Direction;
import elevatorsimulator.Floor;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;

/**
 * Implements the 'Zoning' scheduling algorithm
 * @author Anton Jansson
 *
 */
public class Zoning implements SchedulingAlgorithm {
	private final int numZones;
	private final List<Zone> zones;
		
	/**
	 * Represents a zone
	 * @author Anton Jansson
	 *
	 */
	private static class Zone {
		public final List<Floor> floors;
		public final List<ElevatorCar> elevatorCars;
		
		public Zone(List<Floor> floors, List<ElevatorCar> elevatorCars) {
			this.floors = floors;
			this.elevatorCars = elevatorCars;
		}
				
		public int bottomFloor() {
			return this.floors.get(0).getFloorNumber();
		}
		
		public int middleFloor() {
			return this.floors.get(this.floors.size() / 2).getFloorNumber();
		}
		
		public int topFloor() {
			return this.floors.get(this.floors.size() - 1).getFloorNumber();
		}
	}
	
	/**
	 * Creates a new instance of the Zoning class
	 * @param numZones The number of zones
	 * @param building The building
	 */
	public Zoning(int numZones, Building building) {
		this.numZones = numZones;
		this.zones = new ArrayList<Zoning.Zone>();
		
		int floorsPerZone = building.getFloors().length / this.numZones;
		double spillPerFloor = (building.getFloors().length / (double)this.numZones) - floorsPerZone;

		double totalSpill = 0;
		int handledFloors = 0;
		
		for (int zone = 0; zone < numZones; zone++) {
			List<ElevatorCar> zoneElevators = new ArrayList<ElevatorCar>();
			List<Floor> zoneFloors = new ArrayList<Floor>();
			
			int elevatorsPerZone = building.getElevatorCars().length / this.numZones;
			
			totalSpill += spillPerFloor;
			int minFloor = handledFloors;
			int maxFloor = (zone + 1) * floorsPerZone;
			
			if (totalSpill >= 1.0) {
				totalSpill -= 1.0;
				maxFloor++;
			}
								
			for (ElevatorCar elevator : building.getElevatorCars()) {
				if (elevator.getId() >= zone * elevatorsPerZone && elevator.getId() < (zone + 1) * elevatorsPerZone) {
					zoneElevators.add(elevator);
				}
			}
								
			for (int floor = minFloor; floor < maxFloor; floor++) {
				zoneFloors.add(building.getFloors()[floor]);
			}		
								
			handledFloors += maxFloor - minFloor;
			this.zones.add(new Zone(zoneFloors, zoneElevators));
		} 
	}
	
	/**
	 * Returns the zone for the given elevator car
	 * @param elevatorCar The elevator car
	 */
	private Zone getZone(ElevatorCar elevatorCar) {
		for (Zone zone : this.zones) {
			if (zone.elevatorCars.contains(elevatorCar)) {
				return zone;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the floor for the given floor
	 * @param floor The floor
	 */
	private Zone getZone(int floor) {
		for (Zone zone : this.zones) {
			if (floor >= zone.bottomFloor() && floor <= zone.topFloor()) {
				return zone;
			}
		}
		
		return null;
	}
	
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {

	}

	@Override
	public void update(Simulator simulator) {
		for (Passenger passenger : simulator.getControlSystem().getHallQueue()) {
			for (ElevatorCar elevator : this.getZone(passenger.getArrivalFloor()).elevatorCars) {
				//Check if to dispatch the elevator
				if (elevator.getState() == State.IDLE && elevator.canPickupPassenger(passenger)) {
					elevator.moveTowards(simulator, passenger.getArrivalFloor());
					break;
				}
				
				//Check if to stop at the next floor
				if (elevator.getState() == State.MOVING) {
					Direction dir = Direction.getDirection(passenger.getArrivalFloor(), passenger.getDestinationFloor());
					
					if (elevator.getDirection() == dir) {
						boolean correctFloor = false;
						
						if (elevator.getDirection() == Direction.UP) {
							correctFloor = elevator.getFloor() + 1 == passenger.getArrivalFloor();
						} else if (elevator.getDirection() == Direction.DOWN) {
							correctFloor = elevator.getFloor() - 1 == passenger.getArrivalFloor();
						}
													
						if (correctFloor) {
							elevator.stopElevatorAtNextFloor();
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {		
		Zone zone = this.getZone(elevatorCar);
		
		int targetFloor = -1;
		
		for (Floor floor : zone.floors) {					
			if (!floor.getWaitingQueue().isEmpty()) {		
				if (targetFloor == -1) {
					targetFloor = floor.getFloorNumber();
					continue;
				}
								
				int delta = Math.abs(floor.getFloorNumber() - elevatorCar.getFloor());				
				int bestDelta = Math.abs(targetFloor - elevatorCar.getFloor());
						
				if (elevatorCar.getFloor() < zone.bottomFloor()) {
					//Below the zone
					if (delta > bestDelta) {
						bestDelta = delta;
						targetFloor = floor.getFloorNumber();
					}
				} else if (elevatorCar.getFloor() > zone.topFloor()) {
					//Over the zone
					if (delta < bestDelta) {
						bestDelta = delta;
						targetFloor = floor.getFloorNumber();
					}
				} else {
					//Inside the zone
					if (floor.getFloorNumber() > targetFloor) {
						targetFloor = floor.getFloorNumber();
					}
				}
			}
		}
		
		if (targetFloor == -1) {
			targetFloor = zone.middleFloor();
		}
		
		elevatorCar.moveTowards(simulator, targetFloor);
	}

	@Override
	public String toString() {
		return "Zoning";
	}
}
