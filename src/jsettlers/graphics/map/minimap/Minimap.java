package jsettlers.graphics.map.minimap;

import java.nio.ShortBuffer;

import go.graphics.GLDrawContext;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ISPosition2D;
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
public class Minimap {
	private MapCoordinateConverter converter;
	private int width;
	private int height;
	private int imageIndex = -1;
	private float stride;
	private int updateline = -1; // line in the update buffer. -1 means invalid.
	/**
	 * last line a update was requested for.
	 */
	private int lastUpdateline = 0;
	private ShortBuffer updateData;
	private boolean imageIsValid = false;
	private Object update_syncobj = new Object();
	private final MapDrawContext context;

	private MapRectangle mapViewport;

	public Minimap(MapDrawContext context) {
		this.context = context;
		IGraphicsGrid map = context.getMap();
		stride = map.getHeight() / 2.0f / map.getWidth();
		converter =
		        new MapCoordinateConverter(map.getWidth(), map.getHeight(), 1,
		                1);
		new Thread(new LineLoader(this)).start();
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
				ShortBuffer data = ShortBuffer.allocate(width * height);
				for (int i = 0; i < width * height; i++) {
					data.put((short) 0x0001);
				}
				data.position(0);
				imageIndex = context.generateTexture(width, height, data);
				updateline = -1;
				imageIsValid = true;
			}

			if (updateline >= 0 && updateData.remaining() == width) {
				context.updateTexture(imageIndex, 0, updateline, width, 1,
				        updateData);
			}
			updateline = -1;
			update_syncobj.notifyAll();
		}

		context.color(1, 1, 1, 1);
		context.drawQuadsWithTexture(imageIndex, new float[] {
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
	    int lineStartX = mapViewport.getLineStartX(0);
		int firstY = mapViewport.getLineY(0);
		float minviewx = converter.getViewX(lineStartX, firstY, 0) * width;
		float maxviewy = converter.getViewY(lineStartX, firstY, 0) * height;
		float maxviewx =
		        converter.getViewX(mapViewport.getLineEndX(0), firstY, 0) * width;
		float minviewy =
		        converter.getViewY(lineStartX,
		                mapViewport.getLineY(mapViewport.getLines()), 0) * height;
		context.drawLine(new float[] {
		        minviewx,
		        minviewy,
		        0,
		        maxviewx,
		        minviewy,
		        0,
		        maxviewx,
		        maxviewy,
		        0,
		        minviewx,
		        maxviewy,
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
	 * Blocks and gets the next line to update.
	 * 
	 * @return
	 */
	public int getNextUpdateLine() {
		synchronized (update_syncobj) {
			while (updateline >= 0 || width < 1 || height < 1) {
				try {
					update_syncobj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			lastUpdateline++;
			lastUpdateline %= height;
			return lastUpdateline;
		}
	}

	public void setUpdateData(int line, ShortBuffer data) {
		synchronized (update_syncobj) {
			updateData = data;
			updateline = line;
		}
	}

	public MapDrawContext getContext() {
		return context;
	}

	public ISPosition2D getClickPosition(float relativex, float relativey) {
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

}
