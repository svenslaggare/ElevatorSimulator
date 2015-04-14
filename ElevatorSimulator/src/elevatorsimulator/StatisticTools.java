package elevatorsimulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Contains statistics related tools
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class StatisticTools {
	public static void combineGlobal(final String scenarioName) {
		String dataDir = "data";
		
		String[] files = new File(dataDir).list(new FilenameFilter() {		
			@Override
			public boolean accept(File dir, String name) {
				return 
					name.startsWith(scenarioName)
					&& !name.equals(scenarioName + ".csv")
					&& !name.endsWith("-Hour.csv")
					&& !name.endsWith("-SchedulerUsage.csv")
					&& !name.endsWith("-LearningASWT.csv");
			}
		});
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dataDir + "/" + scenarioName + ".csv"));
			boolean hasWrittenDataLine = false;
			
			for (String fileName : files) {
				String[] schedulerNames = fileName.split(scenarioName + "-");
				
				if (schedulerNames.length == 2) {
					String schedulerName = schedulerNames[1].substring(0, schedulerNames[1].length() - 4);
								
					BufferedReader reader = new BufferedReader(new FileReader(dataDir + "/" + fileName));
					String infoLine = reader.readLine();
					
					if (!hasWrittenDataLine) {
						writer.write("Type;" + infoLine + "\n");
						hasWrittenDataLine = true;
					}
					
					String dataLine = reader.readLine();
					writer.write(schedulerName + ";" + dataLine + "\n");
					
					reader.close();
				}
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public static void main(String[] args) {
		combineGlobal("MediumBuilding-2");
		combineGlobal("MediumBuilding-3");
		combineGlobal("LargeBuilding-3");
		combineGlobal("LargeBuilding-4");
	}
}
