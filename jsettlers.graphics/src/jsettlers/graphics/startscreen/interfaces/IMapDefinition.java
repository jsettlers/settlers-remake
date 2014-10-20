package jsettlers.graphics.startscreen.interfaces;

import java.util.Date;
import java.util.List;

/**
 * This interface defines the methods supplying information about a map definition
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IMapDefinition {

	/**
	 * Gets the id of the map. This id must be unique! The id must also differ between maps in a different version.
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

	/**
	 * Gets the minimum number of players that can play this map.
	 * 
	 * @return That number.
	 */
	int getMinPlayers();

	/**
	 * Gets the maximum number of players supported by this map.
	 * 
	 * @return The number of players supported by this map.
	 */
	int getMaxPlayers();

	/**
	 * Gets a list of players that played on the map.
	 * 
	 * @return The players from that loadable game.
	 */
	public List<ILoadableMapPlayer> getPlayers();

	public Date getCreationDate();
}