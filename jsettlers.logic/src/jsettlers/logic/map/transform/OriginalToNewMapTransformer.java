package jsettlers.logic.map.transform;

import jsettlers.common.map.IMapData;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.FreshMapData;

import java.io.*;

/**
 * @author codingberlin
 */
public class OriginalToNewMapTransformer {

	public static void main(String[] args) {
		new OriginalToNewMapTransformer().transform();
	}

	public String hex(int n) {
		return String.format("0x%8s", Integer.toHexString(n).toUpperCase()).replace(' ', '0');
	}
	public String bin(int n) {
		return String.format("0x%8s", Integer.toBinaryString(n).toUpperCase()).replace(' ', '0');
	}

	void transform() {
		try {
			File oldMap = new File("/Users/stephanbauer/Documents/siedler3/Map/MULTI/A640-4-River.map");
			//File oldMap = new File("/Users/stephanbauer/Documents/siedler3/Map/MULTI/M640-6-Teamwork.map");

			MapList mapList = new MapList(new File("/Users/stephanbauer/Documents/settlers-remake/jsettlers.common/resources"));

			DataInputStream oldMapStream = new DataInputStream(new FileInputStream(oldMap));


		long i = 0;
			do {
				long result = readUnsignedBytes(oldMapStream, (byte) 2);

				System.out.println(result);



				if (result == 640) {
					System.out.println("###### " + i);
					System.exit(1);
				}
					i++;
			} while(true);




			/*
			MapFileHeader mapHeader = new MapFileHeader(
					MapFileHeader.MapType.NORMAL,
					String name,
					String baseMapId,
					String description,
					short width,
					short height,
					1,
					short maxplayer,
					Date date,
					short[] bgimage

			);
			IMapData mapData = new FreshMapData();

			mapList.saveNewMap(mapHeader, mapData, null);*/
		} catch (IOException exception) {
			System.out.println("To transform the original map to new format did not work.");
			exception.printStackTrace();
		}
	}

	private long readUnsignedBytes(DataInputStream stream, byte count) throws IOException {
		long result = 0;
		for (byte i = 0; i < count; i++) {
			result = ((stream.readByte() & 0xff) << i*8) | result;
		}
		return result;
	}

}
