package jsettlers.newmain.datatypes;

import java.util.List;

import jsettlers.graphics.startscreen.interfaces.ILoadableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;

/**
 * This class is an implementatiopn of the interfaces {@link IStartableMapDefinition} and {@link ILoadableMapDefinition}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MapDefinition implements IStartableMapDefinition, ILoadableMapDefinition {

	private final String id;
	private final String name;
	private final String description;
	private final short[] image;
	private final int minPlayers;
	private final int maxPlayers;
	private final List<ILoadableMapPlayer> players;

	private MapDefinition(String id, String name, String description, short[] image, int minPlayers, int maxPlayers, List<ILoadableMapPlayer> players) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.players = players;
	}

	/**
	 * Creates a new {@link MapDefinition} object fulfilling the {@link IStartableMapDefinition} interface.
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param image
	 * @param minPlayers
	 * @param maxPlayers
	 */
	public MapDefinition(String id, String name, String description, short[] image, int minPlayers, int maxPlayers) {
		this(id, name, description, image, minPlayers, maxPlayers, null);
	}

	/**
	 * Creates a new {@link MapDefinition} object fulfilling the {@link ILoadableMapDefinition} and the {@link IStartableMapDefinition} interface.
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param image
	 * @param players
	 */
	public MapDefinition(String id, String name, String description, short[] image, List<ILoadableMapPlayer> players) {
		this(id, name, description, image, players.size(), players.size(), players);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public short[] getImage() {
		return image;
	}

	@Override
	public int getMinPlayers() {
		return minPlayers;
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public List<ILoadableMapPlayer> getPlayers() {
		return players;
	}

}
