package jsettlers.graphics.map.minimap;

import go.graphics.GLDrawContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.LinkedList;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;

/**
 * This is the minimap. It is drawn on on the rectangle:
 * 
 * <pre>
 *       (width * stride | height)      (width * (stride + 1) | height)
 *  (0 | 0)         (width * 1 | 0)
 * </pre>
 * 
 * currently stride is fixed to mapheigh / 2 / mapwidth
 * 
 * @author michael
 */
public final class Minimap {
	private final MapCoordinateConverter converter;
	private int width;
	private int height;
	private int imageIndex = -1;
	private final float stride;

	private boolean imageIsValid = false;
	private final Object update_syncobj = new Object();
	private final MapDrawContext context;

	private MapRectangle mapViewport;
	private short[][] buffer;
	private final LinkedList<Integer> updatedLines = new LinkedList<Integer>();
	private final LineLoader lineLoader;
	private boolean stopped = false;

	public Minimap(MapDrawContext context) {
		this.context = context;
		IGraphicsGrid map = context.getMap();
		stride = map.getHeight() / 2.0f / map.getWidth();
		converter =
				new MapCoordinateConverter(map.getWidth(), map.getHeight(), 1,
						1);
		lineLoader = new LineLoader(this);
		Thread minimapThread = new Thread(lineLoader, "minimap loader");
		minimapThread.setDaemon(true);
		minimapThread.start();
	}

	public void setSize(int width, int height) {
		synchronized (update_syncobj) {
			this.width = width;
			this.height = height;
			imageIsValid = false;
			update_syncobj.notifyAll();
		}
	}

	public void draw(GLDrawContext context) {
		synchronized (update_syncobj) {
			if (!imageIsValid) {
				if (imageIndex < 0) {
					context.deleteTexture(imageIndex);
				}
				ShortBuffer data =
						ByteBuffer.allocateDirect(width * height * 2)
								.order(ByteOrder.nativeOrder()).asShortBuffer();
				for (int i = 0; i < width * height; i++) {
					data.put((short) 0x0001);
				}
				data.position(0);
				imageIndex = context.generateTexture(width, height, data);
				updatedLines.clear();
				imageIsValid = true;
			}

			if (!updatedLines.isEmpty()) {
				ShortBuffer currData =
						ByteBuffer.allocateDirect(width * 2)
								.order(ByteOrder.nativeOrder()).asShortBuffer();
				for (Integer currLine : updatedLines) {
					currData.position(0);
					currData.put(buffer[currLine]);
					currData.position(0);

					context.updateTexture(imageIndex, 0, currLine, width, 1,
							currData);
				}
				updatedLines.clear();
			}
			update_syncobj.notifyAll();
		}

		context.color(1, 1, 1, 1);
		context.drawQuadWithTexture(imageIndex, new float[] {
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
	}

	private void drawViewmark(GLDrawContext context) {
		if (mapViewport == null) {
			return;
		}
		int lineStartX = mapViewport.getLineStartX(0);
		int firstY = mapViewport.getLineY(0);
		float minviewx = converter.getViewX(lineStartX, firstY, 0) * width;
		float maxviewy =
				Math.min(converter.getViewY(lineStartX, firstY, 0), 1) * height;
		float maxviewx =
				converter.getViewX(mapViewport.getLineEndX(0), firstY, 0)
						* width;
		int lastY = mapViewport.getLineY(mapViewport.getLines());
		float minviewy =
				Math.max(converter.getViewY(lineStartX, lastY, 0), 0) * height;

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
												* width) - width)
										/ width / stride * height, minviewy),
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
										/ width / stride * height, maxviewy),
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
		synchronized (update_syncobj) {
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

	public ShortPoint2D getClickPosition(float relativex, float relativey) {
		int x = converter.getMapX(relativex, relativey);
		int y = converter.getMapY(relativex, relativey);
		if (x < 0) {
			x = 0;
		} else if (x >= context.getMap().getWidth()) {
			x = context.getMap().getWidth() - 1;
		}
		if (y < 0) {
			y = 0;
		} else if (y >= context.getMap().getHeight()) {
			y = context.getMap().getHeight() - 1;
		}
		return new ShortPoint2D(x, y);
	}

	public void setMapViewport(MapRectangle rect) {
		mapViewport = rect;
	}

	/**
	 * a call to this method blocks until it's ok to update a line.
	 */
	public void blockUntilUpdateAllowedOrStopped() {
		synchronized (update_syncobj) {
			while (!stopped
					&& (!updatedLines.isEmpty() || width < 1 || height < 1)) {
				try {
					update_syncobj.wait();
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
		synchronized (update_syncobj) {

		}
	}

}
