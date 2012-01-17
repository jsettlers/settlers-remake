package jsettlers.logic.map.save;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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
 */
public class MapFileHeader {
	private static final short ALLOWED_VERSION = 1;

	private static final byte[] START_BYTES = new byte[] {
	        'M', 'A', 'P', ' '
	};

	private static final int PREVIEW_IMAGE_SIZE = 128;

	private final String name;

	private final String description;

	private final MapType type;

	private final short width;

	private final short height;

	private final short minPlayer;

	private final short maxPlayer;

	private Date date;

	/**
	 * The content type of a map file.
	 * 
	 * @author michael
	 */
	public static enum MapType {
		RANDOM, NORMAL, SAVED_SINGLE,
	}

	public MapFileHeader(MapType type, String name, String description,
	        short width, short height, short minplayer, short maxplayer,
	        Date date) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.width = width;
		this.height = height;
		this.minPlayer = minplayer;
		this.maxPlayer = maxplayer;
		this.date = date;
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

	public Date getDate() {
		return date;
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

	public void writeTo(OutputStream stream) throws IOException {
		DataOutputStream out = new DataOutputStream(stream);
		out.write(START_BYTES);
		out.writeShort(ALLOWED_VERSION);
		out.writeUTF(type.toString());
		out.writeUTF(name);
		out.writeUTF(description);

		out.writeShort(width);
		out.writeShort(height);
		out.writeShort(minPlayer);
		out.writeShort(maxPlayer);

		out.write(new byte[PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE * 2]);
		
		if (type == MapType.SAVED_SINGLE) {
			out.writeLong(date.getTime());
		}
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
			if (version != ALLOWED_VERSION) {
				throw new IOException("Map header version is invalid.");
			}

			String typeStr = in.readUTF();
			MapType type = MapType.valueOf(typeStr);

			String name = in.readUTF();

			String description = in.readUTF();

			short width = in.readShort();
			short height = in.readShort();
			short minplayer = in.readShort();
			short maxplayer = in.readShort();

			byte[] bgimage = new byte[PREVIEW_IMAGE_SIZE * PREVIEW_IMAGE_SIZE * 2];
			in.read(bgimage);

			Date date = null;
			if (type == MapType.SAVED_SINGLE) {
				long datetime = in.readLong();
				date = new Date(datetime);
			}

			return new MapFileHeader(type, name, description, width, height,
			        minplayer, maxplayer, date);

		} catch (Throwable t) {
			if (t instanceof IOException) {
				throw (IOException) t;
			} else {
				throw new IOException(t);
			}
		}
	}
}
