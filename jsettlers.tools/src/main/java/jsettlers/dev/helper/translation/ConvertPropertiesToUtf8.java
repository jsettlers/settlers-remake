package jsettlers.dev.helper.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert properties from standard encoding to UTF-8
 * 
 * @author Andreas Butti
 *
 */
public class ConvertPropertiesToUtf8 {

	/**
	 * Path with propertiy files
	 */
	private static final String[] TRANSLATION_PROPERTIES = { "../jsettlers.mapcreator/src/jsettlers/mapcreator/localization" };

	/**
	 * List all relevant properties
	 * 
	 * @return Property list
	 */
	public static List<File> listProperties() {
		List<File> list = new ArrayList<>();

		for (String t : TRANSLATION_PROPERTIES) {
			File root = new File(t);

			for (File f : root.listFiles()) {
				if (f.getName().endsWith(".properties")) {
					list.add(f);
				}
			}

		}

		return list;
	}

	/**
	 * Read the property file to string
	 * 
	 * @param file
	 *            File
	 * @return String
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static String readProperty(File file) throws IOException {
		StringBuilder b = new StringBuilder();
		try (FileInputStream f = new FileInputStream(file)) {
			boolean lastWasBackslash = false;
			int parseUnicodeChars = 0;
			char[] unicodeChars = new char[4];

			while (true) {
				int ci = f.read();
				if (ci == -1) {
					break;
				}
				char c = (char) ci;

				if (parseUnicodeChars > 0) {
					unicodeChars[4 - parseUnicodeChars] = c;

					parseUnicodeChars--;

					if (parseUnicodeChars == 0) {
						char uc = (char) Integer.parseInt(new String(unicodeChars), 16);
						b.append(uc);
					}

					continue;
				}

				if (lastWasBackslash) {
					if (c == 'u') {
						parseUnicodeChars = 4;
						lastWasBackslash = false;
						continue;
					} else {
						b.append('\\');
					}
					lastWasBackslash = false;
				}

				if (c == '\\') {
					lastWasBackslash = true;
					continue;
				}

				b.append(c);
			}
		}
		return b.toString();
	}

	/**
	 * Convert encoding
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		for (File f : listProperties()) {
			System.out.println("->" + f);

			String contents = readProperty(f);
			try (FileOutputStream out = new FileOutputStream(f)) {
				out.write(contents.getBytes("utf-8"));
			}
		}
	}

}
