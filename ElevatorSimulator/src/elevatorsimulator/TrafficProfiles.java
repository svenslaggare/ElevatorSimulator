package elevatorsimulator;

import elevatorsimulator.TrafficProfile.Interval;

/**
 * Contains different traffic profiles
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class TrafficProfiles {
	/**
	 * Returns a week day profile
	 */
	public static final TrafficProfile WEEK_DAY_PROFILE;
	
	private TrafficProfiles() {
		
	}
	
	static {
		Interval[] arrivalRates = new Interval[24 * 6];
		double arrival = 0.0;
		double downRate = 0.0;
		
		//00 to 01
		arrivalRates[0] = new Interval(0.002, 0.85, 0.1);
		arrivalRates[1] = new Interval(0.002, 0.85, 0.1);
		arrivalRates[2] = new Interval(0.0015, 0.84, 0.11);
		arrivalRates[3] = new Interval(0.0015, 0.84, 0.11);
		arrivalRates[4] = new Interval(0.001, 0.83, 0.12);
		arrivalRates[5] = new Interval(0.001, 0.82, 0.13);
		
		//01 to 05
		arrival = 0.0005;
		for (int i = 1 * 6; i < 5 * 6; i++) {
			double upRate = 1.0 - i / 35.0;		

			if (i <= 3 * 6) {
				arrival -= 1 / 30000.0;
			} else {
				arrival += 1 / 10000.0;
			}
			
			arrivalRates[i] = new Interval(arrival, upRate, 1.0 - upRate - 0.05);
		}
				
		//05 to 06
		arrivalRates[5 * 6] = new Interval(0.0025, 0.10, 0.85);
		arrivalRates[5 * 6 + 1] = new Interval(0.0035, 0.09, 0.86);
		arrivalRates[5 * 6 + 2] = new Interval(0.0045, 0.08, 0.87);
		arrivalRates[5 * 6 + 3] = new Interval(0.0055, 0.08, 0.87);
		arrivalRates[5 * 6 + 4] = new Interval(0.0065, 0.07, 0.88);
		arrivalRates[5 * 6 + 5] = new Interval(0.0075, 0.06, 0.89);
		
		//06 to 07
		arrivalRates[6 * 6] = new Interval(0.0090, 0.05, 0.90);
		arrivalRates[6 * 6 + 1] = new Interval(0.0105, 0.04, 0.91);
		arrivalRates[6 * 6 + 2] = new Interval(0.0120, 0.04, 0.91);
		arrivalRates[6 * 6 + 3] = new Interval(0.0135, 0.03, 0.92);
		arrivalRates[6 * 6 + 4] = new Interval(0.0150, 0.03, 0.92);
		arrivalRates[6 * 6 + 5] = new Interval(0.0165, 0.03, 0.92);
		
		//07 to 08
		arrivalRates[7 * 6] = new Interval(0.0180, 0.04, 0.91);
		arrivalRates[7 * 6 + 1] = new Interval(0.0195, 0.04, 0.91);
		arrivalRates[7 * 6 + 2] = new Interval(0.0210, 0.04, 0.91);
		arrivalRates[7 * 6 + 3] = new Interval(0.0225, 0.05, 0.90);
		arrivalRates[7 * 6 + 4] = new Interval(0.0240, 0.05, 0.90);
		arrivalRates[7 * 6 + 5] = new Interval(0.0255, 0.05, 0.90);
		
		//08 to 09
		arrivalRates[8 * 6] = new Interval(0.0260, 0.06, 0.89);
		arrivalRates[8 * 6 + 1] = new Interval(0.0245, 0.07, 0.88);
		arrivalRates[8 * 6 + 2] = new Interval(0.0230, 0.08, 0.87);
		arrivalRates[8 * 6 + 3] = new Interval(0.0215, 0.09, 0.86);
		arrivalRates[8 * 6 + 4] = new Interval(0.0200, 0.1, 0.85);
		arrivalRates[8 * 6 + 5] = new Interval(0.0185, 0.11, 0.84);
		
		//09 to 10
		arrivalRates[9 * 6] = new Interval(0.0170, 0.12, 0.83);
		arrivalRates[9 * 6 + 1] = new Interval(0.0155, 0.13, 0.82);
		arrivalRates[9 * 6 + 2] = new Interval(0.0140, 0.14, 0.81);
		arrivalRates[9 * 6 + 3] = new Interval(0.0125, 0.15, 0.80);
		arrivalRates[9 * 6 + 4] = new Interval(0.0110, 0.16, 0.79);
		arrivalRates[9 * 6 + 5] = new Interval(0.00995, 0.17, 0.78);
		
		//10 to 15
		arrival = 0.00995;
		downRate = 0.65;
		for (int i = 10 * 6; i < 15 * 6; i++) {
			downRate -= 0.010;
			
			if (i < 13 * 6) {
				arrival -= 1 / 12000.0;
			} else {
				arrival += 1 / 12000.0;
			}
			
			arrivalRates[i] = new Interval(arrival, 1.0 - downRate - 0.05, downRate);
		}
				
		//15 to 16
		arrivalRates[15 * 6] = new Interval(0.008, 0.6, 0.35);
		arrivalRates[15 * 6 + 1] = new Interval(0.009, 0.61, 0.34);
		arrivalRates[15 * 6 + 2] = new Interval(0.010, 0.62, 0.33);
		arrivalRates[15 * 6 + 3] = new Interval(0.011, 0.63, 0.32);
		arrivalRates[15 * 6 + 4] = new Interval(0.012, 0.64, 0.31);
		arrivalRates[15 * 6 + 5] = new Interval(0.013, 0.65, 0.30);
		
		//16 to 17
		arrivalRates[16 * 6] = new Interval(0.014, 0.66, 0.29);
		arrivalRates[16 * 6 + 1] = new Interval(0.015, 0.67, 0.28);
		arrivalRates[16 * 6 + 2] = new Interval(0.016, 0.68, 0.27);
		arrivalRates[16 * 6 + 3] = new Interval(0.017, 0.69, 0.26);
		arrivalRates[16 * 6 + 4] = new Interval(0.018, 0.70, 0.25);
		arrivalRates[16 * 6 + 5] = new Interval(0.019, 0.71, 0.24);
		
		//17 to 18
		arrivalRates[17 * 6] = new Interval(0.020, 0.72, 0.23);
		arrivalRates[17 * 6 + 1] = new Interval(0.021, 0.73, 0.22);
		arrivalRates[17 * 6 + 2] = new Interval(0.022, 0.74, 0.21);
		arrivalRates[17 * 6 + 3] = new Interval(0.022, 0.75, 0.20);
		arrivalRates[17 * 6 + 4] = new Interval(0.022, 0.74, 0.21);
		arrivalRates[17 * 6 + 5] = new Interval(0.021, 0.73, 0.22);
		
		//18 to 19
		arrivalRates[18 * 6] = new Interval(0.020, 0.72, 0.23);
		arrivalRates[18 * 6 + 1] = new Interval(0.019, 0.71, 0.24);
		arrivalRates[18 * 6 + 2] = new Interval(0.018, 0.70, 0.25);
		arrivalRates[18 * 6 + 3] = new Interval(0.017, 0.69, 0.26);
		arrivalRates[18 * 6 + 4] = new Interval(0.016, 0.68, 0.27);
		arrivalRates[18 * 6 + 5] = new Interval(0.015, 0.67, 0.28);
		
		//19 to 20
		arrivalRates[19 * 6] = new Interval(0.013, 0.66, 0.29);
		arrivalRates[19 * 6 + 1] = new Interval(0.011, 0.65, 0.30);
		arrivalRates[19 * 6 + 2] = new Interval(0.009, 0.64, 0.31);
		arrivalRates[19 * 6 + 3] = new Interval(0.008, 0.63, 0.32);
		arrivalRates[19 * 6 + 4] = new Interval(0.007, 0.62, 0.33);
		arrivalRates[19 * 6 + 5] = new Interval(0.006, 0.61, 0.34);
		
		//20 to 21
		arrivalRates[20 * 6] = new Interval(0.0055, 0.60, 0.35);
		arrivalRates[20 * 6 + 1] = new Interval(0.0050, 0.61, 0.34);
		arrivalRates[20 * 6 + 2] = new Interval(0.0045, 0.62, 0.33);
		arrivalRates[20 * 6 + 3] = new Interval(0.0040, 0.63, 0.32);
		arrivalRates[20 * 6 + 4] = new Interval(0.00375, 0.64, 0.31);
		arrivalRates[20 * 6 + 5] = new Interval(0.00350, 0.65, 0.30);
		
		//21 to 22
		arrivalRates[21 * 6] = new Interval(0.00325, 0.66, 0.29);
		arrivalRates[21 * 6 + 1] = new Interval(0.00315, 0.67, 0.28);
		arrivalRates[21 * 6 + 2] = new Interval(0.00305, 0.68, 0.27);
		arrivalRates[21 * 6 + 3] = new Interval(0.00295, 0.69, 0.26);
		arrivalRates[21 * 6 + 4] = new Interval(0.00285, 0.70, 0.25);
		arrivalRates[21 * 6 + 5] = new Interval(0.00275, 0.71, 0.24);
		
		//22 to 23
		arrivalRates[22 * 6] = new Interval(0.00270, 0.72, 0.23);
		arrivalRates[22 * 6 + 1] = new Interval(0.00265, 0.73, 0.22);
		arrivalRates[22 * 6 + 2] = new Interval(0.00260, 0.74, 0.21);
		arrivalRates[22 * 6 + 3] = new Interval(0.00255, 0.75, 0.20);
		arrivalRates[22 * 6 + 4] = new Interval(0.00250, 0.76, 0.19);
		arrivalRates[22 * 6 + 5] = new Interval(0.00245, 0.77, 0.18);
		
		//23 to 24
		arrivalRates[23 * 6] = new Interval(0.00240, 0.78, 0.17);
		arrivalRates[23 * 6 + 1] = new Interval(0.00235, 0.79, 0.16);
		arrivalRates[23 * 6 + 2] = new Interval(0.00230, 0.80, 0.15);
		arrivalRates[23 * 6 + 3] = new Interval(0.00225, 0.81, 0.14);
		arrivalRates[23 * 6 + 4] = new Interval(0.00216, 0.82, 0.13);
		arrivalRates[23 * 6 + 5] = new Interval(0.00207, 0.83, 0.12);
				
		for (int i = 0; i < arrivalRates.length; i++) {
			if (arrivalRates[i] == null) {
				arrivalRates[i] = new Interval(0, 0, 0);
			}
		}	
		
//		for (int hour = 0; hour < 24; hour++) {
//			double totalAR = 0.0;
//			double totalUp = 0.0;
//			double totalDown = 0.0;
//			double totalInterfloor = 0.0;
//			
//			for (int i = hour * 6; i < (hour + 1) * 6; i++) {
//				Interval interval = arrivalRates[i];
//				totalAR += interval.getAverageArrivalRatio() / 2;
//				totalUp += interval.getUpRate();
//				totalDown += interval.getDownRate();
//				totalInterfloor += interval.getInterfloorRate();
//			}
//			
//			System.out.println(
//				"Hour: " + hour + " AR: " + (totalAR / 6) * 100
//				 + " Up: " + (totalUp / 6) * 100
//				 + " Down: " + (totalDown / 6) * 100
//				 + " Interfloor: " + (totalInterfloor / 6) * 100);
//		}
		
		WEEK_DAY_PROFILE = new TrafficProfile(arrivalRates);
	}
}
