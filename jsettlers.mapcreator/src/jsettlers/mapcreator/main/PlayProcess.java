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
package jsettlers.mapcreator.main;

import java.io.File;

import jsettlers.exceptionhandler.ExceptionHandler;
import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;

/**
 * Main to play game
 * 
 * @author Andreas Butti
 *
 */
public class PlayProcess {

	/**
	 * Main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExceptionHandler.setupDefaultExceptionHandler();

			final File file = new File(args[0]);

			JSettlersGame game = new JSettlersGame(MapLoader.getLoaderForListedMap(new DirectoryMapLister.ListedMapFile(file)),
					123456L, (byte) 0, null);
			game.start();

		} catch (Exception e) {
			ExceptionHandler.displayError(e, "Error launching game");
		}

	}
}