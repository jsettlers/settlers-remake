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
package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This class lets you fade the landscape.
 * 
 * @author michael
 */
public class LandscapeFader {
	private static final FadableLandscapes[] allowed =
			new FadableLandscapes[] {
					new FadableLandscapes(ELandscapeType.SAND, ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.DRY_GRASS,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.DESERT,
							ELandscapeType.DRY_GRASS),
					new FadableLandscapes(ELandscapeType.FLATTENED,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.SAND,
							ELandscapeType.WATER1),
					new FadableLandscapes(ELandscapeType.SNOW,
							ELandscapeType.MOUNTAIN),
					new FadableLandscapes(ELandscapeType.MOUNTAIN,
							ELandscapeType.MOUNTAINBORDER),
					new FadableLandscapes(ELandscapeType.MOUNTAINBORDER,
							ELandscapeType.MOUNTAINBORDEROUTER),
					new FadableLandscapes(ELandscapeType.MOUNTAINBORDEROUTER,
							ELandscapeType.GRASS),

					new FadableLandscapes(ELandscapeType.EARTH,
							ELandscapeType.GRASS),

					new FadableLandscapes(ELandscapeType.MOOR,
							ELandscapeType.MOORBORDER),
					new FadableLandscapes(ELandscapeType.GRASS,
							ELandscapeType.MOORBORDER),

					new FadableLandscapes(ELandscapeType.GRAVEL,
							ELandscapeType.MOUNTAINBORDER),

					new FadableLandscapes(ELandscapeType.SHARP_FLATTENED_DESERT,
							ELandscapeType.DESERT),
					new FadableLandscapes(ELandscapeType.FLATTENED_DESERT,
							ELandscapeType.DESERT),

					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.RIVER2),
					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.RIVER3),
					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.RIVER4),
					new FadableLandscapes(ELandscapeType.RIVER2,
							ELandscapeType.RIVER3),
					new FadableLandscapes(ELandscapeType.RIVER2,
							ELandscapeType.RIVER4),
					new FadableLandscapes(ELandscapeType.RIVER3,
							ELandscapeType.RIVER4),

					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.RIVER2,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.RIVER3,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.RIVER4,
							ELandscapeType.GRASS),
					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.SAND),
					new FadableLandscapes(ELandscapeType.RIVER2,
							ELandscapeType.SAND),
					new FadableLandscapes(ELandscapeType.RIVER3,
							ELandscapeType.SAND),
					new FadableLandscapes(ELandscapeType.RIVER4,
							ELandscapeType.SAND),
					new FadableLandscapes(ELandscapeType.RIVER1,
							ELandscapeType.WATER1),
					new FadableLandscapes(ELandscapeType.RIVER2,
							ELandscapeType.WATER1),
					new FadableLandscapes(ELandscapeType.RIVER3,
							ELandscapeType.WATER1),
					new FadableLandscapes(ELandscapeType.RIVER4,
							ELandscapeType.WATER1),

					new FadableLandscapes(ELandscapeType.WATER2,
							ELandscapeType.WATER1),
					new FadableLandscapes(ELandscapeType.WATER3,
							ELandscapeType.WATER2),
					new FadableLandscapes(ELandscapeType.WATER4,
							ELandscapeType.WATER3),
					new FadableLandscapes(ELandscapeType.WATER5,
							ELandscapeType.WATER4),
					new FadableLandscapes(ELandscapeType.WATER6,
							ELandscapeType.WATER5),
					new FadableLandscapes(ELandscapeType.WATER7,
							ELandscapeType.WATER6),
					new FadableLandscapes(ELandscapeType.WATER8,
							ELandscapeType.WATER7),
			};

	private final ELandscapeType[][][] fadeLandscapesBuffer =
			new ELandscapeType[ELandscapeType.values().length][ELandscapeType
					.values().length][];

	public boolean canFadeTo(ELandscapeType l1, ELandscapeType l2) {
		if (l1 == l2) {
			return true;
		}

		FadableLandscapes searched = new FadableLandscapes(l1, l2);
		for (FadableLandscapes fl : allowed) {
			if (fl.equals(searched)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets an array of landscapes that can be used to fade between one landscape type and the other. Contains the first and last landscape (or only
	 * one element if the landscape is the same.
	 * <p>
	 * Returns null if there is no way the landscapes can be faded.
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ELandscapeType[] getLandscapesBetween(ELandscapeType l1,
			ELandscapeType l2) {
		ELandscapeType[] buffered =
				fadeLandscapesBuffer[l1.ordinal()][l2.ordinal()];
		if (buffered == null) {
			computeLandscapesFrom(l1);
			buffered = fadeLandscapesBuffer[l1.ordinal()][l2.ordinal()];
			// int length = buffered.length;
			// ELandscapeType[] reverse = new ELandscapeType[length];
			// for (int i = 0; i < length; i++) {
			// reverse[i] = buffered[length - 1 - i];
			// }
			// fadeLandscapesBuffer[l2.ordinal()][l1.ordinal()] = reverse;
		}
		return buffered;
	}

	private void computeLandscapesFrom(ELandscapeType l1) {
		System.out.println("Starting to compute fades from " + l1.toString());
		ELandscapeType[][] ways = fadeLandscapesBuffer[l1.ordinal()];

		ways[l1.ordinal()] = new ELandscapeType[] {
				l1
		};

		boolean foundnew = true;
		while (foundnew) {
			foundnew = false;
			for (FadableLandscapes f : allowed) {
				int ordinal1 = f.getL1().ordinal();
				int ordinal2 = f.getL2().ordinal();
				if (ways[ordinal1] == null && ways[ordinal2] != null) {
					ways[ordinal1] = add(ways[ordinal2], f.getL1());
					foundnew = true;
				}
				if (ways[ordinal2] == null && ways[ordinal1] != null) {
					ways[ordinal2] = add(ways[ordinal1], f.getL2());
					foundnew = true;
				}
			}
		}
	}

	private static ELandscapeType[] add(ELandscapeType[] types,
			ELandscapeType l1) {
		int length = types.length;
		ELandscapeType[] result = new ELandscapeType[length + 1];
		System.arraycopy(types, 0, result, 0, length);
		result[length] = l1;
		return result;
	}

	private static class FadableLandscapes {
		private final ELandscapeType l1;
		private final ELandscapeType l2;

		private FadableLandscapes(ELandscapeType l1, ELandscapeType l2) {
			this.l1 = l1;
			this.l2 = l2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FadableLandscapes) {
				FadableLandscapes other = (FadableLandscapes) obj;
				return (other.l1 == l1 && other.l2 == l2)
						|| (other.l1 == l2 && other.l2 == l1);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return l1.hashCode() + l2.hashCode();
		}

		public ELandscapeType getL1() {
			return l1;
		}

		public ELandscapeType getL2() {
			return l2;
		}
	}
}
