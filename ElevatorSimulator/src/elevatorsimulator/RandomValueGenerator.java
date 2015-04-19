package elevatorsimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a random value generator
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 * @param <T> The type of the value to generate
 */
public class RandomValueGenerator<T> {
	private Random random;
	private List<ValueProbability<T>> values = new ArrayList<ValueProbability<T>>();
	
	private static class ValueProbability<T> {
		public final double probability;
		public final T value;
		
		public ValueProbability(double probability, T value) {
			this.probability = probability;
			this.value = value;
		}
	}
	
	/**
	 * Creates a new random value generator
	 * @param random The random generator
	 */
	public RandomValueGenerator(Random random) {
		this.random = random;
	}
	
	/**
	 * Adds a new value
	 * @param probability The probability of the value
	 * @param value The value
	 */
	public void addValue(double probability, T value) {
		this.values.add(new ValueProbability<T>(probability, value));
	}
	
	/**
	 * Generates a new random value
	 */
	public T randomValue() {
		double randomValue = this.random.nextDouble();

        for (ValueProbability<T> value : this.values) {
            if (randomValue < value.probability) {
                return value.value;
            }

            randomValue -= value.probability;
        }

        throw new IllegalStateException("The probabilities of the values do not add up to 1.");
	}
}
