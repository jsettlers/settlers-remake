package jsettlers.network.synchronic.random;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	 * loads the list of double random numbers with the given seed.
	 */
	public static void load(long seed) {
		uniIns = new RandomSingleton(seed);
		System.out.println("First random numbers: " + nextD() + " " + nextD() + " " + nextD() + " " + nextD());
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

	public static void serialize(ObjectOutputStream oos) throws IOException {
		oos.writeObject(uniIns);
	}

	public static void deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		uniIns = (RandomSingleton) ois.readObject();
	}

}
