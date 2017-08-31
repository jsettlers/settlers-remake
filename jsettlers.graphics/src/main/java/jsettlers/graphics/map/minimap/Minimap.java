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
package jsettlers.graphics.map.minimap;

import go.graphics.GLDrawContext;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.LinkedList;

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
 *       (width * stride | height)      (width * (stride + 1) | height)
 *  (0 | 0)         (width * 1 | 0)
 * </pre>
 *
 * currently stride is fixed to (mapwidth / 2) / mapwidth.
 *
 * @author michael
 */
public final class Minimap implements IMinimapData {
	private final MapCoordinateConverter converter;
	private int width;
	private int height;
	private TextureHandle texture = null;
	private final float stride;

	private boolean imageIsValid = false;
	private final Object updateMutex = new Object();
	private final MapDrawContext context;

	private MapRectangle mapViewport;
	private short[][] buffer;
	private final LinkedList<Integer> updatedLines = new LinkedList<>();
	private final LineLoader lineLoader;
	private boolean stopped = false;

	public Minimap(MapDrawContext context, MinimapMode modeSettings) {
		this.context = context;
		IGraphicsGrid map = context.getMap();
		stride = MiniMapLayoutProperties.getStride(map.getWidth()) / map.getWidth();
		converter = new MapCoordinateConverter(map.getWidth(), map.getHeight(), 1, 1);
		lineLoader = new LineLoader(this, modeSettings);
		Thread minimapThread = new Thread(lineLoader, "minimap loader");
		minimapThread.setDaemon(true);
		minimapThread.start();
	}

	public void setSize(int width, int height) {
		synchronized (updateMutex) {
			this.width = width;
			this.height = height;
			imageIsValid = false;
			updateMutex.notifyAll();
		}
	}

	public void draw(GLDrawContext context) {
		boolean imageWasCreatedJustNow = false;
		try {
			synchronized (updateMutex) {
				if (!imageIsValid) {
					imageWasCreatedJustNow = true;
					if (texture != null) {
						texture.delete();
						texture = null;
					}
					ShortBuffer data = ByteBuffer.allocateDirect(width * height * 2)
							.order(ByteOrder.nativeOrder()).asShortBuffer();
					for (int i = 0; i < width * height; i++) {
						data.put(LineLoader.BLACK);
					}
					data.position(0);
					texture = context.generateTexture(width, height, data);
					updatedLines.clear();
					imageIsValid = true;
				}

				if (!updatedLines.isEmpty()) {
					ShortBuffer currData = ByteBuffer.allocateDirect(width * 2)
							.order(ByteOrder.nativeOrder()).asShortBuffer();
					for (Integer currLine : updatedLines) {
						currData.position(0);
						currData.put(buffer[currLine]);
						currData.position(0);

						context.updateTexture(texture, 0, currLine, width, 1,
								currData);
					}
					updatedLines.clear();
				}
				updateMutex.notifyAll();
			}

			context.color(1, 1, 1, 1);
			context.drawQuadWithTexture(texture, new float[] {
					0,
					0,
					0,
					0,
					0,
					width,
					0,
					0,
					1,
					0,
					(stride + 1) * width,
					height,
					0,
					1,
					1,
					stride * width,
					height,
					0,
					0,
					1,
			});

			drawViewmark(context);
		} catch (IllegalBufferException e) {
			if (imageWasCreatedJustNow) {
				// TODO: Error reporting
				e.printStackTrace();
			} else {
				// Retry with a new image.
				synchronized (updateMutex) {
					imageIsValid = false;
				}
				draw(context);
			}
		}
	}

	private void drawViewmark(GLDrawContext context) {
		if (mapViewport == null) {
			return;
		}
		int lineStartX = mapViewport.getLineStartX(0);
		int firstY = mapViewport.getLineY(0);
		float minviewx = converter.getViewX(lineStartX, firstY, 0) * width;
		float maxviewy = Math.min(converter.getViewY(lineStartX, firstY, 0), 1) * height;
		float maxviewx = converter.getViewX(mapViewport.getLineEndX(0), firstY, 0)
				* width;
		int lastY = mapViewport.getLineY(mapViewport.getLines());
		float minviewy = Math.max(converter.getViewY(lineStartX, lastY, 0), 0) * height;

		context.drawLine(
				new float[] {
						// bottom left
						Math.max(minviewx, minviewy / height * stride * width),
						minviewy,
						0,
						// bottom right
						Math.min(maxviewx, (minviewy / height * stride + 1)
								* width),
						minviewy,
						0,
						Math.min(maxviewx, (maxviewy / height * stride + 1)
								* width),
						Math.max(
								(Math.min(maxviewx,
										(maxviewy / height * stride + 1)
												* width)
												- width)
										/ width / stride * height,
								minviewy),
						0,
						// top right
						Math.min(maxviewx, (maxviewy / height * stride + 1)
								* width),
						maxviewy,
						0,
						// top left
						Math.max(minviewx, maxviewy / height * stride * width),
						maxviewy,
						0,
						Math.max(minviewx, minviewy / height * stride * width),
						Math.min(
								Math.max(minviewx, minviewy / height * stride
										* width)
										/ width / stride * height,
								maxviewy),
						0,
				}, true);
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
	 * @param data
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
		mapViewport = rect;
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
