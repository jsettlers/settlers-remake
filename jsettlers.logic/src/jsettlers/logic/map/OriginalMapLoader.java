package jsettlers.logic.map;

import jsettlers.logic.map.save.loader.MapLoader;

import java.io.*;
import java.util.Vector;

/**
 * @author codingberlin
 */
public class OriginalMapLoader {
	public MapLoader loadOriginalMap(File originalMapFile) {
		try {
			OriginalMapFileContent mapContent = new OriginalMapFileContent(originalMapFile);

			if (!mapContent.isChecksumValid()) {
				System.out.println("Checksum of original map was not valid!");
				return null;
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public class OriginalMapFileContent {

		long[] mapContent;

		public OriginalMapFileContent(File originalMapFile) throws IOException {
			DataInputStream dis = new DataInputStream(new FileInputStream(originalMapFile));
			Vector<Long> mapContent = new Vector<Long>();

			while (dis.available() > 0) {
				long currentByte = dis.readUnsignedByte();
				mapContent.add(currentByte);
			}
			dis.close();

			this.mapContent = new long[mapContent.size()];
			for (int i = 0; i < mapContent.size(); i++) {
				this.mapContent[i] = mapContent.get(i);
			}
		}

		public int readBytesFrom(int numberOfBytes, int offset) {
			int result = 0;
			for (int i = offset; i < offset+numberOfBytes; i++) {
				result += mapContent[i] << (i - offset) * 8;
			}
			return result;
		}

		boolean isChecksumValid() {
			long fileChecksum = readBytesFrom(4, 0);
			System.out.println(fileChecksum);
			int count = mapContent.length - 8;

			int currentChecksum = 0;
			for (int i = 8; i < count; i+=4) {
				long currentInt = readBytesFrom(4, i);
				currentChecksum = (int) ((currentChecksum * 2) ^ currentInt);
				if (i < 40) {
					System.out.println(i + " : " + currentInt + " : " + currentChecksum);
				}
				if (currentChecksum == fileChecksum) {
					System.out.println("Treffer " + i);
				}
			}

			if (currentChecksum != fileChecksum) {
				return false;
			}

			return true;
		}

	}

	public static void main(String[] args) {
		(new OriginalMapLoader()).loadOriginalMap(new File("/data/home/sbauer/Downloads/a.map"));
	}

}
