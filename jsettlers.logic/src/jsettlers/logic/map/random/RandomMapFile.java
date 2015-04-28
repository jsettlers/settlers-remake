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
package jsettlers.logic.map.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.logic.map.random.instructions.GenerationInstruction;
import jsettlers.logic.map.random.instructions.MetaInstruction;

/**
 * This class defines properties for a random map file.
 * 
 * @author michael
 */
public class RandomMapFile {
	private static final Pattern HEAD_LINE = Pattern
			.compile("\\[(\\w+)\\]\\s*");
	private static final Pattern PARAM_LINE = Pattern
			.compile("\\s*(\\w+)\\s*=\\s*(.+)");
	private List<GenerationInstruction> instructions =
			new ArrayList<GenerationInstruction>();

	private RandomMapFile(InputStream stream) throws MapLoadException {
		try {
			LineNumberReader reader =
					new LineNumberReader(new InputStreamReader(stream));
			readNextSection(reader);
		} catch (IOException e) {
			throw new MapLoadException("Error during map creation", e);
		}
	}

	private void readNextSection(LineNumberReader reader) throws IOException {
		// this instruction is ignored!
		GenerationInstruction currentInstruction = new MetaInstruction();

		while (true) {
			String line = getNexLine(reader);
			if (line == null) {
				break;
			}

			Matcher headlineMatcher = HEAD_LINE.matcher(line);
			Matcher parameterMatcher = PARAM_LINE.matcher(line);
			if (headlineMatcher.matches()) {
				String type = headlineMatcher.group(1);
				currentInstruction = GenerationInstruction.createByType(type);
				this.instructions.add(currentInstruction);
			} else if (parameterMatcher.matches()) {
				String key = parameterMatcher.group(1);
				String value = parameterMatcher.group(2);
				currentInstruction.setParameter(key, value);
			} else {
				System.err.println("Random map generation definition:\n"
						+ "Syntax error in line " + reader.getLineNumber());
			}

		}

	}

	private static String getNexLine(BufferedReader reader) throws IOException {
		String line = "";
		while (line != null && (line.isEmpty() || line.startsWith("#"))) {
			line = reader.readLine();
		}
		return line;
	}

	public List<GenerationInstruction> getInstructions() {
		return instructions;
	}

	public static RandomMapFile getByName(String name) throws MapLoadException {
		String file = "maps/" + name.replaceAll("[\\.\\/\\\\]", "") + ".map";
		InputStream stream;
		try {
			stream = ResourceManager.getFile(file);
		} catch (IOException e) {
			throw new MapLoadException(e);
		}
		return loadFromStream(stream);
	}

	public static RandomMapFile loadFromStream(InputStream stream) throws MapLoadException {
		return new RandomMapFile(stream);
	}
}
