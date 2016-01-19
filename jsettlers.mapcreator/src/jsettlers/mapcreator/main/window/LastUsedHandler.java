/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.main.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import jsettlers.common.resources.ResourceManager;

/**
 * Helper class to load / save last used maps
 * 
 * @author Andreas Butti
 *
 */
public class LastUsedHandler {

	/**
	 * File to load / save
	 */
	private File lastUsedFile;

	/**
	 * List with the last used files (IDs)
	 */
	private LinkedList<String> lastUsed = new LinkedList<>();

	/**
	 * Load last used list (if any)
	 */
	public LastUsedHandler() {
		lastUsedFile = new File(ResourceManager.getResourcesDirectory(), "last-used.properties");

		if (!lastUsedFile.exists()) {
			return;
		}

		try {
			Properties p = new Properties();
			p.load(new FileInputStream(lastUsedFile));

			for (int i = 0; i < 10; i++) {
				String value = p.getProperty("id" + i);
				if (value != null) {
					lastUsed.add(value);
				}
			}

		} catch (Exception e) {
			System.err.println("Could not load last used list");
			e.printStackTrace();
		}
	}

	/**
	 * @return List with the last used files
	 */
	public List<String> getLastUsed() {
		return Collections.unmodifiableList(lastUsed);
	}

	/**
	 * Save an id to the list
	 * 
	 * @param id
	 *            ID
	 */
	public void saveUsedMapId(String id) {
		Iterator<String> it = lastUsed.iterator();
		while (it.hasNext()) {
			if (it.next().equals(id)) {
				it.remove();
			}
		}

		lastUsed.addFirst(id);

		try {
			Properties p = new Properties();

			int i = 0;
			for (String usedId : lastUsed) {
				p.setProperty("id" + i, usedId);
				i++;
			}

			p.store(new FileOutputStream(lastUsedFile), "last used list");
		} catch (Exception e) {
			System.err.println("Could not save last used list");
			e.printStackTrace();
		}
	}

}
