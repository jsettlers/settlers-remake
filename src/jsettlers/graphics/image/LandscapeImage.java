package jsettlers.graphics.image;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jsettlers.common.map.IHexTile;

/**
 * Class to draw triangles of landscape images.
 * <p>
 * There are 2 types of images: <br>
 * Some images are big and continuous, so they are just drawn and wrapped at the
 * end. <br>
 * Other images only consist of 6 triangles indicating a border between two
 * terrian types in all 6 directions.
 * <p>
 * You have to do the drawing yourself, but there are helper functions that help
 * you:
 * <p>
 * bind() activates drawing the texture.
 * 
 * @author michael
 */
public class LandscapeImage extends Image {
	/**
	 * States that you request the image in the top right border of the texture.
	 */
	public static final int TRI_TOPRIGHT = 0;
	public static final int TRI_BOTTOMRIGHT = 1;
	public static final int TRI_BOTTOM = 2;
	public static final int TRI_BOTTOMLEFT = 3;
	public static final int TRI_TOP = 4;
	public static final int TRI_TOPLEFT = 5;

	public LandscapeImage(ImageDataPrivider data) {
		super(data);
	}

	/**
	 * Draws the triangle at a given Position.
	 * 
	 * @param gl
	 *            The gl context
	 * @param x
	 *            the x coordinate of the left corner.
	 * @param y
	 *            the y coordinate of the left corner.
	 * @param triangleNumber
	 *            a Number indicating the triangle to draw (for hex images) and
	 *            the direction.
	 */
	public void drawAt(GL2 gl, int x, int y, int triangleNumber) {
		drawTexture(gl, x, y, triangleNumber);
	}

	/**
	 * Checks whether the given triangle number is a triangle that points down.
	 * 
	 * @param triangeNumber
	 *            the number of the triangle
	 * @return
	 */
	private boolean isDownTriangle(int triangeNumber) {
		return triangeNumber % 2 == 1;
	}

	private void drawTexture(GL2 gl, int x, int y, int triangeNumber) {
		bind(gl);

		int textureOffsetX;
		int textureOffsetY;
		if (this.width > 100 && this.height > 100) {
			// continuous...
			textureOffsetX = x;
			textureOffsetY = y;
		} else if (triangeNumber == TRI_TOPLEFT
		        || triangeNumber == TRI_BOTTOMLEFT) {
			textureOffsetX = IHexTile.X_DISTANCE;
			textureOffsetY = IHexTile.Y_DISTANCE;
		} else if (triangeNumber == TRI_TOP) {
			textureOffsetX = IHexTile.X_DISTANCE / 2;
			textureOffsetY = 0;
		} else if (triangeNumber == TRI_BOTTOM) {
			textureOffsetX = IHexTile.X_DISTANCE / 2;
			textureOffsetY = IHexTile.Y_DISTANCE * 2;
		} else {
			textureOffsetX = 0;
			textureOffsetY = IHexTile.Y_DISTANCE;
		}

		gl.glVertex2i(x, y);

		gl.glColor3f(1, 1, 1);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f((float) textureOffsetX / this.width,
		        (float) textureOffsetY / this.height);
		gl.glVertex2i(x, y);

		gl.glTexCoord2f((float) (textureOffsetX + IHexTile.X_DISTANCE)
		        / this.width, (float) textureOffsetY / this.height);
		gl.glVertex2i(x + IHexTile.X_DISTANCE, y);

		int downFactor = isDownTriangle(triangeNumber) ? -1 : 1;
		gl.glTexCoord2f((float) (x + IHexTile.X_DISTANCE / 2) / this.width,
		        (float) (y + downFactor * IHexTile.Y_DISTANCE) / this.height);
		gl.glVertex2i(x + IHexTile.X_DISTANCE / 2, y + downFactor
		        * IHexTile.Y_DISTANCE);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Checks whether the given image is a continous image, that means it can be
	 * repeated when drawing.
	 * 
	 * @return If the image is continuous.
	 */
	public boolean isContinuous() {
		return this.width > 50 && this.height > 50;
	}

	@Override
	protected void setTextureParameters(GL2 gl) {
		gl
		        .glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
		                GL.GL_REPEAT);
		gl
		        .glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
		                GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		        GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		        GL.GL_NEAREST);
	}

}
