package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This class lets you fade the landscape.
 * 
 * @author michael
 */
public class LandscapeFader {
	private static final FadableLandscapes[] allowed = new FadableLandscapes[] {

	};

	public boolean canFadeTo(ELandscapeType l1, ELandscapeType l2) {
		FadableLandscapes searched = new FadableLandscapes(l1, l2);
		for (FadableLandscapes fl: allowed) {
			if (fl.equals(searched)) {
				return true;
			}
		}
		return false;
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
	}
}
