package jsettlers.mapcreator.tools.shapes;


public class FuzzyLineCircleShape extends LineCircleShape {
	private float inner = .5f;

	@Override
	protected byte getFieldRating(int x, int y, double distance) {
		if (distance > getRadius()) {
			return 0;
		} else {
			int sloped = (int) getSlopedRating(distance);
			return toByte(sloped);
		}
	}

	protected static byte toByte(int sloped) {
	    return sloped < 0 ? 0 : sloped > Byte.MAX_VALUE ? Byte.MAX_VALUE
	            : (byte) sloped;
    }

	protected double getSlopedRating(double distance) {
		if (inner > .97) {
			// to hard to compute
			return Byte.MAX_VALUE;
		}
		if (distance > getRadius()) {
			return 0;
		}
		// linear falloff:
		//double m = Byte.MAX_VALUE / ((1 - inner) * getRadius());
		//return (getRadius() - distance) * m;
		
		// cosine falloff:
		//return (byte) Byte.MAX_VALUE * Math.cos(distance * Math.PI / getRadius() / 2);
		
		// even better:
		return Byte.MAX_VALUE * (.5 * Math.cos(distance * Math.PI / getRadius()) + .5);
	}

	public void setInner(float inner) {
		if (inner < 0) {
			this.inner = 0;
		} else if (inner > 1) {
			this.inner = 1;
		} else {
			this.inner = inner;
		}
	}

	public float getInner() {
		return inner;
	}
	
	@Override
	public String getName() {
	    return "fuzzy line";
	}
}
