package networklib.synchronic.random;

import java.util.Random;

/**
 * Class to contain a game wide singleton for generating deterministic random numbers.<br>
 * It just uses the algorithms of java.util.Random
 * 
 * @author Andreas Eberle
 * 
 */
public final class RandomSingleton extends Random {
	private static final long serialVersionUID = 3067260303483403560L;

	private static RandomSingleton uniIns;

	private RandomSingleton(long seed) {
		super(seed);
	}

	/**
	 * loads the list of double random numbers in file {@value #RANDOM_FILE}.
	 */
	public static void load(long seed) {
		uniIns = new RandomSingleton(seed);
	}

	/**
	 * 
	 * @return return next double
	 */
	public static double nextD() {
		return uniIns.nextDouble();
	}

	public static float nextF() {
		return uniIns.nextFloat();
	}

	public static RandomSingleton get() {
		return uniIns;
	}

	/**
	 * @param min
	 * @param max
	 * @return returns an int value in the range [min, max]
	 */
	public static int getInt(int min, int max) {
		return min + uniIns.nextInt(max - min + 1);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive).
	 * 
	 * @param n
	 * @return
	 */
	public static int getInt(int n) {
		return uniIns.nextInt(n);
	}
}
