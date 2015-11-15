/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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

	public static boolean getBoolean() {
		return getInt(0, 1) == 0;
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
