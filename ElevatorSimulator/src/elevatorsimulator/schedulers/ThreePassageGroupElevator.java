package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import elevatorsimulator.Building;
import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCarConfiguration;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.ElevatorCar.State;

/**
 * Implements the 'Three passage group elevator' scheduling algorithm
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 */
public class ThreePassageGroupElevator implements SchedulingAlgorithm {
	private List<ElevatorData> elevators = new ArrayList<ElevatorData>();
	private Map<ElevatorCar, ElevatorData> elevatorToData = new HashMap<>();
	
	/**
	 * The passage type
	 * @author Anton Jansson and Kristoffer Uggla Lingvall
	 *
	 */
	private static enum PassageType {
		P1,
		P2,
		P3;
	}
	
	/**
	 * Represents a passenger call
	 * @author Anton Jansson and Kristoffer Uggla Lingvall
	 *
	 */
	private static class PassengerCall {
		public PassageType type;
		public final Passenger passenger;
		
		/**
		 * Creates a new passenger call
		 * @param type The type of the call
		 * @param passenger The passenger
		 */
		public PassengerCall(PassageType type, Passenger passenger) {
			this.type = type;
			this.passenger = passenger;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result	+ ((passenger == null) ? 0 : passenger.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof PassengerCall)) {
				return false;
			}
			PassengerCall other = (PassengerCall) obj;
			if (passenger == null) {
				if (other.passenger != null) {
					return false;
				}
			} else if (!passenger.equals(other.passenger)) {
				return false;
			}
			if (type != other.type) {
				return false;
			}
			return true;
		}	
		
