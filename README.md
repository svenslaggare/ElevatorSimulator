# ElevatorSimulator
Implements an elevator simulator with Reinforcement Learning written in Java. The reinforcement learning system is implemented using the [YORLL](http://www.cs.york.ac.uk/rl/software.php) library.

## Features
* Arrivals generated from a Poisson process
* Traffic described by a profile
* Easy to add new scheduling algorithms, buildings and traffic
* Exports statistics as CSV file
* Reinforcement Learning

## Scheduling algorithms
The simulator implements the following algorithms:
* Longest Queue First
* Zoning
* Round Robin
* Up-Peak Group Elevator
* Three Passage Group Elevator
