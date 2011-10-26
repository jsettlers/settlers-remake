package jsettlers.graphics.reader;

import java.util.List;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.image.Torso;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is a dat file set that holds the data of a dat file in converted form.
 * <p>
 * It allows access to the torsos, settler images and landscape tiles in the
 * file.
 * <p>
 * The lists should allow quick index access.
 * 
 * @author michael
 */
public interface DatFileSet {
	/**
	 * Gets a list of settlers in the dat file.
	 * @return The unmodifiable list.
	 */
	SequenceList<SettlerImage> getSettlers();

	/**
	 * Gets a list of landscape tiles in the dat file.
	 * @return The unmodifiable list.
	 */
	Sequence<LandscapeImage> getLandscapes();

	/**
	 * Gets a list of gui images.
	 * @return The unmodifiable list.
	 */
	Sequence<GuiImage> getGuis();
}
