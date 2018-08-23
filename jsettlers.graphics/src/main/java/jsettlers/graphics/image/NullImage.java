/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.graphics.image;

import java.nio.ShortBuffer;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import jsettlers.common.Color;
import jsettlers.graphics.image.reader.ImageMetadata;

/**
 * This is a null image.
 * <p>
 * It may be returned by image methods if the requested image is not available.
 * 
 * @author Michael Zangl
 */
public final class NullImage extends SingleImage {
	private static final float NULL_IMAGE_ALPHA = 0.5f;
	private static final short[] NULL_DATA = new short[] { 0 };
	private static final int HALFSIZE = 3;
	private static final ImageMetadata NULL_IMAGE_METADATA = new ImageMetadata();

	static {
		NULL_IMAGE_METADATA.width = 1;
		NULL_IMAGE_METADATA.height = 1;
		NULL_IMAGE_METADATA.offsetX = 0;
		NULL_IMAGE_METADATA.offsetY = 0;
	}

	private static NullImage instance;
	private static SingleImage landscapeinstance = null;

	/**
	 * Gets an instance of the null image.
	 * 
	 * @return An instance.
	 */
	public static NullImage getInstance() {
		if (instance == null) {
			instance = new NullImage();
		}
		return instance;
	}

	private NullImage() {
		super(ShortBuffer.allocate(1), 1, 1, 0, 0, "placeholder/null");
	}


	private static GeometryHandle nullGeometry = null;

	private static final float[] nullData = new float[] {
			-HALFSIZE,
			-HALFSIZE,
			+HALFSIZE,
			-HALFSIZE,
			+HALFSIZE,
			+HALFSIZE,
			-HALFSIZE,
			+HALFSIZE,
	};

	@Override
	public void drawOnlyImageAt(GLDrawContext gl, float x, float y, float z, Color torsoColor, float fow) {
		try {
			if(nullGeometry == null || !nullGeometry.isValid()) nullGeometry = gl.storeGeometry(nullData, EGeometryFormatType.VertexOnly2D, false, "placeholder/null");

			gl.draw2D(nullGeometry, null, EGeometryType.Quad, 0, 4, x, y, z, 1, 1, 1, null, NULL_IMAGE_ALPHA);
			gl.draw2D(nullGeometry, null, EGeometryType.LineLoop, 0, 4, x, y, z, 1, 1, 1, Color.RED, 1);
		} catch (IllegalBufferException e) {
			e.printStackTrace();
		}
	}

	private static SingleImage guiinstance;

	/**
	 * Gets an empty landscape image.
	 * 
	 * @return The imge instance.
	 */
	public static SingleImage getForLandscape() {
		if (landscapeinstance == null) {
			landscapeinstance = new SingleImage(NULL_IMAGE_METADATA, NULL_DATA, "landscape-placeholder/null");
		}
		return landscapeinstance;
	}

	/**
	 * Gets an empty gui image.
	 * 
	 * @return The imge instance.
	 */
	public static SingleImage getForGui() {
		if (guiinstance == null) {
			guiinstance = new SingleImage(NULL_IMAGE_METADATA, NULL_DATA, "gui-placeholder/null");
		}
		return guiinstance;
	}
}
