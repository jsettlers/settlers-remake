package jsettlers.common.map;

/**
 * Provides a method to construct the map data.
 * @author michael
 *
 */
public interface IMapDataProvider {
	public IMapData getData() throws MapLoadException;
}
