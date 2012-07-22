package jsettlers.graphics.image;

import go.graphics.GLDrawContext;
import jsettlers.common.Color;
import jsettlers.graphics.map.draw.DrawBuffer;

public class ImageIndexImage extends Image {
	private final short width;
	private final short height;
	private float[] geometry;
	private final ImageIndexTexture texture;
	private final int offsetX;
	private final int offsetY;
	private final float umin;
	private final float vmin;
	private final float umax;
	private final float vmax;

	protected ImageIndexImage(ImageIndexTexture texture, int offsetX,
	        int offsetY, short width, short height, float umin, float vmin,
	        float umax, float vmax) {
		this.texture = texture;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.umin = umin;
		this.vmin = vmin;
		this.umax = umax;
		this.vmax = vmax;
		
		geometry =
		        createGeometry(offsetX, offsetY, width, height, umin, vmin,
		                umax, vmax);
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
		if (color == null) {
			gl.color(1, 1, 1, 1);
		} else {
			gl.color(color.getRed(), color.getGreen(), color.getBlue(),
			        color.getAlpha());
		}

		gl.drawTrianglesWithTexture(texture.getTextureIndex(gl), geometry);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private static final float IMAGE_DRAW_OFFSET = .5f;

	private static float[] createGeometry(int offsetX, int offsetY, int width,
	        int height, float umin, float vmin, float umax, float vmax) {
		return new float[] {
		        // top left
		        -offsetX + IMAGE_DRAW_OFFSET,
		        -offsetY + height + IMAGE_DRAW_OFFSET,
		        0,
		        umin,
		        vmin,

		        // bottom left
		        -offsetX + IMAGE_DRAW_OFFSET,
		        -offsetY + IMAGE_DRAW_OFFSET,
		        0,
		        umin,
		        vmax,

		        // bottom right
		        -offsetX + width + IMAGE_DRAW_OFFSET,
		        -offsetY + IMAGE_DRAW_OFFSET,
		        0,
		        umax,
		        vmax,

		        // top right
		        -offsetX + width + IMAGE_DRAW_OFFSET,
		        -offsetY + height + IMAGE_DRAW_OFFSET,
		        0,
		        umax,
		        vmin,
		        // top left
		        -offsetX + IMAGE_DRAW_OFFSET,
		        -offsetY + height + IMAGE_DRAW_OFFSET,
		        0,
		        umin,
		        vmin,
		        // bottom right
		        -offsetX + width + IMAGE_DRAW_OFFSET,
		        -offsetY + IMAGE_DRAW_OFFSET,
		        0,
		        umax,
		        vmax,

		};
	}

	@Override
	public void drawImageAtRect(GLDrawContext gl, float minX, float minY,
	        float maxX, float maxY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAt(GLDrawContext gl, DrawBuffer buffer, float viewX, float viewY, int iColor) {
		buffer.addImage(texture.getTextureIndex(gl), viewX - offsetX, viewY - offsetY, viewX - offsetX + width, viewY - offsetY + height, umin, vmin, umax, vmax, iColor);
	}

}
