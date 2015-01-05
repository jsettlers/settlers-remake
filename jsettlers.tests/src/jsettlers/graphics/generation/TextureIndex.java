package jsettlers.graphics.generation;

import java.io.IOException;

/**
 * This is a index where textures can be registered
 * 
 * @author michael
 *
 */
public interface TextureIndex {
	void registerTexture(String name, int file, int offsetx, int offsety, int width, int height, boolean hasTorso, TexturePosition position)
			throws IOException;

	int getNextTextureIndex();
}
