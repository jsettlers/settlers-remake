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

	private RandomMapFile(String name) {
		try {
		    String file = "maps/" + name.replaceAll("[\\.\\/\\\\]", "") + ".map";
			InputStream stream = ResourceManager.getFile(file);
			LineNumberReader reader =
			        new LineNumberReader(new InputStreamReader(stream));
			readNextSection(reader);
			stream.close();
		} catch (IOException e) {
			// TODO: use default set, error, ...
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

	public static RandomMapFile getByName(String name) {
	    return new RandomMapFile(name);
    }
}
