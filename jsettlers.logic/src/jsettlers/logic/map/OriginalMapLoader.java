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

		//- Read Big-Ending INT from Buffer
		public int readBEIntFrom(int numberOfBytes, int offset) {
			int result = 0;
			for (int i = 0; i < numberOfBytes; i++) {
				result += mapContent[i+offset] << (i << 3);
			}
			return result;
		}

		boolean isChecksumValid() {
			long fileChecksum = readBEIntFrom(4, 0);
			System.out.println(fileChecksum);
			int count = mapContent.length;

			int currentChecksum = 0;
			for (int i = 8; i < count ; i+=4) {
				int currentInt = readBEIntFrom(4, i);
				
				//- using: Logic Right-Shift-Operator: >>>
				currentChecksum = ((currentChecksum >>> 31) | ((currentChecksum << 1) ^ currentInt));
			}

			if (currentChecksum != fileChecksum) {
				return false;
			}

			return true;
		}

	}

	public static void main(String[] args) {
		(new OriginalMapLoader()).loadOriginalMap(new File("D:\\Spiele\\Siedler3\\Map\\User\\a.map"));
	}

}
