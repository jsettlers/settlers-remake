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
package jsettlers.common.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is a object that provides access to resources needed for the game.
 * 
 * @author michael
 */
public interface IResourceProvider {
	/**
	 * Gets a input steam for a resources file name.
	 * 
	 * @param name
	 *            The name of the file. With directory, separated by "/"
	 * @return The InputStream
	 * @throws IOException
	 *             If no stream could be generated
	 */
	InputStream getResourcesFileStream(String name) throws IOException;

	/**
	 * Gets a output steam to write a file with the name.<br>
	 * It also creates parent folders as needed.<br>
	 * The file will be located in a settings folder not directly visible to the user
	 * 
	 * @param name
	 *            The name of the file. With directory, separated by "/"
	 * @return The InputStream
	 * @throws IOException
	 *             If no stream could be generated
	 */
	OutputStream writeConfigurationFile(String name) throws IOException;

	/**
	 * Gets a output steam to write a file with the name.<br>
	 * It also creates parent folders as needed.<br>
	 * The file will be located in the installation folder or another directly visible folder.
	 *
	 * @param name
	 *            The name of the file. With directory, separated by "/"
	 * @return The InputStream
	 * @throws IOException
	 *             If no stream could be generated
	 */
	OutputStream writeUserFile(String name) throws IOException;


	/**
	 * Gets a directory to store settings and other stuff.
	 * 
	 * @see ResourceManager#getResourcesDirectory()
	 * @return The directory, where the user has write permissions.
	 */
	File getResourcesDirectory();

}
