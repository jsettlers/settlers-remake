/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.loading.newmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.logic.map.loading.data.objects.MovableObject;
import jsettlers.logic.map.loading.data.objects.StackMapDataObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;

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
 * For each map object (until end of file): 16 bit x, 16 bit y, 8 bit type, String for additional data.
 * 
 * @author michael
 * @author Andreas Eberle
 * 
 * @see IMapData
 */
public class FreshMapSerializer {
	protected static final int VERSION = 3;
	private static final int VERSION_WITH_RESOURCES_BLOCKED_PARTITIONS = 3;

	private static final int TYPE_TREE = 1;
	private static final int TYPE_STONE = 2;
	private static final int TYPE_BUILDING = 3;
	private static final int TYPE_MOVABLE = 4;
	private static final int TYPE_STACK = 5;

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
	public static void serialize(IMapData data, OutputStream out) throws IOException {
		DataOutputStream stream = new DataOutputStream(out);
		int width = data.getWidth();
		int height = data.getHeight();

		stream.writeShort(VERSION_WITH_RESOURCES_BLOCKED_PARTITIONS);
		stream.writeShort(width);
		stream.writeShort(height);

		stream.writeByte(data.getPlayerCount());
		for (int player = 0; player < data.getPlayerCount(); player++) {
			ShortPoint2D start = data.getStartPoint(player);
			stream.writeShort(start.x);
			stream.writeShort(start.y);
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

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				stream.writeShort(data.getBlockedPartition(x, y));
			}
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				MapDataObject object = data.getMapObject(x, y);
				if (object instanceof MapTreeObject) {
					writeObject(stream, x, y, TYPE_TREE, "");
				} else if (object instanceof StoneMapDataObject) {
					int capacity = ((StoneMapDataObject) object).getCapacity();
					writeObject(stream, x, y, TYPE_STONE, Integer.toString(capacity));
				} else if (object instanceof BuildingMapDataObject) {
					int player = ((BuildingMapDataObject) object).getPlayerId();
					writeObject(stream, x, y, TYPE_BUILDING, ((BuildingMapDataObject) object).getType() + "," + player);
				} else if (object instanceof MovableObject) {
					int player = ((MovableObject) object).getPlayerId();
					writeObject(stream, x, y, TYPE_MOVABLE, ((MovableObject) object).getType() + "," + player);
				} else if (object instanceof StackMapDataObject) {
					int capacity = ((StackMapDataObject) object).getCount();
					writeObject(stream, x, y, TYPE_STACK, ((StackMapDataObject) object).getType() + "," + capacity);
				}
			}
		}

	}

	private static void writeObject(DataOutputStream stream, int x, int y, int type, String string) throws IOException {
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
	public static void deserialize(IMapDataReceiver data, InputStream in) throws IOException {
		try {
			DataInputStream stream = new DataInputStream(in);
			int version = stream.readShort();

			if (version < VERSION_WITH_RESOURCES_BLOCKED_PARTITIONS) {
				throw new IOException("wrong stream version, got: " + version);
			}

			int width = stream.readShort();
			int height = stream.readShort();

			int players = stream.readByte();

			data.setDimension(width, height, players);

			for (int player = 0; player < players; player++) {
				int x = stream.readShort();
				int y = stream.readShort();
				data.setPlayerStart((byte) player, x, y);
			}

			ELandscapeType[] types = ELandscapeType.VALUES;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					byte type = stream.readByte();
					data.setLandscape(x, y, types[type]);
				}
			}

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					byte h = stream.readByte();
					data.setHeight(x, y, h);
				}
			}

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					byte t = stream.readByte();
					byte amount = stream.readByte();
					data.setResources(x, y, EResourceType.VALUES[t], amount);
				}
			}

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					data.setBlockedPartition(x, y, stream.readShort());
				}
			}

			while (stream.available() > 0) {
				int x = stream.readShort();
				int y = stream.readShort();
				int type = stream.readByte();
				String string = stream.readUTF();
				MapDataObject object = getObject(type, string);
				if (object != null) {
					data.setMapObject(x, y, object);
				}
			}
		} catch (Throwable t) {
			throw new IOException("Error while reading map file", t);
		}
	}

	private static MapDataObject getObject(int type, String string) {
		switch (type) {
		case TYPE_TREE:
			return MapTreeObject.getInstance();

		case TYPE_STONE:
			return StoneMapDataObject.getInstance(Integer.parseInt(string));

		case TYPE_STACK: {
			String[] parts = string.split(",");
			return new StackMapDataObject(EMaterialType.valueOf(parts[0]), Integer.valueOf(parts[1]));
		}

		case TYPE_MOVABLE: {
			String[] parts = string.split(",");
			return new MovableObject(EMovableType.valueOf(parts[0]), Byte.valueOf(parts[1]));
		}

		case TYPE_BUILDING: {
			String[] parts = string.split(",");
			return new BuildingMapDataObject(EBuildingType.valueOf(parts[0]), Byte.valueOf(parts[1]));
		}

		default:
			return null;
		}
	}

	/**
	 * Receives the map data.
	 * <p>
	 * Before any other set methods, {@link #setDimension(int, int, int)} is called exactly once.
	 * 
	 * @author michael
	 */
	public interface IMapDataReceiver {
		void setDimension(int width, int height, int playerCount);

		void setBlockedPartition(int x, int y, short blockedPartition);

		void setPlayerStart(byte player, int x, int y);

		void setHeight(int x, int y, byte height);

		void setLandscape(int x, int y, ELandscapeType type);

		void setMapObject(int x, int y, MapDataObject object);

		void setResources(int x, int y, EResourceType type, byte amount);
	}
}
