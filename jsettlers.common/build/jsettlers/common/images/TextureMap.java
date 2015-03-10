package jsettlers.common.images;
import java.util.Arrays;

public final class TextureMap {
	private TextureMap() {}

	public static int getIndex(String name) {
		int arrindex = Arrays.binarySearch(names, name);
		if (arrindex < 0) {
			throw new IllegalArgumentException("Could not find " + name + " in image map.");
		}
		return indexes[arrindex];
	}

	private static final String[] names = new String[] {
	};
	private static final int[] indexes = new int[] {
	};
}
