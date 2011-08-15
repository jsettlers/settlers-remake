package jsettlers.logic.map.random.noise;

public class NoiseSet {

	private int prime1;
	private int prime2;
	private int prime3;

	public NoiseSet(int prime1, int prime2, int prim3) {
		this.prime1 = prime1;
		this.prime2 = prime2;
		this.prime3 = prim3;
	}

	public float getInterpolated(float x, float y) {
		int intx = (int) x;
		int inty = (int) y;

		float v1 = getSmoothNoise(intx, inty);
		float v2 = getSmoothNoise(intx, inty + 1);
		float v3 = getSmoothNoise(intx + 1, inty);
		float v4 = getSmoothNoise(intx + 1, inty + 1);

		float xfract = x - intx;
		float yfract = y - inty;

		return interpolate(interpolate(v1, v2, yfract),
		        interpolate(v3, v4, yfract), xfract);
	}

	private float interpolate(float start, float end, float fractional) {
		// return start * (1-fractional) + end * (fractional);
		float f = (1 - (float) Math.cos(fractional * Math.PI)) * .5f;

		return start * (1 - f) + end * f;
	}

	public float getSmoothNoise(int x, int y) {
		// smooth corners:
		float cornerSum =
		        getNoise(x - 1, y - 1) + getNoise(x + 1, y - 1)
		                + getNoise(x - 1, y + 1) + getNoise(x + 1, y + 1);
		float edgeSum =
		        getNoise(x, y - 1) + getNoise(x + 1, y)
		                + getNoise(x, y + 1) + getNoise(x - 1, y);
		return getNoise(x, y) * .25f + edgeSum / 4 * .5f + cornerSum / 4
		        * .25f;
	}

	private float getNoise(int x, int y) {
		return makeRandom(x + y * 2207963);
	}

	private float makeRandom(int x) {
		return 1.0f - ((x * (x * x * prime1 + prime2) + prime3) & 0x7fffffff) / 1073741824.0f;

	}
}