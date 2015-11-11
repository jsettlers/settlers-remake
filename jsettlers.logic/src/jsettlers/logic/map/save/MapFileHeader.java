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
package jsettlers.logic.map.save;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

/**
 * This is a map file header.
 * <p>
 * Each map file starts with such a header.
 * <p>
 * The header format is:
 * <ul>
 * <li>4 byte: MAP<space></li>
 * <li>2 Byte (short): Header version. must be '1'.</li>
 * <li>String: Type of the map.</li>
 * <li>String: name</li>
 * <li>String: mapId</li>
 * <li>String: baseMapId</li>
 * <li>String: description</li>
 * <li>16 bit: width</li>
 * <li>16 bit: height</li>
 * <li>16 bit: minplayer (for saved game: number of alive players)</li>
 * <li>16 bit: maxplayer (for saved game: number of start players)</li>
 * <li>128*128*2 bytes: image of the map.</li>
 * <li>Some more type-dependent settings</li>
 * </ul>
 * 
 * @author michael
 * @author Andreas Eberle
 */
public class MapFileHeader {
	private static final short MIN_VERSION = 1;
	private static final short VERSION_MAP_ID_INTRODUCED = 2;
	private static final short VERSION_DATE_ALWAYS_SAVED = 3;
	private static final short VERSION = 3;

	private static final byte[] START_BYTES = new byte[] {
			'M', 'A', 'P', ' '
	};

	public static final int PREVIEW_IMAGE_SIZE = 128;

	private final String name;
	private final String mapId;
	private final String baseMapId;
	private final String description;
	private final MapType type;
	private final short width;
	private final short height;
	private final short minPlayer;
	private final short maxPlayer;
	private final Date creationDate;
	private final short[] bgimage;

	/**
	 * The content type of a map file.
	 * 
	 * @author michael
	 */
	public static enum MapType {
		NORMAL,
		SAVED_SINGLE
	}

	public MapFileHeader(MapType type, String name, String baseMapId, String description, short width, short height, short minplayer,
			short maxplayer, Date date, short[] bgimage) {
		this(type, name, UUID.randomUUID().toString(), baseMapId, description, width, height, minplayer, maxplayer, date, bgimage);
	}

	private MapFileHeader(MapType type, String name, String mapId,
			String baseMapId, String description, short width, short height, short minplayer, short maxplayer,
			Date date, short[] bgimage) {
		if (bgimage.length != PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE) {
			throw new IllegalArgumentException("bg image has wrong size.");
		}
		this.type = type;
		this.name = name;
		this.mapId = mapId;
		this.baseMapId = baseMapId;
		this.description = description;
		this.width = width;
		this.height = height;
		this.minPlayer = minplayer;
		this.maxPlayer = maxplayer;
		this.creationDate = date;
		this.bgimage = bgimage;
	}

	public MapType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public short getWidth() {
		return width;
	}

	public short getHeight() {
		return height;
	}

	public short getMinPlayer() {
		return minPlayer;
	}

	public short getMaxPlayer() {
		return maxPlayer;
	}

	public short[] getBgimage() {
		return bgimage;
	}

	public void writeTo(OutputStream stream) throws IOException {
		DataOutputStream out = new DataOutputStream(stream);
		out.write(START_BYTES);
		out.writeShort(VERSION);
		out.writeUTF(type.toString());
		out.writeUTF(name);
		out.writeUTF(mapId);
		out.writeUTF(baseMapId == null ? "" : baseMapId);
		out.writeUTF(description);

		out.writeShort(width);
		out.writeShort(height);
		out.writeShort(minPlayer);
		out.writeShort(maxPlayer);

		for (int i = 0; i < PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE; i++) {
			out.writeShort(bgimage[i]);
		}

		out.writeLong(creationDate.getTime());
	}

	/**
	 * Reads a new file header from the stream.
	 * 
	 * @param stream
	 *            The stream to read from.
	 * @return
	 */
	public static MapFileHeader readFromStream(InputStream stream)
			throws IOException {
		try {
			DataInputStream in = new DataInputStream(stream);
			for (byte b : START_BYTES) {
				if (in.readByte() != b) {
					throw new IOException("Map header start is invalid.");
				}
			}

			int version = in.readShort();
			if (version < MIN_VERSION) {
				throw new IOException("Map header version is invalid.");
			}

			String typeStr = in.readUTF();
			MapType type = MapType.valueOf(typeStr);

			String mapName = in.readUTF();

			String mapId = (version < VERSION_MAP_ID_INTRODUCED) ? mapName : in.readUTF();
			String baseMapId = (version < VERSION_MAP_ID_INTRODUCED) ? null : in.readUTF();

			String description = in.readUTF();

			short width = in.readShort();
			short height = in.readShort();
			short minplayer = in.readShort();
			short maxplayer = in.readShort();

			short[] bgimage = new short[PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE];
			for (int i = 0; i < PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE; i++) {
				bgimage[i] = in.readShort();
			}

			Date date = null;
			if (version < VERSION_DATE_ALWAYS_SAVED) {
				if (type == MapType.SAVED_SINGLE) {
					date = new Date(in.readLong());
				}
			} else {
				date = new Date(in.readLong());
			}

			return new MapFileHeader(type, mapName, mapId, baseMapId, description, width, height,
					minplayer, maxplayer, date, bgimage);

		} catch (Throwable t) {
			if (t instanceof IOException) {
				throw (IOException) t;
			} else {
				throw new IOException(t);
			}
		}
	}

	public String getUniqueId() {
		return mapId;
	}

	public String getBaseMapId() {
		return baseMapId;
	}
}
