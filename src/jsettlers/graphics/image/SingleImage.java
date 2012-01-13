package jsettlers.graphics.image;

import go.graphics.Color;
import go.graphics.GLDrawContext;

import java.nio.ShortBuffer;

import jsettlers.graphics.reader.ImageMetadata;

/**
 * This is the base for all images.
 * <p>
 * This class interprets the image data in 5-5-5-1-Format. To change the
 * interpretation, it is possible to subclass this class.
 * 
 * @author michael
 */
public class SingleImage implements ImageDataPrivider, Image {

	protected ShortBuffer data;
	protected final int width;
	protected final int height;
	protected int textureWidth = 0;
	protected int textureHeight = 0;
	protected final int offsetX;
	protected final int offsetY;

	private int texture = -1;
	private int geometryindex = -1;

	/**
	 * Creates a new image by the given buffer.
	 * 
	 * @param data
	 *            The data buffer for the image with an unspecified color
	 *            format.
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 * @param offsetX
	 *            The x offset of the image.
	 * @param offsetY
	 *            The y offset of the image.
	 */
	protected SingleImage(ShortBuffer data, int width, int height, int offsetX,
	        int offsetY) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	/**
	 * Creates a new image by linking this images data to the data of the
	 * provider.
	 * 
	 * @param provider
	 *            The provider.
	 */
	protected SingleImage(ImageMetadata metadata, short[] data) {
		this.data = ShortBuffer.wrap(data);
		this.width = metadata.width;
		this.height = metadata.height;
		this.offsetX = metadata.offsetX;
		this.offsetY = metadata.offsetY;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getOffsetX() {
		return this.offsetX;
	}

	@Override
	public int getOffsetY() {
		return this.offsetY;
	}

	/**
	 * Converts the current data to match the pwer of two size.
	 */
	protected void adaptDataToTextureSize() {
		this.data.rewind();
		short[] newData = new short[textureHeight * textureWidth];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newData[y * textureWidth + x] = data.get(y * width + x);
			}
			for (int x = width; x < textureWidth; x++) {
				newData[y * textureWidth + x] =
				        newData[y * textureWidth + width - 1];
			}
		}
		for (int y = height; y < textureHeight; y++) {
			for (int x = 0; x < textureWidth; x++) {
				newData[y * textureWidth + x] =
				        newData[(height - 1) * textureWidth + x];
			}
		}
		data = ShortBuffer.wrap(newData);
	}

	/**
	 * Generates the texture, if needed, and returns the index of that texutre.
	 * 
	 * @return The gl index or 0 if the texture is not allocated.
	 */
	public int getTextureIndex(GLDrawContext gl) {
		if (texture < 0) {
			if (textureWidth == 0) {
				textureWidth = gl.makeWidthValid(width);
				textureHeight = gl.makeHeightValid(height);
				if (textureWidth != width || textureHeight != height) {
					adaptDataToTextureSize();
				}
				data.position(0);
			}
			texture =
			        gl.generateTexture(textureWidth, textureHeight, this.data);
		}
		return this.texture;
	}

	static private float[] tmpBuffer = new float[] {
	        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};

	public void drawImageAtRect(GLDrawContext gl, float left, float bottom,
	        float right, float top) {
		tmpBuffer[0] = left;
		tmpBuffer[1] = top;

		tmpBuffer[5] = left;
		tmpBuffer[6] = bottom;
		tmpBuffer[9] = (float) height / textureHeight;

		tmpBuffer[10] = right;
		tmpBuffer[11] = bottom;
		tmpBuffer[13] = (float) width / textureWidth;
		tmpBuffer[14] = (float) height / textureHeight;

		tmpBuffer[15] = right;
		tmpBuffer[16] = top;
		tmpBuffer[18] = (float) width / textureWidth;

		gl.drawQuadWithTexture(getTextureIndex(gl), tmpBuffer);
	}

	/*
	 * (non-Javadoc)
	 * @see jsettlers.graphics.image.Image#drawAt(go.graphics.GLDrawContext,
	 * float, float)
	 */
	@Override
	public void drawAt(GLDrawContext gl, float x, float y) {
		drawAt(gl, x, y, null);
	}

	/*
	 * (non-Javadoc)
	 * @see jsettlers.graphics.image.Image#drawAt(go.graphics.GLDrawContext,
	 * float, float, go.graphics.Color)
	 */
	@Override
	public void drawAt(GLDrawContext gl, float x, float y, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		draw(gl, color);
		gl.glPopMatrix();
	}

	@Override
	public ShortBuffer getData() {
		return this.data;
	}

	/*
	 * (non-Javadoc)
	 * @see jsettlers.graphics.image.Image#draw(go.graphics.GLDrawContext,
	 * go.graphics.Color)
	 */
	@Override
	public void draw(GLDrawContext gl, Color color) {
		if (color == null) {
			gl.color(1, 1, 1, 1);
		} else {
			gl.color(color);
		}

		gl.drawTrianglesWithTexture(getTextureIndex(gl), getGeometryIndex(gl),
		        2);

		// gl.drawTrianglesWithTexture(getTextureIndex(gl), getGeometry());
		return;
	}

	@Override
	public void draw(GLDrawContext gl, Color color, float multiply) {
		if (color == null) {
			gl.color(multiply, multiply, multiply, 1);
		} else {
			gl.color(color.getRed() * multiply, color.getGreen() * multiply,
			        color.getBlue() * multiply, color.getAlpha());
		}

		gl.drawTrianglesWithTexture(getTextureIndex(gl), getGeometryIndex(gl),
		        2);
		return;
	}

	private float[] getGeometry() {
		int left = getOffsetX();
		int top = -getOffsetY();
		return new float[] {
		        // top right
		        left + this.width,
		        top,
		        0,
		        (float) width / textureWidth,
		        0,
		        // bottom right
		        left,
		        top,
		        0,
		        0,
		        0,
		        // top left
		        left + this.width,
		        top - this.height,
		        0,
		        (float) width / textureWidth,
		        (float) height / textureHeight,

		        // top left
		        left + this.width,
		        top - this.height,
		        0,
		        (float) width / textureWidth,
		        (float) height / textureHeight,
		        // bottom right
		        left,
		        top,
		        0,
		        0,
		        0,
		        // bottom left
		        left,
		        top - this.height,
		        0,
		        0,
		        (float) height / textureHeight,
		};
	}

	protected int setGeometryIndex(int geometryindex) {
		return geometryindex;
	}

	protected int getGeometryIndex(GLDrawContext context) {
		if (!context.isGeometryValid(geometryindex)) {
			geometryindex = context.storeGeometry(getGeometry());
		}
		return geometryindex;
	}

	public float getTextureScaleX() {
		return (float) width / textureWidth;
	}

	public float getTextureScaleY() {
		return (float) height / textureHeight;
	}
}