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
package jsettlers.algorithms.previewimage;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This interface defines the methods needed by the {@link PreviewImageCreator} to calculate a preview image that can be saved in a map file's header.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPreviewImageDataSupplier {

	/**
	 * Gets the {@link ELandscapeType} at the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	ELandscapeType getLandscape(short x, short y);

	/**
	 * Gets the height of the landscape at the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	byte getLandscapeHeight(short x, short y);

}
