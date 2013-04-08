package jsettlers.logic.map.save;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.network.INetworkableMap;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.logic.map.newGrid.GameSerializer;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.main.IGameCreator;

/**
 * This is the main map loader.
 * <p>
 * It loads a map file.
 * 
 * @author michael
 */
public class MapLoader implements IGameCreator, ILoadableGame, IMapItem, INetworkableMap, Comparable<MapLoader> {
	private final File file;
	private MapFileHeader header;
	private MainGrid mainGrid;
	private ShortPoint2D[] startPoints;
	private UIState uiState;
	private IMapData mapData;

	public MapLoader(File file) {
		this.file = file;
	}

	public MapFileHeader getFileHeader() throws MapLoadException {
		loadHeader();
		return header;
	}

	private void loadHeader() throws MapLoadException {
		if (header == null) {
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(new FileInputStream(file));
				header = MapFileHeader.readFromStream(stream);
			} catch (IOException e) {
				// TODO: handle this exception, and do not try again.
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
	}

	private void loadAll() throws IOException, MapLoadException {
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			header = MapFileHeader.readFromStream(stream);

			if (header.getType() == MapType.NORMAL) {
				// load a normal map file
				MapDataReceiver data = new MapDataReceiver();
				MapDataSerializer.deserialize(data, stream);
				loadBy(data);
				mapData = data;

			} else if (header.getType() == MapType.RANDOM) {

				// TODO: arguments
				int players = 3;
				int randomSeed = 3;

				RandomMapFile file = RandomMapFile.loadFromStream(stream);
				RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), players);
				evaluator.createMap(new Random(randomSeed));
				mapData = evaluator.getGrid();

				loadBy(mapData);

			} else if (header.getType() == MapType.SAVED_SINGLE) {
				uiState = UIState.readFrom(stream);

				GameSerializer gameSerializer = new GameSerializer();
				mainGrid = gameSerializer.load(stream);
			} else {
				throw new IOException("unsupported map type.");
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

	private void loadBy(IMapData data) {
		startPoints = new ShortPoint2D[data.getPlayerCount()];
		for (int i= 0; i < startPoints.length; i++) {
			startPoints[i] = data.getStartPoint(i);
		}
	}

	@Override
	public MainGrid getMainGrid() throws MapLoadException {
		return getMainGrid((byte)0);
	}

	@Override
	public MainGrid getMainGrid(byte player) throws MapLoadException {
		try {
			if (mainGrid == null) {
				loadAll();
			}
			if (mapData != null) {
				mainGrid =
				        MainGrid.create(mapData,
				                (byte) mapData.getPlayerCount(), player);
				if (mainGrid == null) {
					throw new MapLoadException("loaded map was null");
				}
			}
			return mainGrid;
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
	}

	@Override
	public UIState getUISettings(int player) throws MapLoadException {
		try {
			if (uiState == null && startPoints == null) {
				loadAll();
			}
			if (uiState != null) {
				return uiState;
			} else {
				return new UIState(player, startPoints[player]);
			}
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
	}

	@Override
	public String getName() {
		try {
			return getFileHeader().getName();
		} catch (MapLoadException e) {
			return "";
		}
	}

	@Override
	public int getMinPlayers() {
		try {
			return getFileHeader().getMinPlayer();
		} catch (MapLoadException e) {
			return 1;
		}
	}

	@Override
	public int getMaxPlayers() {
		try {
			return getFileHeader().getMaxPlayer();
		} catch (MapLoadException e) {
			return 1;
		}
	}

	@Override
	public Date getSaveTime() {
		try {
			return getFileHeader().getDate();
		} catch (MapLoadException e) {
			return new Date();
		}
	}

	/**
	 * Gets the map data for this loader, if the data is available.
	 * 
	 * @return
	 */
	public IMapData getMapData() throws MapLoadException {
		try {
			if (mapData == null) {
				loadAll();
			}
			return mapData;
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
	}

	@Override
	public String toString() {
		return file.getName();
	}

	public String getMapID() {
		return getUniqueID();
	}

	@Override
	public String getUniqueID() {
		try {
			return getFileHeader().getUniqueId();
		} catch (MapLoadException e) {
			return "";
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public INetworkableMap getNetworkableMap() {
		return this;
	}

	@Override
	public String getDescription() {
		try {
			return getFileHeader().getDescription() + "\n" + getFileHeader().getWidth() + "x" + getFileHeader().getHeight();
		} catch (MapLoadException e) {
			return "";
		}
	}

	@Override
	public short[] getImage() {
		try {
			return getFileHeader().getBgimage();
		} catch (MapLoadException e) {
			return new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE];
		}
	}

	@Override
	public int compareTo(MapLoader o) {
		try {
			MapFileHeader myHeader = getFileHeader();
			MapFileHeader otherHeader = o.getFileHeader();
			if (myHeader.getType() == MapType.SAVED_SINGLE) {
				return -myHeader.getDate().compareTo(otherHeader.getDate()); // order by date descending
			} else {
				return myHeader.getName().compareTo(otherHeader.getName()); // order by name ascending
			}
		} catch (MapLoadException e) {
			return 0;
		}
	}
}