		@Override
		public String toString() {
			return "{ type: " + this.type + ", travel: " + this.passenger.getArrivalFloor() + "->" + this.passenger.getDestinationFloor() + " }";
		}
	}
	
	/**
	 * Contains data about an elevator car
	 */
	private static class ElevatorData {
		public final ElevatorCar elevatorCar;
		public final Queue<PassengerCall> hallCalls = new LinkedList<PassengerCall>();
		public final Set<PassengerCall> carCalls = new HashSet<PassengerCall>();
		
		public ElevatorData(ElevatorCar elevatorCar) {
			this.elevatorCar = elevatorCar;
		}
	}
	
	/**
	 * Creates a new instance of the 'ThreePassageGroupElevator' class.
	 * @param building The building
	 */
	public ThreePassageGroupElevator(Building building) {
		for (ElevatorCar elevator : building.getElevatorCars()) {
			ElevatorData elevatorData = new ElevatorData(elevator);
			this.elevators.add(elevatorData);
			this.elevatorToData.put(elevator, elevatorData);
		}
	}
	
	/**
	 * Represents a call tuple
	 * @author Anton Jansson and Kristoffer Uggla Lingvall
	 *
	 */
	private static class CallTuple {
		public final ElevatorData elevatorData;
		public final PassengerCall call;
		
		public CallTuple(ElevatorData elevatorData, PassengerCall call) {
			this.elevatorData = elevatorData;
			this.call = call;
		}
	}

	/**
	 * Finds the call for the given passenger
	 * @param passenger The passenger
	 * @return The call or null
	 */
	private CallTuple findCall(Passenger passenger) {
		for (ElevatorData elevatorData : this.elevators) {
			for (PassengerCall hallCall : elevatorData.hallCalls) {
				if (hallCall.passenger == passenger) {
					return new CallTuple(elevatorData, hallCall);
				}
			}
		}
		
		System.out.println(passenger);
		return null;
	}
	
	/**
	 * Finds the hall call for the given passenger
	 * @param elevatorData The elevator data
	 * @param passenger The passenger
	 * @return The hall call or null
	 */
	@SuppressWarnings("unused")
	private PassengerCall findHallCall(ElevatorData elevatorData, Passenger passenger) {
		for (PassengerCall hallCall : elevatorData.hallCalls) {
			if (hallCall.passenger == passenger) {
				return hallCall;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the car call for the given passenger
	 * @param elevatorData The elevator data
	 * @param passenger The passenger
	 * @return The car call or null
	 */
	private PassengerCall findCarCall(ElevatorData elevatorData, Passenger passenger) {
		for (PassengerCall carCall : elevatorData.carCalls) {
			if (carCall.passenger == passenger) {
				return carCall;
			}
		}
		
		return null;
	}
	
	/**
	 * Determines the type of call
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	private PassageType getType(ElevatorCar elevatorCar, Passenger passenger) {
		Direction passDir = passenger.getDirection();
		Direction carDir = elevatorCar.getDirection();
		
		int currentFloor = elevatorCar.nextFloor();	
		boolean carIsOver = currentFloor > passenger.getArrivalFloor();
		boolean carIsUnder = currentFloor < passenger.getArrivalFloor();
		
		if (carIsOver) {
			if (passDir == carDir) {
				if (carDir == Direction.DOWN) {
					return PassageType.P1;
				} else {
					return PassageType.P3;
				}
			} else {
				return PassageType.P2;
			}
		} else if (carIsUnder) {
			if (passDir == carDir) {
				if (carDir == Direction.UP) {
					return PassageType.P1;
				} else {
					return PassageType.P3;
				}
			} else {
				return PassageType.P2;
			}
		} else {			
			//Same floor
			if (elevatorCar.getState() == State.IDLE
				|| elevatorCar.getState() == State.STOPPED
				|| elevatorCar.getState() == State.DECELERATING) {
				return PassageType.P1;
			}
			
			if (passDir == carDir) {
				return PassageType.P1;
			} else {
				return PassageType.P2;
			}
		}
	}
	
	/**
	 * Finds the car calls between the given floors
	 * @param simulator The simulator
	 * @param minFloor The min floor
	 * @param maxFloor The max floor
	 */
	private Set<Passenger> getCarCalls(Simulator simulator, int minFloor, int maxFloor) {
		Set<Passenger> carCalls = new HashSet<>();
		
		for (ElevatorCar elevatorCar : simulator.getBuilding().getElevatorCars()) {
			for (Passenger passenger : elevatorCar.getPassengers()) {
				if (passenger.getDestinationFloor() > minFloor && passenger.getDestinationFloor() < maxFloor) {
					carCalls.add(passenger);
				}
			}
		}	
		
		return carCalls;
	}
	
	/**
	 * Finds the hall calls between the given floors
	 * @param simulator The simulator
	 * @param minFloor The min floor
	 * @param maxFloor The max floor
	 */
	private Set<Passenger> getHallCalls(Simulator simulator, int minFloor, int maxFloor) {
		Set<Passenger> hallCalls = new HashSet<>();
		
		for (ElevatorData elevatorData : this.elevators) {
			for (PassengerCall hallCall : elevatorData.hallCalls) {
				Passenger passenger = hallCall.passenger;
				if (passenger.getDestinationFloor() > minFloor && passenger.getDestinationFloor() < maxFloor) {
					hallCalls.add(passenger);
				}
			}
		}
		
		return hallCalls;
	}
	
	private static class Tuple {
		public final int first;
		public final int second;
		
		public Tuple(int first, int second) {
			this.first = first;
			this.second = second;
		}
	}
	
	private int calculateNkPass(Simulator simulator, Passenger passengerToHandle) {
		int nkPass = 0;
		
		for (Passenger passenger : simulator.getControlSystem().getHallQueue()) {
			if (passenger.getArrivalFloor() == passengerToHandle.getArrivalFloor()
				&& passenger.getDirection() == passengerToHandle.getDirection()) {
				nkPass++;
			}
		}
		
		return nkPass;
	}
		
	private Tuple calculateFi(Building building, int floor, Direction dir) {
		int fik = 0;
		int fiActual = 0;
		
		if (dir == Direction.UP) {
			fik = building.numFloors() - floor;
			fiActual = building.numFloors();
		} else if (dir == Direction.DOWN) {
			fik = floor;
			fiActual = 0;
		} else {
			fik = Math.max(building.numFloors() - floor, floor);
			
			if (fik == floor) {
				fiActual = 0;
			} else {
				fiActual = building.numFloors() - 1;
			}
		}
		
		if (fik == 0) {
			fik = building.numFloors();
		}
		
		return new Tuple(fik, fiActual);
	}
		
	private double calculatePik(int nkPass, double fik) {
		if (nkPass == 1) {
			return 1.0 - 1.0 / fik;
		} else {
			return Math.exp(-(double)nkPass / fik);
		}
	}
	
	private double calculateSik(double fik, double Pik) {
		return fik * (1.0 - Pik);
	}
	
	private double calculateLiNet(int fik, double Pik) {
		double liNet = 0.0;
		if (fik > 1) {
			for (int l = 2; l <= fik; l++) {
				double product = 1;
				
				for (int j = fik - l + 1; j <= fik; j++) {
					product *= Pik;
				}
				
				liNet += product;
			}
		}
		
		return liNet;
	}
	
	private double calculateFiFarthest(int fiActual, double liNet) {
		return fiActual - liNet;
	}
	
	private int calculateSkjManadatory(Simulator simulator, int k, int j) {
		Set<Passenger> Ckj = this.getCarCalls(simulator, k, j);
		Set<Passenger> Hkj = this.getHallCalls(simulator, k, j);
		Set<Passenger> CHkj = new HashSet<>();
		CHkj.addAll(Ckj);
		CHkj.addAll(Hkj);
		
		return Ckj.size() + Hkj.size() - CHkj.size();
	}
		
	private double calculateSkjExtra(double sik, double skjMandatory, double fik, int k, int j) {
		return sik * ((double)Math.abs(j - k) - 1.0 - skjMandatory) / (double)fik;
	}
	
	private double calculateStopTime(double Pik, double sik, double liNet, double fiFarthest, double skjMandatory, double skjExtra) {		
		return Pik + sik + liNet + fiFarthest + skjMandatory + skjExtra;
	}
		
	private double calculateTiAttending(Simulator simulator, int elevatorFloor, Direction elevatorDir, Passenger passengerToHandle, PassageType callType, Set<Integer> CiBefore, Set<Integer> HiBefore) {
		double tikNonstop = 
				Math.abs(passengerToHandle.getDestinationFloor() - elevatorFloor)
				* ElevatorCarConfiguration.defaultConfiguration().getFloorTime();
		
		double sikExtraSum = 0;
		int k = passengerToHandle.getArrivalFloor();
		int j = passengerToHandle.getDestinationFloor();
		int nkPass = this.calculateNkPass(simulator, passengerToHandle);
		Tuple fi = this.calculateFi(simulator.getBuilding(), elevatorFloor, elevatorDir);
		int fik = fi.first;
		int fiActual = fi.second;
		double Pik = this.calculatePik(nkPass, fik);
		double sik = this.calculateSik(fik, Pik);
		double skjMandatory = this.calculateSkjManadatory(simulator, k, j);
		double skjExtra = this.calculateSkjExtra(sik, skjMandatory, fik, k, j);
		double liNet = this.calculateLiNet(fik, Pik);
		double fiFarthest = this.calculateFiFarthest(fiActual, liNet);
		
		for (int floor : HiBefore) {
			double skjMandatoryFloor = this.calculateSkjManadatory(simulator, floor, passengerToHandle.getArrivalFloor());
			double skjExtraFloor = this.calculateSkjExtra(sik, skjMandatoryFloor, fik, floor, passengerToHandle.getArrivalFloor());
			
			if (skjExtraFloor < 0) {
				skjExtraFloor *= 1;
			}
			
			sikExtraSum += skjExtraFloor;
		}
				
		double ts = this.calculateStopTime(Pik, sik, liNet, fiFarthest, skjMandatory, skjExtra);
		
		Set<Integer> CHiBefore = new HashSet<>();
		CHiBefore.addAll(CiBefore);
		CHiBefore.addAll(HiBefore);
		
		double tiAttending = tikNonstop + (CiBefore.size() + HiBefore.size() - CHiBefore.size() + sikExtraSum) * ts;
		
		if (tiAttending < 0) {
			System.out.println(
				"Negative tiAttending: (" + tiAttending + "): "
				+ passengerToHandle.getArrivalFloor() + "->" + passengerToHandle.getDestinationFloor()
				+ " (Elevator floor: " + elevatorFloor + ")"
				+ " Elevator dir: " + elevatorDir
				+ " Passenger dir: " + passengerToHandle.getDirection()
				+ " Call type: " + callType
				+ " HiBefore: " + HiBefore);
		}
		
		return tiAttending;		
	}
	
	private double calculateTiAttending(Simulator simulator, ElevatorCar elevatorCar, Passenger passengerToHandle, PassageType callType, Set<Integer> CiBefore, Set<Integer> HiBefore) {
		Direction elevatorDir = elevatorCar.getDirection();
		
		if (elevatorDir == Direction.NONE) {
			elevatorDir = passengerToHandle.getDirection();
		}

		return calculateTiAttending(simulator, elevatorCar.getFloor(), elevatorDir, passengerToHandle, callType, CiBefore, HiBefore);
	}
	
	private double calculateTiAttending(Simulator simulator, ElevatorData elevatorData, Passenger passengerToHandle, PassageType callType) {
		Direction elevatorDir = elevatorData.elevatorCar.getDirection();
		
		switch (callType) {
		case P1:
			{
				Set<Integer> CiBefore = new HashSet<>();		
				int callDelta = Math.abs(elevatorData.elevatorCar.getFloor() - passengerToHandle.getDestinationFloor());
				
				for (PassengerCall carCall : elevatorData.carCalls) {
					if (carCall.type == PassageType.P1) {
						int delta = Math.abs(elevatorData.elevatorCar.getFloor() - carCall.passenger.getDestinationFloor());
						
						if (delta < callDelta) {					
							CiBefore.add(carCall.passenger.getDestinationFloor());
						}
					}
				}
							
				callDelta = Math.abs(elevatorData.elevatorCar.getFloor() - passengerToHandle.getArrivalFloor());
				Set<Integer> HiBefore = new HashSet<>();
				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (hallCall.type == PassageType.P1) {
						int delta = Math.abs(elevatorData.elevatorCar.getFloor() - hallCall.passenger.getArrivalFloor());
						
						if (delta < callDelta) {	
							HiBefore.add(hallCall.passenger.getArrivalFloor());
						}
					}
				}
												
				return this.calculateTiAttending(
					simulator,
					elevatorData.elevatorCar,
					passengerToHandle,
					callType,
					CiBefore,
					HiBefore);
			}
		case P2:
			{
				//Check if any P1 calls exist
				boolean existsP1 = false;
				for (PassengerCall carCall : elevatorData.carCalls) {
					if (carCall.type == PassageType.P1) {
						existsP1 = true;
						break;
					}
				}
				
				if (!existsP1) {
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P1) {
							existsP1 = true;
							break;
						}
					}
				}
				
				//First case
				if (!existsP1) {				
					//Calculate the reversal floor
					int reversalFloor = -1;
					
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P2) {
							if (reversalFloor == -1) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
								continue;
							}
							
							if (elevatorDir == Direction.UP) {
								if (hallCall.passenger.getArrivalFloor() > reversalFloor) {
									reversalFloor = hallCall.passenger.getArrivalFloor();
								}
							} else if (elevatorDir == Direction.DOWN) {
								if (hallCall.passenger.getArrivalFloor() < reversalFloor) {
									reversalFloor = hallCall.passenger.getArrivalFloor();
								}
							}
						}
					}
					
					Set<Integer> CiBefore = new HashSet<>();		
					
					int callDelta = Math.abs(reversalFloor - passengerToHandle.getDestinationFloor());
					
					for (PassengerCall carCall : elevatorData.carCalls) {
						if (carCall.type == PassageType.P2) {
							int delta = Math.abs(reversalFloor - carCall.passenger.getDestinationFloor());
							
							if (delta < callDelta) {					
								CiBefore.add(carCall.passenger.getDestinationFloor());
							}
						}
					}
								
					callDelta = Math.abs(reversalFloor - passengerToHandle.getArrivalFloor());
					Set<Integer> HiBefore = new HashSet<>();
					
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P2) {
							int delta = Math.abs(reversalFloor - hallCall.passenger.getArrivalFloor());
							
							if (delta < callDelta) {	
								HiBefore.add(hallCall.passenger.getArrivalFloor());
							}
						}
					}
										
					double tiAttending = this.calculateTiAttending(
						simulator,
						elevatorData.elevatorCar,
						passengerToHandle,
						callType,
						CiBefore,
						HiBefore);
										
					return tiAttending;
				} else {
					//The second case
					
					//Part 1
					Set<Integer> CiBefore = new HashSet<>();		
					for (PassengerCall carCall : elevatorData.carCalls) {
						if (passengerToHandle.getArrivalFloor() != carCall.passenger.getDestinationFloor()) {
							CiBefore.add(carCall.passenger.getDestinationFloor());
						}
					}
					
					Set<Integer> HiBefore = new HashSet<>();				
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P1 && passengerToHandle.getArrivalFloor() != hallCall.passenger.getArrivalFloor()) {
							HiBefore.add(hallCall.passenger.getArrivalFloor());
						}
					}
					
					double tiAttendingPart1 = this.calculateTiAttending(
						simulator,
						elevatorData.elevatorCar,
						passengerToHandle,
						callType,
						CiBefore,
						HiBefore);
					
					//Part 2				
					//Calculate the reversal floor
					int reversalFloor = -1;
					
					for (PassengerCall carCall : elevatorData.carCalls) {
						if (carCall.type == PassageType.P1) {
							if (reversalFloor == -1) {
								reversalFloor = carCall.passenger.getDestinationFloor();
								continue;
							}
							
							if (elevatorDir == Direction.UP) {
								if (carCall.passenger.getDestinationFloor() > reversalFloor) {
									reversalFloor = carCall.passenger.getDestinationFloor();
								}
							} else if (elevatorDir == Direction.DOWN) {
								if (carCall.passenger.getDestinationFloor() < reversalFloor) {
									reversalFloor = carCall.passenger.getDestinationFloor();
								}
							}
						}
					}
					
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P2) {
							if (reversalFloor == -1) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
								continue;
							}
							
							if (elevatorDir == Direction.UP) {
								if (hallCall.passenger.getArrivalFloor() > reversalFloor) {
									reversalFloor = hallCall.passenger.getArrivalFloor();
								}
							} else if (elevatorDir == Direction.DOWN) {
								if (hallCall.passenger.getArrivalFloor() < reversalFloor) {
									reversalFloor = hallCall.passenger.getArrivalFloor();
								}
							}
						}
					}
					
					CiBefore.clear();
					HiBefore.clear();
					
					int callDelta = Math.abs(reversalFloor - passengerToHandle.getArrivalFloor());
					for (PassengerCall hallCall : elevatorData.hallCalls) {
						if (hallCall.type == PassageType.P2) {
							int delta = Math.abs(reversalFloor - hallCall.passenger.getArrivalFloor());
							
							if (delta < callDelta) {	
								HiBefore.add(hallCall.passenger.getArrivalFloor());
							}
						}
					}
					
					double tiAttendingPart2 = this.calculateTiAttending(
						simulator,
						reversalFloor,
						elevatorData.elevatorCar.getDirection().oppositeDir(),
						passengerToHandle,
						callType,
						CiBefore,
						HiBefore);			
										
					return tiAttendingPart1 + tiAttendingPart2;
				}
			}
		case P3:
			{
				//The third case
				//Part 1
				Set<Integer> CiBefore = new HashSet<>();		
				for (PassengerCall carCall : elevatorData.carCalls) {
					if (passengerToHandle.getArrivalFloor() != carCall.passenger.getDestinationFloor()) {
						CiBefore.add(carCall.passenger.getDestinationFloor());
					}
				}
				
				Set<Integer> HiBefore = new HashSet<>();				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (hallCall.type == PassageType.P1 && passengerToHandle.getArrivalFloor() != hallCall.passenger.getArrivalFloor()) {
						HiBefore.add(hallCall.passenger.getArrivalFloor());
					}
				}
				
				double tiAttendingPart1 = this.calculateTiAttending(simulator, elevatorData.elevatorCar, passengerToHandle, callType, CiBefore, HiBefore);
				HiBefore.clear();
				CiBefore.clear();
				
				//Part 2
				//Calculate the reversal floor
				int reversalFloor = -1;
				
				for (PassengerCall carCall : elevatorData.carCalls) {
					if (carCall.type == PassageType.P1) {
						if (reversalFloor == -1) {
							reversalFloor = carCall.passenger.getDestinationFloor();
							continue;
						}
						
						if (elevatorDir == Direction.UP) {
							if (carCall.passenger.getDestinationFloor() > reversalFloor) {
								reversalFloor = carCall.passenger.getDestinationFloor();
							}
						} else if (elevatorDir == Direction.DOWN) {
							if (carCall.passenger.getDestinationFloor() < reversalFloor) {
								reversalFloor = carCall.passenger.getDestinationFloor();
							}
						}
					}
				}
				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (hallCall.type == PassageType.P2) {
						if (reversalFloor == -1) {
							reversalFloor = hallCall.passenger.getArrivalFloor();
							continue;
						}
						
						if (elevatorDir == Direction.UP) {
							if (hallCall.passenger.getArrivalFloor() > reversalFloor) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
							}
						} else if (elevatorDir == Direction.DOWN) {
							if (hallCall.passenger.getArrivalFloor() < reversalFloor) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
							}
						}
					}
				}
				
				CiBefore.clear();
				HiBefore.clear();
				
				int callDelta = Math.abs(reversalFloor - passengerToHandle.getArrivalFloor());
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (hallCall.type == PassageType.P2) {
						int delta = Math.abs(reversalFloor - hallCall.passenger.getArrivalFloor());
						
						if (delta < callDelta) {	
							HiBefore.add(hallCall.passenger.getArrivalFloor());
						}
					}
				}
				
				double tiAttendingPart2 = this.calculateTiAttending(
					simulator,
					reversalFloor,
					elevatorData.elevatorCar.getDirection().oppositeDir(),
					passengerToHandle,
					callType,
					CiBefore,
					HiBefore);
				
				//Part 3
				reversalFloor = -1;
				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (reversalFloor == -1) {
						if (hallCall.type == PassageType.P2) {
							reversalFloor = hallCall.passenger.getDestinationFloor();
						} else if (hallCall.type == PassageType.P3) {
							reversalFloor = hallCall.passenger.getArrivalFloor();
						}
						continue;
					}
					
					if (hallCall.type == PassageType.P2) {						
						if (elevatorDir == Direction.UP) {
							if (hallCall.passenger.getDestinationFloor() > reversalFloor) {
								reversalFloor = hallCall.passenger.getDestinationFloor();
							}
						} else if (elevatorDir == Direction.DOWN) {
							if (hallCall.passenger.getDestinationFloor() < reversalFloor) {
								reversalFloor = hallCall.passenger.getDestinationFloor();
							}
						}
					} else if (hallCall.type == PassageType.P3) {
						if (elevatorDir == Direction.UP) {
							if (hallCall.passenger.getArrivalFloor() < reversalFloor) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
							}
						} else if (elevatorDir == Direction.DOWN) {
							if (hallCall.passenger.getArrivalFloor() > reversalFloor) {
								reversalFloor = hallCall.passenger.getArrivalFloor();
							}
						}
					}
				}
				
				CiBefore.clear();
				HiBefore.clear();
				
				callDelta = Math.abs(reversalFloor - passengerToHandle.getArrivalFloor());
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (hallCall.type == PassageType.P3) {
						int delta = Math.abs(reversalFloor - hallCall.passenger.getArrivalFloor());
						
						if (delta < callDelta) {	
							HiBefore.add(hallCall.passenger.getArrivalFloor());
						}
					}
				}
				
				double tiAttendingPart3 = this.calculateTiAttending(
					simulator,
					reversalFloor,
					elevatorData.elevatorCar.getDirection().oppositeDir(),
					passengerToHandle,
					callType,
					CiBefore,
					HiBefore);
				
				return tiAttendingPart1 + tiAttendingPart2 + tiAttendingPart3;
			}
		default:
			return 0.0;
		}
	}
	
	private double calculateTijDelay(Simulator simulator, ElevatorData elevatorData, Passenger newPassenger, PassageType newPassengerCallType, PassengerCall passengerJ) {
		int numExtraStops = 0;
		boolean floorExists = false;
		
		for (PassengerCall carCall : elevatorData.carCalls) {
			if (passengerJ.passenger.getArrivalFloor() == carCall.passenger.getDestinationFloor()) {
				floorExists = true;
				break;
			}
		}
		
		if (floorExists) {
			numExtraStops++;
		}
		
		int delta = 0;
		
		//Calculate the reversal floor
		int currentReversalFloor = -1;
		
		for (PassengerCall carCall : elevatorData.carCalls) {
			if (carCall.type == PassageType.P1) {
				if (currentReversalFloor == -1) {
					currentReversalFloor = carCall.passenger.getDestinationFloor();
					continue;
				}
				
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (carCall.passenger.getDestinationFloor() > currentReversalFloor) {
						currentReversalFloor = carCall.passenger.getDestinationFloor();
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (carCall.passenger.getDestinationFloor() < currentReversalFloor) {
						currentReversalFloor = carCall.passenger.getDestinationFloor();
					}
				}
			}
		}
		
		for (PassengerCall hallCall : elevatorData.hallCalls) {
			if (hallCall.type == PassageType.P2) {
				if (currentReversalFloor == -1) {
					currentReversalFloor = hallCall.passenger.getArrivalFloor();
					continue;
				}
				
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (hallCall.passenger.getArrivalFloor() > currentReversalFloor) {
						currentReversalFloor = hallCall.passenger.getArrivalFloor();
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (hallCall.passenger.getArrivalFloor() < currentReversalFloor) {
						currentReversalFloor = hallCall.passenger.getArrivalFloor();
					}
				}
			}
		}
		
		if (passengerJ.type == PassageType.P2 && newPassengerCallType != PassageType.P3) {						
			if (newPassengerCallType == PassageType.P1) {
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (newPassenger.getDestinationFloor() > currentReversalFloor) {
						delta = newPassenger.getDestinationFloor() - currentReversalFloor;
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (newPassenger.getDestinationFloor() < currentReversalFloor) {
						delta = currentReversalFloor - newPassenger.getDestinationFloor();
					}
				}
			} else {
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (newPassenger.getArrivalFloor() > currentReversalFloor) {
						delta = newPassenger.getArrivalFloor() - currentReversalFloor;
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (newPassenger.getArrivalFloor() < currentReversalFloor) {
						delta = currentReversalFloor - newPassenger.getArrivalFloor();
					}
				}
			}
		} else if (passengerJ.type == PassageType.P3) {
			if (newPassengerCallType == PassageType.P1) {
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (newPassenger.getDestinationFloor() > currentReversalFloor) {
						delta = newPassenger.getDestinationFloor() - currentReversalFloor;
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (newPassenger.getDestinationFloor() < currentReversalFloor) {
						delta = currentReversalFloor - newPassenger.getDestinationFloor();
					}
				}
			} else if (newPassengerCallType == PassageType.P2) {
				if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
					if (newPassenger.getArrivalFloor() > currentReversalFloor) {
						delta = newPassenger.getArrivalFloor() - currentReversalFloor;
					}
				} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
					if (newPassenger.getArrivalFloor() < currentReversalFloor) {
						delta = currentReversalFloor - newPassenger.getArrivalFloor();
					}
				}
			}
			
			//If the passenger did not affect the first reversal floor, calculate if it will affect the second
			if (delta == 0) {
				currentReversalFloor = -1;
				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					if (currentReversalFloor == -1) {
						if (hallCall.type == PassageType.P2) {
							currentReversalFloor = hallCall.passenger.getDestinationFloor();
						} else if (hallCall.type == PassageType.P3) {
							currentReversalFloor = hallCall.passenger.getArrivalFloor();
						}
						continue;
					}
					
					if (hallCall.type == PassageType.P2) {						
						if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
							if (hallCall.passenger.getDestinationFloor() > currentReversalFloor) {
								currentReversalFloor = hallCall.passenger.getDestinationFloor();
							}
						} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
							if (hallCall.passenger.getDestinationFloor() < currentReversalFloor) {
								currentReversalFloor = hallCall.passenger.getDestinationFloor();
							}
						}
					} else if (hallCall.type == PassageType.P3) {
						if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
							if (hallCall.passenger.getArrivalFloor() < currentReversalFloor) {
								currentReversalFloor = hallCall.passenger.getArrivalFloor();
							}
						} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
							if (hallCall.passenger.getArrivalFloor() > currentReversalFloor) {
								currentReversalFloor = hallCall.passenger.getArrivalFloor();
							}
						}
					}
				}
				
				if (newPassengerCallType == PassageType.P2) {
					if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
						if (newPassenger.getDestinationFloor() < currentReversalFloor) {
							delta = currentReversalFloor - newPassenger.getDestinationFloor();
						}
					} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
						if (newPassenger.getDestinationFloor() > currentReversalFloor) {
							delta = newPassenger.getDestinationFloor() - currentReversalFloor;
						}
					}
				} else if (newPassengerCallType == PassageType.P3) {
					if (elevatorData.elevatorCar.getDirection() == Direction.UP) {
						if (newPassenger.getArrivalFloor() < currentReversalFloor) {
							delta = currentReversalFloor - newPassenger.getArrivalFloor();
						}
					} else if (elevatorData.elevatorCar.getDirection() == Direction.DOWN) {
						if (newPassenger.getArrivalFloor() > currentReversalFloor) {
							delta = newPassenger.getArrivalFloor() - currentReversalFloor;
						}
					}
				}
			}
		}
		
		double stopTime = 
			ElevatorCarConfiguration.defaultConfiguration().getStartTime()
			+ ElevatorCarConfiguration.defaultConfiguration().getStopTime()
			+ ElevatorCarConfiguration.defaultConfiguration().getFloorTime()
			+ 2 * ElevatorCarConfiguration.defaultConfiguration().getDoorTime();
		
		return 
			ElevatorCarConfiguration.defaultConfiguration().getFloorTime()
			* delta * 2
			+ numExtraStops * stopTime;
	}

	private double calculateTijSum(Simulator simulator, ElevatorData elevatorData, Passenger newPassenger, PassageType newPassengerCallType) {
		double sum = 0.0;
		
		for (PassengerCall hallCall : elevatorData.hallCalls) {
			int nJPass = this.calculateNkPass(simulator, hallCall.passenger);
			sum += this.calculateTijDelay(simulator, elevatorData, newPassenger, newPassengerCallType, hallCall) * nJPass;
		}
		
		return sum;
	}
	
	private double calculateTiTotal(Simulator simulator, ElevatorData elevatorData, Passenger newPassenger, PassageType newPassengerCallType) {
		return 
			this.calculateTijSum(simulator, elevatorData, newPassenger, newPassengerCallType)
			+ this.calculateTiAttending(simulator, elevatorData, newPassenger, newPassengerCallType);
	}
	
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		ElevatorData bestElevatorData = null;
		PassageType bestType = PassageType.P3;
		double lowestETA = Double.MAX_VALUE;
		
		for (ElevatorData elevatorData : this.elevators) {
			PassageType type = this.getType(elevatorData.elevatorCar, passenger);
						
			double eta = this.calculateTiTotal(simulator, elevatorData, passenger, type);
			
			if (eta < lowestETA) {
				lowestETA = eta;
				bestType = type;
				bestElevatorData = elevatorData;
			}
		}
		
		bestElevatorData.hallCalls.add(new PassengerCall(bestType, passenger));
	}
	
	@Override
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {				
		CallTuple callData = this.findCall(passenger);
		ElevatorData elevatorData = null;
		
		if (callData == null) {
			System.out.println("wololo");
		}
		
		if (callData.elevatorData.elevatorCar == elevatorCar) {
			elevatorData = callData.elevatorData;
		} else {
			elevatorData = this.elevatorToData.get(elevatorCar);
		}
		
		callData.elevatorData.hallCalls.remove(callData.call);
		elevatorData.carCalls.add(callData.call);
	}
	
	@Override
	public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {
		ElevatorData elevatorData = this.elevatorToData.get(elevatorCar);
		PassengerCall carCall = this.findCarCall(elevatorData, passenger);
		
		if (carCall != null) {
			elevatorData.carCalls.remove(carCall);
		}
	}

	@Override
	public void update(Simulator simulator) {
		for (ElevatorData elevatorData : this.elevators) {
			ElevatorCar elevatorCar = elevatorData.elevatorCar;	
			
			if (elevatorCar.getState() == State.MOVING) {
				boolean stopAtNext = false;
				
				for (PassengerCall hallCall : elevatorData.hallCalls) {
					Passenger passenger = hallCall.passenger;
					PassageType type = hallCall.type;
					
					if (type == PassageType.P1) {
						if (passenger.getArrivalFloor() == elevatorCar.nextFloor()) {
							stopAtNext = true;
							break;
						}
					}
				}
				
				if (stopAtNext) {
					elevatorCar.stopElevatorAtNextFloor();
				}
			} else if (elevatorCar.getState() == State.IDLE) {						
				Passenger toHandle = null;
				if (!elevatorData.hallCalls.isEmpty()) {
					toHandle = elevatorData.hallCalls.peek().passenger;
				}
				
				if (toHandle != null) {
					elevatorCar.moveTowards(simulator, toHandle.getArrivalFloor());
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {

	}
	
	@Override
	public void onTurned(Simulator simulator, ElevatorCar elevatorCar) {
		ElevatorData elevatorData = this.elevatorToData.get(elevatorCar);
		
		for (PassengerCall hallCall : elevatorData.hallCalls) {
			hallCall.type = this.getType(elevatorCar, hallCall.passenger);
		}
	}
	
	/**
	 * Indicates that the simulator switched to the current scheduler
	 * @param simulator The simulator
	 */
	@Override
	public void changedTo(Simulator simulator) {
		//Clear all queues
		for (ElevatorData elevatorData : this.elevators) {
			elevatorData.carCalls.clear();
			elevatorData.hallCalls.clear();
		}
		
		//Add for passenger insides elevators
		for (ElevatorData elevatorData : this.elevators) {
			for (Passenger passenger : elevatorData.elevatorCar.getPassengers()) {
				elevatorData.carCalls.add(new PassengerCall(PassageType.P1, passenger));
			}
		}
		
		//Add for passengers waiting
		for (Passenger passenger : simulator.getControlSystem().getHallQueue()) {
			this.passengerArrived(simulator, passenger);
		}
	}
		
	@Override
	public String toString() {
		return "Three Passage Group Elevator";
	}
}
