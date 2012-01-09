package jsettlers.mapcreator.tools;

import jsettlers.mapcreator.noise.NoiseGenerator;

public class NoisyLineCircleShape extends FuzzyLineCircleShape {

	private int noiseSize = 100;

	private NoiseGenerator noise = new NoiseGenerator();

	protected byte getFieldRating(int x, int y, double distance) {
		if (distance > (double) getRadius() * (Byte.MAX_VALUE + noiseSize)
		        / Byte.MAX_VALUE) {
			return 0;
		} else {
			double sloped = getSlopedRating(distance);
			double add = noise.getNoise(x, y) * noiseSize;
			return toByte((int) (sloped + add));
		}
	};
}
