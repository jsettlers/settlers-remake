package jsettlers.logic.map.save;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import jsettlers.common.map.IMapData;

public class MapSaver {
	public static void saveMap(MapFileHeader header, IMapData data, OutputStream out) throws IOException {
		header.writeTo(out);
		MapDataSerializer.serialize(data, out);
	}

	public static void saveRandomMap(MapFileHeader header, String definition,
			OutputStream out) throws IOException {
		header.writeTo(out);
		OutputStreamWriter writer = new OutputStreamWriter(out);
		writer.write(definition);
	}
}
