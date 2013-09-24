package jsettlers.logic.map.save.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.save.MapDataReceiver;
import jsettlers.logic.map.save.MapDataSerializer;
import jsettlers.logic.map.save.MapFileHeader;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class FreshMapLoader extends MapLoader {

	private MapDataReceiver data = null;

	public FreshMapLoader(File file, MapFileHeader header) {
		super(file, header);
	}

	@Override
	public IMapData getMapData() throws MapLoadException {
		if (data != null) {
			return data;
		}

		try {
			InputStream stream = super.getMapDataStream();
			data = new MapDataReceiver();
			MapDataSerializer.deserialize(data, stream);
			return data;
		} catch (IOException ex) {
			throw new MapLoadException(ex);
		}
	}

	@Override
	public MainGridWithUiSettings loadMainGrid(byte playerId) throws MapLoadException {
		return super.loadMainGridFromMapData(playerId);
	}

}
