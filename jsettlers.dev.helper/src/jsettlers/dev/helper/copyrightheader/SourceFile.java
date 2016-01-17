package jsettlers.dev.helper.copyrightheader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * A source file
 * 
 * @author Andreas Butti
 */
public class SourceFile {

	/**
	 * The file represented
	 */
	private final File file;

	/**
	 * Contents of the file
	 */
	private String contents;

	/**
	 * Encoding of the java files
	 */
	private static final Charset FILE_ENCODING = Charset.forName("utf-8");

	/**
	 * Copyright header
	 */
	private static final String COPYRIGHT;

	static {
		try {
			InputStream in = SourceFile.class.getResourceAsStream("header.txt");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, FILE_ENCODING));
			String read;

			while ((read = br.readLine()) != null) {
				sb.append(read);
				sb.append('\n');
			}

			br.close();
			COPYRIGHT = sb.toString();
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not read header.txt", e);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param file
	 *            The file represented
	 * @throws IOException
	 */
	public SourceFile(File file) throws IOException {
		this.file = file;
		byte[] encoded = Files.readAllBytes(file.toPath());
		contents = new String(encoded, FILE_ENCODING);
	}

	/**
	 * Checks if the file already contains the copyright header
	 * 
	 * @return true if yes
	 */
	public boolean containsHeader() {
		return contents.contains("/*******************************************************************************");
	}

	/**
	 * Install the header in the file, and save the file
	 * 
	 * @throws IOException
	 */
	public void installHeader() throws IOException {
		String newContents = COPYRIGHT + contents;

		contents = newContents;

		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(newContents.getBytes(FILE_ENCODING));
		}

	}

}
