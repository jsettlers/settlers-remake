package go.graphics;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class SharedTexture {

	private static final int CAPACITY = 2048;
	private final int capacity;
	private static int maxIndex = 0;

	private int[] remaining;
	private int index;
	private TextureHandle texture;

	private static final ArrayList<SharedTexture> textures = new ArrayList<>();

	public static SharedTextureHandle addTexture(GLDrawContext dc, ShortBuffer data, int width, int height) throws IllegalBufferException {
		if(staticdc == null) staticdc = dc;
		int stextureIndex = 0;

		while(true) {
			// create an instance if needed
			if(textures.size() == stextureIndex) {
				textures.add(new SharedTexture(dc, ++maxIndex, CAPACITY));
				System.out.println(textures.size());
			}

			SharedTexture texture = textures.get(stextureIndex);

			if(width > texture.capacity || height > texture.capacity) return new SharedTextureHandle(texture, 0, 0, 0, 0);

			int placement_count = texture.capacity - width;

			int[] placements = new int[placement_count];
			int[] lowest = new int[placement_count];

			for(int i = 0; i != placement_count; i++) {
				int sum_of_remaining = 0;
				int lowest_remaining = texture.remaining[i];
				for(int j = 0; j != width; j++) {
					sum_of_remaining += texture.remaining[i+j];
					if(lowest_remaining > texture.remaining[i+j]) lowest_remaining = texture.remaining[i+j];
				}

				lowest[i] = lowest_remaining;

				if(lowest_remaining < height) {
					placements[i] = -1;
				} else {
					placements[i] = sum_of_remaining - (width * lowest_remaining);
				}
			}

			int lowest_damage_placement = -1;
			for(int i = 0;i != placement_count; i++) {
				if(placements[i] != -1) {
					if(lowest_damage_placement == -1) {
						lowest_damage_placement = i;
					} else if(placements[lowest_damage_placement] > placements[i]) {
						lowest_damage_placement = i;
					}
				}
			}

			if(lowest_damage_placement != -1) {
				SharedTextureHandle handle = texture.fill(lowest_damage_placement, lowest[lowest_damage_placement], width, height, data);
				if (handle != null) return handle;
			}

			stextureIndex++;
		}
	}

	private SharedTextureHandle fill(int start, int expected_least, int width, int height, ShortBuffer data) throws IllegalBufferException {
		int leastRemaining = remaining[start];
		for(int i = 0; i != width; i++) {
			if(leastRemaining > remaining[i+start]) {
				leastRemaining = remaining[i+start];
			}
		}

		if(leastRemaining != expected_least) {
			System.out.println("texture packing algorythm faulty");
		}

		if(leastRemaining < height) return null;

		// claim texture space
		Arrays.fill(remaining, start, start+width, leastRemaining-height);

		// ...and populate it
		staticdc.updateTexture(texture, start, capacity-leastRemaining, width, height, data);
		return new SharedTextureHandle(this, start, capacity-leastRemaining, width, height);

	}

	public static class SharedTextureHandle {
		public final TextureHandle texture;
		public final float x, y, width, height;
		private final int iteration = SharedTexture.iteration;

		private SharedTextureHandle(SharedTexture shared, float x, float y, float width, float height) {
			texture = shared.texture;
			this.x = x / shared.capacity;
			this.y = y / shared.capacity;
			this.width = width / shared.capacity;
			this.height = height / shared.capacity;
		}
	}

	private static int iteration = 0;
	private static GLDrawContext staticdc = null;

	public static boolean isInvalid(GLDrawContext dc, SharedTextureHandle handle) {
		if(dc != staticdc) {
			textures.clear();
			staticdc = dc;
			iteration++;
		}
		return handle.iteration!=iteration;
	}

	private SharedTexture(GLDrawContext dc, int index, int capacity) {
		this.capacity = capacity;
		this.index = index;
		texture = dc.generateTexture(capacity, capacity, null, "sharedtexture-" + index);
		remaining = new int[capacity];
		Arrays.fill(remaining, capacity);
	}
}
