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
package jsettlers.common.utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class defines a tupel of two values.
 * 
 * @author Andreas Eberle
 * 
 * @param <S>
 * @param <T>
 */
public class Tuple<S, T> implements Serializable {
	private static final long serialVersionUID = -1637245486740963305L;

	public final S e1;
	public final T e2;

	public Tuple(S element1, T element2) {
		this.e1 = element1;
		this.e2 = element2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((e1 == null) ? 0 : e1.hashCode());
		result = prime * result + ((e2 == null) ? 0 : e2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<?, ?> other = (Tuple<?, ?>) obj;
		if (e1 == null) {
			if (other.e1 != null)
				return false;
		} else if (!e1.equals(other.e1))
			return false;
		if (e2 == null) {
			if (other.e2 != null)
				return false;
		} else if (!e2.equals(other.e2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tuple (" + e1 + ", " + e2 + ")";
	}

	/**
	 * 
	 * @return A {@link Comparator} that compares the first element of {@link Tuple}s.
	 */
	public static <A extends Comparable<A>, B> Comparator<Tuple<A, B>> getE1Comparator() {
		return new Comparator<Tuple<A, B>>() {
			@Override
			public int compare(Tuple<A, B> arg0, Tuple<A, B> arg1) {
				return arg0.e1.compareTo(arg1.e1);
			}
		};
	}

	/**
	 * 
	 * @return A {@link Comparator} that compares the second element of {@link Tuple}s.
	 */
	public static <A, B extends Comparable<B>> Comparator<Tuple<A, B>> getE2Comparator() {
		return new Comparator<Tuple<A, B>>() {
			@Override
			public int compare(Tuple<A, B> arg0, Tuple<A, B> arg1) {
				return arg0.e2.compareTo(arg1.e2);
			}
		};
	}
}
