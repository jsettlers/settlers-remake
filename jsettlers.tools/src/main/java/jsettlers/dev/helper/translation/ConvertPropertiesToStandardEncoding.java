package jsettlers.dev.helper.translation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Convert the translation properties to standard Encoding ISO-8859-1 and \\uXXXX for unicode chars
 * 
 * @author Andreas Butti
 *
 */
public class ConvertPropertiesToStandardEncoding {

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
		byte[] encoded = Files.readAllBytes(file.toPath());
		return new String(encoded, "utf-8");
	}

	/**
	 * Encode the property as ISO-8859-1 and escape all unicode chars
	 * 
	 * @param contents
	 *            Contents to write out
	 * @param f
	 *            Target file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void encodeProperty(String contents, File f) throws IOException {
		try (FileOutputStream out = new FileOutputStream(f)) {
			for (int i = 0; i < contents.length(); i++) {
				int c = contents.charAt(i);

				if (c <= 127) {
					out.write(c);
				} else {
					out.write('\\');
					out.write('u');

					String str = String.format("%04x", c).toUpperCase();
					out.write(str.getBytes());
				}
			}

		}

	}

	/**
	 * Convert encoding
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		for (File f : ConvertPropertiesToUtf8.listProperties()) {
			System.out.println("->" + f);

			String contents = readProperty(f);
			encodeProperty(contents, f);

		}
	}

}
