package jsettlers.graphics.startscreen.interfaces;

/**
 * This interface defines the methods supplying information about a map
 * definition
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IMapDefinition {

	/**
	 * Gets the id of the map. This id must be unique! The id must also differ
	 * between maps in a different version.
	 * 
	 * @return The unique identifier of the represented map.
	 */
	String getId();

	/**
	 * Gets the name of the map.
	 * 
	 * @return A name describing the map.
	 */
	String getName();

	/**
	 * Gets the description of this map.
	 * 
	 * @return A string that describes this map. It may contain linebreaks.
	 */
	String getDescription();

	/**
	 * Gets the image of this map.
	 * 
	 * @see MapFileHeader.PREVIEW_IMAGE_SIZE
	 * @return The image data
	 */
	short[] getImage();

}