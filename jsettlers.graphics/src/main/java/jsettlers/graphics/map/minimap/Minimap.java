/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.graphics.map.minimap;

import go.graphics.EPrimitiveType;
import go.graphics.GLDrawContext;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import go.graphics.UnifiedDrawHandle;
import go.graphics.VkDrawContext;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.original.MiniMapLayoutProperties;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * This is the minimap. It is drawn on on the rectangle:
 *
 * <pre>
 * (width * stride | height)  (width * (stride + 1) | height)
 *                      .--------.
 *                    /         /
 *                  /         /
 *                /         /
 *               .--------.
 *           (0 | 0)  (width * 1 | 0)
 * </pre>
 * the origin of coordinates is in the bottom left and values increase to the top right.
 * currently stride is fixed to (mapwidth / 2) / mapwidth resulting in 0.5.
 *
 * @author michael
 */
public final class Minimap implements IMinimapData {
	private final MapCoordinateConverter converter;
	private final MiniMapShapeCalculator miniMapShapeCalculator;

	private final float               stride;
	private final Object              updateMutex  = new Object();
	private final MapDrawContext      context;
	private final LinkedList<Integer> updatedLines = new LinkedList<>();
	private final LineLoader          lineLoader;

	private int           width;
	private int           height;
	private TextureHandle texture      = null;
	private boolean       imageIsValid = false;

	private short[][] buffer;
	private boolean   stopped = false;

	public Minimap(MapDrawContext context, MinimapMode modeSettings) {
		this.context = context;
		IGraphicsGrid map = context.getMap();
		stride = MiniMapLayoutProperties.getStride(map.getWidth()) / map.getWidth();
		converter = new MapCoordinateConverter(map.getWidth(), map.getHeight(), 1, 1);
		miniMapShapeCalculator = new MiniMapShapeCalculator(stride, converter);
		lineLoader = new LineLoader(this, modeSettings);
		Thread miniMapThread = new Thread(lineLoader, "minimap loader");
		miniMapThread.setDaemon(true);
		miniMapThread.start();
	}

	public void setSize(int width, int height) {
		synchronized (updateMutex) {
			this.width = width;
			this.height = height;
			miniMapShapeCalculator.setWidth(width);
			miniMapShapeCalculator.setHeight(height);
			updateGeometry = true;
			imageIsValid = false;
		}
	}

	private boolean updateGeometry = true;
	private UnifiedDrawHandle geometry = null;
	private UnifiedDrawHandle lineGeometry = null;
	private static final ByteBuffer lineBfr = ByteBuffer.allocateDirect(12*4).order(ByteOrder.nativeOrder());

	private ByteBuffer bfr = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());

	private void replaceBufferValue(GLDrawContext context, int pos, float value) throws IllegalBufferException {
		bfr.putFloat(0, value);
		context.updateBufferAt(geometry.vertices, pos*4, bfr);
	}

	public void draw(GLDrawContext context, float x, float y) {
		boolean imageWasCreatedJustNow = false;
		try {
			synchronized (updateMutex) {
				if (!imageIsValid || texture == null || !texture.isValid()) {
					imageWasCreatedJustNow = true;
					ShortBuffer data = ByteBuffer.allocateDirect(width * height * 2)
												 .order(ByteOrder.nativeOrder()).asShortBuffer();
					for (int i = 0; i < width * height; i++) {
						data.put(LineLoader.BLACK);
					}
					data.position(0);
					if(texture != null && texture.isValid()) {
						texture = context.resizeTexture(texture, width, height, data);
					} else {
						texture = context.generateTexture(width, height, data, "minimap");
					}
					updatedLines.clear();
					imageIsValid = true;
				}

				int updatedLineCount = updatedLines.size();
				if (updatedLineCount > 0) {
					if(context instanceof VkDrawContext) {
						int padding = -(-width%2);

						ByteBuffer currData = ByteBuffer.allocateDirect((width+padding) * 2 * updatedLineCount)
								.order(ByteOrder.nativeOrder());
						ShortBuffer currShortData = currData.asShortBuffer();
						List<int[]> updateInfo = new ArrayList<>(updatedLineCount);
						for(int i = 0; i != updatedLineCount; i++) {
							int currLine = updatedLines.get(i);
							currShortData.position((width+padding)*i);
							currShortData.put(buffer[currLine]);
							updateInfo.add(new int[] {0, currLine, width, 1, (width+padding)*2*i});
						}
						((VkDrawContext)context).updateTexture(texture, updateInfo, currData);
					} else {
						ShortBuffer currData = ByteBuffer.allocateDirect(width * 2)
								.order(ByteOrder.nativeOrder()).asShortBuffer();
						for(int i = 0; i != updatedLineCount; i++) {
							int currLine = updatedLines.get(i);
							currData.position(0);
							currData.put(buffer[currLine]);
							currData.position(0);

							context.updateTexture(texture, 0, currLine, width, 1, currData);
						}
					}
					updatedLines.clear();
				}
				updateMutex.notifyAll();
			}

			if(geometry == null || !geometry.isValid()) {
				geometry = context.createUnifiedDrawCall(4, "minimap", texture, new float[] {0, 0, 0, 0, width, 0, 1, 0,(stride + 1) * width, height, 1, 1, stride * width, height, 0, 1});
				lineGeometry = context.createUnifiedDrawCall(6, "minimap-frame", null, null);
			}

			if(updateGeometry) {
				lineBfr.asFloatBuffer().put(miniMapShapeCalculator.getMiniMapShapeNodes(), 0, 12);
				context.updateBufferAt(lineGeometry.vertices, 0, lineBfr);

				replaceBufferValue(context, 4, width);
				replaceBufferValue(context, 8, (stride + 1) * width);
				replaceBufferValue(context, 9, height);
				replaceBufferValue(context, 12, stride * width);
				replaceBufferValue(context, 13, height);
				updateGeometry = false;
			}

			geometry.drawSimple(EPrimitiveType.Quad, x, y, 0, 1, 1, null, 1);
			lineGeometry.drawSimple(EPrimitiveType.LineLoop, x, y, 0, 1, 1, null, 1);
		} catch (IllegalBufferException e) {
			if (imageWasCreatedJustNow) {
				// TODO: Error reporting
				e.printStackTrace();
			} else {
				// Retry with a new image.
				synchronized (updateMutex) {
					imageIsValid = false;
				}
				draw(context, x, y);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Sets the data
	 *
	 * @param line
	 * Line to be added to updatedLines
	 */
	public void setUpdatedLine(int line) {
		synchronized (updateMutex) {
			updatedLines.add(line);
		}
	}

	public MapDrawContext getContext() {
		return context;
	}

	public ShortPoint2D getClickPositionIfOnMap(float relativex, float relativey) {
		int x = converter.getMapX(relativex, relativey);
		int y = converter.getMapY(relativex, relativey);

		if (context.checkMapCoordinates(x, y)) {
			return new ShortPoint2D(x, y);
		} else {
			return null;
		}
	}

	public void setMapViewport(MapRectangle rect) {
		miniMapShapeCalculator.setMapViewport(rect);
		updateGeometry = true;
	}

	/**
	 * a call to this method blocks until it's ok to update a line.
	 */
	public void blockUntilUpdateAllowedOrStopped() {
		synchronized (updateMutex) {
			while (!stopped
				&& (!updatedLines.isEmpty() || width < 1 || height < 1)) {
				try {
					updateMutex.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setBufferArray(short[][] buffer) {
		this.buffer = buffer;
	}

	public void stop() {
		lineLoader.stop();
		stopped = true;
		synchronized (updateMutex) {

		}
	}
}
