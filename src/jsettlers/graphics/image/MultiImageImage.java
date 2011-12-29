package jsettlers.graphics.image;

import go.graphics.Color;
import go.graphics.GLDrawContext;
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

	private static float[] createGeometry(MultiImageMap map,
	        ImageMetadata settlerMeta, int settlerx, int settlery) {
		return new float[] {
		        // top left
		        settlerMeta.offsetX,
		        -settlerMeta.offsetY - settlerMeta.height,
		        0,
		        (float) settlerx / map.getWidth(),
		        (float) (settlery + settlerMeta.height) / map.getHeight(),

		        // bottom left
		        settlerMeta.offsetX,
		        -settlerMeta.offsetY,
		        0,
		        (float) settlerx / map.getWidth(),
		        (float) settlery / map.getHeight(),

		        // bottom right
		        settlerMeta.offsetX + settlerMeta.width,
		        -settlerMeta.offsetY,
		        0,
		        (float) (settlerx + settlerMeta.width) / map.getWidth(),
		        (float) settlery / map.getHeight(),

		        // top right
		        settlerMeta.offsetX + settlerMeta.width,
		        -settlerMeta.offsetY - settlerMeta.height,
		        0,
		        (float) (settlerx + settlerMeta.width) / map.getWidth(),
		        (float) (settlery + settlerMeta.height) / map.getHeight(),
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
		tmpBuffer[0] = left;
		tmpBuffer[1] = top;
		tmpBuffer[5] = left;
		tmpBuffer[6] = bottom;
		tmpBuffer[10] = right;
		tmpBuffer[11] = bottom;
		tmpBuffer[15] = right;
		tmpBuffer[16] = top;

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
