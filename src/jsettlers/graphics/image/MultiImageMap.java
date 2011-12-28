package jsettlers.graphics.image;

import jsettlers.graphics.sequence.Sequence;

/**
 * This is a map of multile images of one sequence.
 * It always contains the settler image and the torso
 * @author michael
 *
 */
public class MultiImageMap {
	private final int width;
	private final int height;

	public MultiImageMap(int width, int height) {
		this.width = width;
		this.height = height;
		short[] buffer = new short[width * height];
	}
	
	
}
