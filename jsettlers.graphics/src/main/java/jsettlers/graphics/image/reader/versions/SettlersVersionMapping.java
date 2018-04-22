/*
 * Copyright (c) 2018
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
 */

package jsettlers.graphics.image.reader.versions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class SettlersVersionMapping {
	private static final String MAPPING_FILE_NAME = "mapping.json";

	private Map<String, String> mappingFilesByVersionId = new HashMap<>();

	public void putMapping(String settlersVersionId, String fileName) {
		mappingFilesByVersionId.put(settlersVersionId, fileName);
	}

	private GfxFolderMapping getMapping(String settlersVersionId) throws IOException {
		if (mappingFilesByVersionId.containsKey(settlersVersionId)) {
			String mappingFileName = mappingFilesByVersionId.get(settlersVersionId);
			InputStream mappingFileStream = getClass().getResourceAsStream(mappingFileName);
			return IndexingGfxFolderMapping.readFromStream(mappingFileStream);
		} else {
			return new DefaultGfxFolderMapping();
		}
	}

	public void serializeToDirectory(String directory) throws IOException {
		serializeToStream(new FileOutputStream(new File(directory, MAPPING_FILE_NAME)));
	}

	private void serializeToStream(OutputStream out) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(this);

		try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8")) {
			writer.write(jsonString);
		}
	}

	public static SettlersVersionMapping readFromDirectory(String directory) throws IOException {
		File file = new File(directory, MAPPING_FILE_NAME);
		if (file.exists()) {
			return readFromStream(new FileInputStream(file));
		} else {
			return new SettlersVersionMapping();
		}
	}

	private static SettlersVersionMapping readFromStream(InputStream in) throws IOException {
		try (Reader reader = new InputStreamReader(in, "utf-8")) {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, SettlersVersionMapping.class);
		}
	}

	public static GfxFolderMapping getMappingForVersionId(String settlersVersionId) {
		try {
			SettlersVersionMapping mappingFile = readFromStream(SettlersVersionMapping.class.getResourceAsStream(MAPPING_FILE_NAME));
			return mappingFile.getMapping(settlersVersionId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
