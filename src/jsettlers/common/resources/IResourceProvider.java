package jsettlers.common.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is a object that provides access to resources needed for the game.
 * 
 * @author michael
 * 
 */
public interface IResourceProvider {
	/**
	 * Gets a input steam for a file name.
	 * 
	 * @param name
	 *            The name of the file. With directory, separated by "/"
	 * @return The InputStream
	 * @throws IOException
	 *             If no stream could be generated
	 */
	InputStream getFile(String name) throws IOException;

	/**
	 * Gets a output steam to write a file with the name.<br>
	 * It also creates parent folders as needed.
	 * 
	 * @param name
	 *            The name of the file. With directory, separated by "/"
	 * @return The InputStream
	 * @throws IOException
	 *             If no stream could be generated
	 */
	OutputStream writeFile(String name) throws IOException;
}
