package marl.utility;

import java.util.Arrays;
import java.util.Random;

/**
 * The singleton Random object, so that all parts of the system can have access
 * to the same Random object.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public enum Rand {
	INSTANCE;
	
	
	private Random random = new Random();
	public void setSeed(long seed)
	{
		random.setSeed(seed);
	}
	public int nextInt()
	{
		return random.nextInt();
	}
	public int nextInt(int max)
	{
		return random.nextInt(max);
	}
	public double nextDouble()
	{
		return random.nextDouble();
	}
	public double nextDouble(double min, double max)
	{
		return min + (random.nextDouble() * (max - min));
	}
	
	public double nextGaussian()
	{
	    return random.nextGaussian();
	}
    
    
    /**
     * Select a random index, using the given probability vector
     * @param probabilities e.g. [0.2, 0.3, 0.5]. 
     * @param sumOfProbabilities the sum of the probabilities (e.g. 1.0).
     * @return 0 with probability 0.2, 1 with prob 0.3, 2 with prob 0.5.
     * @author Erel Segal the Levite
     * @since 2013-01-09
     */
    public int randomIndex(double[] probabilities, double sumOfProbabilities) {
        double selector = random.nextDouble() * sumOfProbabilities;
        for (int i=0; i<probabilities.length; i++) {
            if (selector <= probabilities[i]) {// select the ith gaussian
                return i;
            } else {
                selector = selector - probabilities[i];
            }
        }
        throw new IllegalArgumentException("Couldn't find a random index - probably the probabilities don't sum to "+sumOfProbabilities+"!"+Arrays.toString(probabilities));
    }
    
    /**
     * Select a random index, using the given probability vector
     * @param probabilities e.g. [0.2, 0.3, 0.5]. The sum must be 1. 
     * @return 0 with probability 0.2, 1 with prob 0.3, 2 with prob 0.5.
     * @author Erel Segal the Levite
     * @since 2013-01-09
     */
    public int randomIndex(double[] probabilities) {
        return randomIndex(probabilities, 1.0);
    }

}
