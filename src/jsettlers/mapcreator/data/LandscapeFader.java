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
	                        ELandscapeType.GRASS),
	                new FadableLandscapes(ELandscapeType.FLATTENED,
	                        ELandscapeType.GRASS),
	                new FadableLandscapes(ELandscapeType.SAND,
	                        ELandscapeType.WATER),
	                new FadableLandscapes(ELandscapeType.SNOW,
	                        ELandscapeType.MOUNTAIN),
	                new FadableLandscapes(ELandscapeType.MOUNTAIN,
	                        ELandscapeType.MOUNTAINBORDER),
	                new FadableLandscapes(ELandscapeType.MOUNTAINBORDER,
	                        ELandscapeType.GRASS),

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
	                        ELandscapeType.WATER),
	                new FadableLandscapes(ELandscapeType.RIVER2,
	                        ELandscapeType.WATER),
	                new FadableLandscapes(ELandscapeType.RIVER3,
	                        ELandscapeType.WATER),
	                new FadableLandscapes(ELandscapeType.RIVER4,
	                        ELandscapeType.WATER),
	        };

	private ELandscapeType[][][] fadeLandscapesBuffer =
	        new ELandscapeType[ELandscapeType.values().length][ELandscapeType
	                .values().length][];

	public boolean canFadeTo(ELandscapeType l1, ELandscapeType l2) {
		FadableLandscapes searched = new FadableLandscapes(l1, l2);
		for (FadableLandscapes fl : allowed) {
			if (fl.equals(searched)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets an array of landscpaes that can be used to fade between one landscae
	 * type and the oter. Contains the first and last landscape (or only one
	 * element if the landscape is the same.
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
