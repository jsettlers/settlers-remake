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
package jsettlers.logic.map.loading.list;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IListedMap {
	/**
	 * Returns the file name without the .map extension.
	 * 
	 * @return
	 */
	String getFileName();

	/**
	 * Gets a stream for that map.
	 * 
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Deletes the map from the disk storage.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the file cannot be deleted.
	 */
	void delete();

	/**
	 * 
	 * @return Returns true if this is a compressed map file.
	 */
	boolean isCompressed();

	/**
	 * Returns the File object of this {@link IListedMap} file if possible.
	 * 
	 * @return
	 */
	File getFile();
}
