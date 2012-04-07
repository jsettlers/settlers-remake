package jsettlers.logic.map.save;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.random.grid.MapGrid;

/**
 * Serializes the map data to a byte stream.
 * <p>
 * Format:
 * <p>
 * 16 bit version: always 1.
 * <p>
 * 16 bit width, 16 bit height
 * <p>
 * 1 byte: player count
 * <p>
 * For each player: 2 byte x, 2 byte y
 * <p>
 * width * height bytes: landscape types (ordinals)
 * <p>
 * width * height bytes: height map
 * <p>
 * For each map object (until end of file): 16 bit x, 16 bit y, 8 bit type,
 * String for additional data.
 * 
 * @author michael
 * @see IMapData
 */
public class MapDataSerializer {
	private static final int VERSION = 1;
	private static final int TYPE_TREE = 1;
	private static final int TYPE_STONE = 2;
	private static final int TYPE_BUILDING = 3;
	private static final int TYPE_MOVABLE = 4;
	private static final int TYPE_STACK = 5;
	private static final int VERSION_WITH_RESOURCES = 2;

	/**
	 * Serializes the given data to the output stream.
	 * 
	 * @param data
	 *            The data to serialize
	 * @param out
	 *            Thre stream to write to.
	 * @throws IOException
	 *             If an IO error occured.
	 */
	public static void serialize(IMapData data, OutputStream out)
	        throws IOException {
		DataOutputStream stream = new DataOutputStream(out);
		int width = data.getWidth();
		int height = data.getHeight();

		stream.writeShort(VERSION_WITH_RESOURCES);
		stream.writeShort(width);
		stream.writeShort(height);

		stream.writeByte(data.getPlayerCount());
		for (int player = 0; player < data.getPlayerCount(); player++) {
			ShortPoint2D start = data.getStartPoint(player);
			stream.writeShort(start.getX());
			stream.writeShort(start.getY());
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				stream.writeByte(data.getLandscape(x, y).ordinal());
			}
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				stream.writeByte(data.getLandscapeHeight(x, y));
			}
		}

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				stream.writeByte(data.getResourceType(x, y).ordinal);
				stream.writeByte(data.getResourceAmount(x, y));
			}
		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				MapObject object = data.getMapObject(x, y);
				if (object instanceof MapTreeObject) {
					writeObject(stream, x, y, TYPE_TREE, "");
				} else if (object instanceof MapStoneObject) {
					int capacity = ((MapStoneObject) object).getCapacity();
					writeObject(stream, x, y, TYPE_STONE,
					        Integer.toString(capacity));
				} else if (object instanceof BuildingObject) {
					int player = ((BuildingObject) object).getPlayer();
					writeObject(stream, x, y, TYPE_BUILDING,
					        ((BuildingObject) object).getType() + "," + player);
				} else if (object instanceof MovableObject) {
					int player = ((MovableObject) object).getPlayer();
					writeObject(stream, x, y, TYPE_MOVABLE,
					        ((MovableObject) object).getType() + "," + player);
				} else if (object instanceof StackObject) {
					int capacity = ((StackObject) object).getCount();
					writeObject(stream, x, y, TYPE_STACK,
					        ((StackObject) object).getType() + "," + capacity);
				}
			}
		}
	}

	private static void writeObject(DataOutputStream stream, int x, int y,
	        int type, String string) throws IOException {
		stream.writeShort(x);
		stream.writeShort(y);
		stream.writeByte(type);
		stream.writeUTF(string);
	}

	/**
	 * Reads the map data from the given stream and sets up the receiver by it.
	 * 
	 * @param data
	 *            The receiver of the data.
	 * @param in
	 *            The stream to read from.
	 * @throws IOException
	 *             If an error occured during deserialization.
	 */
	public static void deserialize(IMapDataReceiver data, InputStream in)
	        throws IOException {
		try {
			DataInputStream stream = new DataInputStream(in);
			int version = stream.readShort();

			if (version != VERSION && version != VERSION_WITH_RESOURCES) {
				throw new IOException("wrong stream version, got: " + version);
			}
			Random rand = new Random(123);

			int width = stream.readShort();
			int height = stream.readShort();

			int players = stream.readByte();

			data.setDimension(width, height, players);

			for (int player = 0; player < players; player++) {
				int x = stream.readShort();
				int y = stream.readShort();
				data.setPlayerStart((byte) player, x, y);
			}

			ELandscapeType[] types = ELandscapeType.values();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					byte type = stream.readByte();
					data.setLandscape(x, y, types[type]);
					if (version < VERSION_WITH_RESOURCES) {
						//fallback. Can be removed once all maps use new format.
						EResourceType type2 =
						        MapGrid.getResourceType(types[type], rand);
						data.setResources(x, y, type2,
						        MapGrid.getResourceAmount(types[type], rand));
					}
				}
			}

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					byte h = stream.readByte();
					data.setHeight(x, y, h);
				}
			}

			if (version >= VERSION_WITH_RESOURCES) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						byte t = stream.readByte();
						byte amount = stream.readByte();
						data.setResources(x, y, EResourceType.values[t], amount);
					}
				}

			}

			while (stream.available() > 0) {
				int x = stream.readShort();
				int y = stream.readShort();
				int type = stream.readByte();
				String string = stream.readUTF();
				MapObject object = getObject(type, string);
				if (object != null) {
					data.setMapObject(x, y, object);
				}
			}

		} catch (Throwable t) {
			throw new IOException("Error while reading map file", t);
		}
	}

	private static MapObject getObject(int type, String string) {
		switch (type) {
			case TYPE_TREE:
				return MapTreeObject.getInstance();

			case TYPE_STONE:
				return MapStoneObject.getInstance(Integer.parseInt(string));

			case TYPE_STACK: {
				String[] parts = string.split(",");
				return new StackObject(EMaterialType.valueOf(parts[0]),
				        Integer.valueOf(parts[1]));
			}

			case TYPE_MOVABLE: {
				String[] parts = string.split(",");
				return new MovableObject(EMovableType.valueOf(parts[0]),
				        Byte.valueOf(parts[1]));
			}

			case TYPE_BUILDING: {
				String[] parts = string.split(",");
				return new BuildingObject(EBuildingType.valueOf(parts[0]),
				        Byte.valueOf(parts[1]));
			}

			default:
				return null;
		}
	}

	/**
	 * Receives the map data.
	 * <p>
	 * Before any other set methods, {@link #setDimension(int, int, int)} is
	 * called exactly once.
	 * 
	 * @author michael
	 */
	public interface IMapDataReceiver {
		void setDimension(int width, int height, int playerCount);

		void setPlayerStart(byte player, int x, int y);

		void setHeight(int x, int y, byte height);

		void setLandscape(int x, int y, ELandscapeType type);

		void setMapObject(int x, int y, MapObject object);

		void setResources(int x, int y, EResourceType type, byte amount);
	}
}
