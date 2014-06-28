package jsettlers.logic.map.save.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.graphics.map.UIState;
import jsettlers.input.PlayerState;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.IGameCreator;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;

/**
 * This is the main map loader.
 * <p>
 * It loads a map file.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public abstract class MapLoader implements IGameCreator, Comparable<MapLoader> {
	private final File file;
	private final MapFileHeader header;

	public MapLoader(File file, MapFileHeader header) {
		this.file = file;
		this.header = header;
	}

	public static MapLoader getLoaderForFile(File file) throws MapLoadException {
		MapFileHeader header = loadHeader(file);

		switch (header.getType()) {
		case NORMAL:
			return new FreshMapLoader(file, header);
		case RANDOM:
			return new RandomMapLoader(file, header);
		case SAVED_SINGLE:
			return new SavegameLoader(file, header);
		default:
			throw new MapLoadException("Unkown EMapType: " + header.getType());
		}

	}

	public MapFileHeader getFileHeader() {
		return header;
	}

	private static MapFileHeader loadHeader(File file) throws MapLoadException {
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			return MapFileHeader.readFromStream(stream);
		} catch (IOException e) {
			throw new MapLoadException("Error during header request: ", e);
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 
	 * @return Returns a stream of the file without the header. So you can directly start reading the data of the map.
	 * @throws IOException
	 */
	protected final InputStream getMapDataStream() throws IOException {
		InputStream stream = new BufferedInputStream(new FileInputStream(file));
		MapFileHeader.readFromStream(stream);
		return stream;
	}

	@Override
	public String getMapName() {
		return header.getName();
	}

	public int getMinPlayers() {
		return header.getMinPlayer();
	}

	public int getMaxPlayers() {
		return header.getMaxPlayer();
	}

	public Date getSaveTime() {
		return header.getDate();
	}

	/**
	 * Gets the map data for this loader, if the data is available.
	 * 
	 * @return
	 */
	public abstract IMapData getMapData() throws MapLoadException;

	@Override
	public String toString() {
		return file.getName();
	}

	@Override
	public String getMapID() {
		return header.getUniqueId();
	}

	public File getFile() {
		return file;
	}

	public String getDescription() {
		return header.getDescription() + "\n" + header.getWidth() + "x" + header.getHeight();
	}

	public short[] getImage() {
		return header.getBgimage();
	}

	@Override
	public int compareTo(MapLoader o) {
		MapFileHeader myHeader = header;
		MapFileHeader otherHeader = o.header;
		if (myHeader.getType() == MapType.SAVED_SINGLE) {
			return -myHeader.getDate().compareTo(otherHeader.getDate()); // order by date descending
		} else {
			return myHeader.getName().compareTo(otherHeader.getName()); // order by name ascending
		}
	}

	@Override
	public MainGridWithUiSettings loadMainGrid() throws MapLoadException {
		IMapData mapData = getMapData();

		byte numberOfPlayers = (byte) getMaxPlayers();
		MainGrid mainGrid = new MainGrid(getMapID(), getMapName(), mapData, numberOfPlayers);

		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) {
			playerStates[playerId] = new PlayerState(playerId, new UIState(mapData.getStartPoint(playerId)));
		}

		return new MainGridWithUiSettings(mainGrid, playerStates);
	}
}
