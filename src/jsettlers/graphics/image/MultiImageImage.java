package jsettlers.graphics.image;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is an image inside a multi image map.
 * 
 * @author michael
 */
public class MultiImageImage implements Image {
	private final MultiImageMap map;

	private final float[] settlerGeometry;
	private final float[] torsoGeometry;

	private final int width;

	private final int height;

	public MultiImageImage(MultiImageMap map, ImageMetadata settlerMeta,
	        int settlerx, int settlery, ImageMetadata torsoMeta, int torsox,
	        int torsoy) {
		this.map = map;
		this.width = settlerMeta.width;
		this.height = settlerMeta.height;
		settlerGeometry = createGeometry(map, settlerMeta, settlerx, settlery);
		if (torsoMeta != null) {
			torsoGeometry = createGeometry(map, torsoMeta, torsox, torsoy);
		} else {
			torsoGeometry = null;
		}
	}

	private static final float IMAGE_DRAW_OFFSET = .5f;

	private static float[] createGeometry(MultiImageMap map,
	        ImageMetadata settlerMeta, int settlerx, int settlery) {
		float umin = (float) settlerx / map.getWidth();
		float umax = (float) (settlerx + settlerMeta.width) / map.getWidth();

		float vmin = (float) (settlery + settlerMeta.height) / map.getHeight();
		float vmax = (float) (settlery) / map.getHeight();
		return new float[] {
		        // top left
		        settlerMeta.offsetX + IMAGE_DRAW_OFFSET,
		        -settlerMeta.offsetY - settlerMeta.height + IMAGE_DRAW_OFFSET,
		        0,
		        umin,
		        vmin,

		        // bottom left
		        settlerMeta.offsetX + IMAGE_DRAW_OFFSET,
		        -settlerMeta.offsetY + IMAGE_DRAW_OFFSET,
		        0,
		        umin,
		        vmax,

		        // bottom right
		        settlerMeta.offsetX + settlerMeta.width + IMAGE_DRAW_OFFSET,
		        -settlerMeta.offsetY + IMAGE_DRAW_OFFSET,
		        0,
		        umax,
		        vmax,

		        // top right
		        settlerMeta.offsetX + settlerMeta.width + IMAGE_DRAW_OFFSET,
		        -settlerMeta.offsetY - settlerMeta.height + IMAGE_DRAW_OFFSET,
		        0,
		        umax,
		        vmin,
		};
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y) {
		drawAt(gl, x, y, null);
	}

	@Override
	public void drawAt(GLDrawContext gl, float x, float y, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		draw(gl, color);
		gl.glPopMatrix();
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		draw(gl, color, 1);
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		gl.color(multiply, multiply, multiply, 1);
		int texture = map.getTexture(gl);
		gl.drawQuadWithTexture(texture, settlerGeometry);
		if (torsoGeometry != null) {
			if (color != null) {
				gl.color(color.getRed() * multiply,
				        color.getGreen() * multiply,
				        color.getBlue() * multiply, color.getAlpha());
			}
			gl.drawQuadWithTexture(texture, torsoGeometry);
		}
	}

	static private float[] tmpBuffer = new float[5 * 4];

	@Override
	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
	        float right, float top) {
		gl.color(1, 1, 1, 1);

		System.arraycopy(settlerGeometry, 0, tmpBuffer, 0, 4 * 5);
		tmpBuffer[0] = left + IMAGE_DRAW_OFFSET;
		tmpBuffer[1] = top + IMAGE_DRAW_OFFSET;
		tmpBuffer[5] = left + IMAGE_DRAW_OFFSET;
		tmpBuffer[6] = bottom + IMAGE_DRAW_OFFSET;
		tmpBuffer[10] = right + IMAGE_DRAW_OFFSET;
		tmpBuffer[11] = bottom + IMAGE_DRAW_OFFSET;
		tmpBuffer[15] = right + IMAGE_DRAW_OFFSET;
		tmpBuffer[16] = top + IMAGE_DRAW_OFFSET;

		gl.drawQuadWithTexture(map.getTexture(gl), tmpBuffer);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
