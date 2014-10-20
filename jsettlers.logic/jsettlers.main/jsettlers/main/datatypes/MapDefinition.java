package jsettlers.main.datatypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.ILoadableMapPlayer;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.save.loader.MapLoader;

/**
 * This class is an implementatiopn of the interfaces {@link IStartableMapDefinition} and {@link ILoadableMapDefinition}.
 * 
 * @author Andreas Eberle
 * 
 */
public class MapDefinition implements IMapDefinition {

	private final MapLoader mapLoader;

	public MapDefinition(MapLoader mapLoader) {
		this.mapLoader = mapLoader;
	}

	@Override
	public String getId() {
		return mapLoader.getMapID();
	}

	@Override
	public String getName() {
		return mapLoader.getMapName();
	}

	@Override
	public String getDescription() {
		return mapLoader.getDescription();
	}

	@Override
	public short[] getImage() {
		return mapLoader.getImage();
	}

	@Override
	public int getMinPlayers() {
		return mapLoader.getMinPlayers();
	}

	@Override
	public int getMaxPlayers() {
		return mapLoader.getMaxPlayers();
	}

	@Override
	public List<ILoadableMapPlayer> getPlayers() { // TODO @Andreas Eberle: supply saved players information.
		return new ArrayList<ILoadableMapPlayer>();
	}

	@Override
	public Date getCreationDate() {
		return mapLoader.getCreationDate();
	}

}
