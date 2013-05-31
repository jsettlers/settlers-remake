package jsettlers.graphics.startscreen.interfaces;

public interface IMapDefinition {

	/**
	 * Gets the name of the map.
	 * 
	 * @return A name describing the map.
	 */
	public abstract String getName();

	/**
	 * Gets the description of this map.
	 * @return A string that describes this map. It may contain linebreaks.
	 */
	public abstract String getDescription();

	/**
	 * Gets the image of this map.
	 * @see MapFileHeader.PREVIEW_IMAGE_SIZE
	 * @return The image data
	 */
	public abstract short[] getImage();

}