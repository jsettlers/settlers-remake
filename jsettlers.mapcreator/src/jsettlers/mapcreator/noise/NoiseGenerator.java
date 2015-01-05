package jsettlers.mapcreator.noise;

public class NoiseGenerator {
	private static final float PERSISTENCE = .5f;

	private static final float FREQ_INCREASE = 2;
	private final NoiseSet[] SETS = new NoiseSet[] {
			new NoiseSet(15731, 789221, 1376312589),
			new NoiseSet(15731, 789221, 2350883)
	};

	public float getNoise(int x, int y) {
		return getRealNoise(x * .3f, y * .3f);
	}

	private float getRealNoise(float x, float y) {
		float sum = 0;

		float amplitude = 1;
		float frequency = 1;
		for (int i = 0; i < SETS.length; i++) {
			sum += SETS[i].getInterpolated(x * frequency, y * frequency) * amplitude;

			frequency *= FREQ_INCREASE;
			amplitude *= PERSISTENCE;
		}

		return sum;
	}
}
