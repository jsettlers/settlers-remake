package jsettlers.logic.algorithms.previewimage;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This interface defines the methods needed by the {@link PreviewImageCreator} to calculate a preview image that can be saved in a map file's header.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPreviewImageDataSupplier {

	/**
	 * Gets the {@link ELandscapeType} at the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	ELandscapeType getLandscape(short x, short y);

	/**
	 * Gets the height of the landscape at the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	byte getLandscapeHeight(short x, short y);

}
