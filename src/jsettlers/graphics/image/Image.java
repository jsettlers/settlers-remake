package jsettlers.graphics.image;

import go.graphics.Color;
import go.graphics.GLDrawContext;

import java.nio.ShortBuffer;

/**
 * This is the base for all images.
 * <p>
 * This class interprets the image data in 5-5-5-1-Format. To change the
 * interpretation, it is possible to subclass this class.
 * 
 * @author michael
 */
public class Image implements ImageDataPrivider {

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
	protected Image(ShortBuffer data, int width, int height, int offsetX,
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
	protected Image(ImageDataPrivider provider) {
		this.data = provider.getData();
		this.width = provider.getWidth();
		this.height = provider.getHeight();
		this.offsetX = provider.getOffsetX();
		this.offsetY = provider.getOffsetY();
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
	 * Generates a texture if no texture was generated yet.
	 * 
	 * @param gl
	 *            The gl context
	 */
	// protected void tryGenerateTexture(GL2 gl) {
	// if (this.texture == 0) {
	// generateTexture(gl);
	// }
	// }

	/**
	 * Generates the texture. If there is already a texture, the old one is
	 * deleted first.
	 * 
	 * @param gl
	 *            The gl context
	 * @return The success status
	 * @see #tryGenerateTexture(GL2)
	 */
	// protected boolean generateTexture(GL2 gl) {
	// if (this.texture != 0) {
	// gl.glDeleteTextures(1, new int[] {
	// this.texture
	// }, 0);
	// }
	//
	// // 1 byte aligned.
	// gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
	//
	// int[] textureIndexes = new int[1];
	// gl.glGenTextures(1, textureIndexes, 0);
	// this.texture = textureIndexes[0];
	// if (this.texture == 0) {
	// // FIXME: Error?
	// return false;
	// }
	//
	// gl.glBindTexture(GL.GL_TEXTURE_2D, this.texture);
	//
	// if (textureWidth == 0) {
	// textureWidth = TextureCalculator.supportedTextureSize(gl, width);
	// textureHeight = TextureCalculator.supportedTextureSize(gl, height);
	// if (textureHeight != height || textureWidth != width) {
	// adaptDataToTextureSize();
	// }
	// }
	//
	// this.data.rewind();
	// texImage2D(gl);
	//
	// setTextureParameters(gl);
	//
	// return true;
	// }

	// private void generateVBOs(GL2 gl) {
	// gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	// if (elementBufferId == 0) {
	// // two triangles
	// byte[] elements = new byte[] {
	// 0, 1, 3, 1, 3, 2
	// };
	// ByteBuffer buffer = ByteBuffer.wrap(elements);
	//
	// int[] elementBufferIds = new int[] {
	// 0
	// };
	// gl.glGenBuffers(0, elementBufferIds, 0);
	// elementBufferId = elementBufferIds[0];
	//
	// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, elementBufferId);
	// gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, 6, buffer,
	// GL.GL_STATIC_DRAW);
	// }
	//
	// if (vertexBufferId != 0) {
	// gl.glDeleteBuffers(1, new int[] {
	// vertexBufferId
	// }, 0);
	// }
	// int[] vertexBuffIds = new int[] {
	// 0
	// };
	// gl.glGenBuffers(1, vertexBuffIds, 0);
	// vertexBufferId = vertexBuffIds[0];
	//
	// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufferId);
	// gl.glBufferData(GL.GL_ARRAY_BUFFER, 5 * 4 * Buffers.SIZEOF_FLOAT, null,
	// GL.GL_DYNAMIC_DRAW);
	// ByteBuffer buffer =
	// gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY);
	// // FloatBuffer buffer = FloatBuffer.allocate(5 * 4);
	// loadBuffer(buffer);
	// gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
	//
	// // buffer.rewind();
	// // gl.glBufferData(GL.GL_ARRAY_BUFFER, 5 * 4 * Buffers.SIZEOF_FLOAT,
	// // buffer, GL.GL_STATIC_DRAW);
	// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
	// gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	// }

	// private void loadBuffer(ByteBuffer buffer) {
	// int left = getOffsetX();
	// int top = -getOffsetY();
	// // bottom left
	// buffer.putFloat(0);
	// buffer.putFloat(0);
	// buffer.putFloat(left);
	// buffer.putFloat(top - this.height);
	// buffer.putFloat(0);
	// // top left
	// buffer.putFloat((float) width / textureWidth);
	// buffer.putFloat(0);
	// buffer.putFloat(left + this.width);
	// buffer.putFloat(top - this.height);
	// buffer.putFloat(0);
	// // top right
	// buffer.putFloat((float) width / textureWidth);
	// buffer.putFloat((float) height / textureHeight);
	// buffer.putFloat(left + this.width);
	// buffer.putFloat(top);
	// buffer.putFloat(0);
	// // bottom right
	// buffer.putFloat(0);
	// buffer.putFloat((float) height / textureHeight);
	// buffer.putFloat(left);
	// buffer.putFloat(top);
	// buffer.putFloat(0);
	// }

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

	public void drawImageAtRect(GLDrawContext gl, float minX, float minY, float maxX,
	        float maxY) {
		float[] coords =
		        new float[] {
		                minX,
		                minY,
		                0,
		                0,
		                0,
		                minX,
		                maxY,
		                0,
		                0,
		                (float) height / textureHeight,
		                maxX,
		                maxY,
		                0,
		                (float) width / textureWidth,
		                (float) height / textureHeight,
		                maxX,
		                minY,
		                0,
		                (float) width / textureWidth,
		                0,
		        };
		gl.color(1, 1, 1, 1);
		gl.drawQuadWithTexture(getTextureIndex(gl), coords);
	}

	/**
	 * Convenience method, calls drawAt(gl, x, y, -1);
	 * 
	 * @param gl
	 *            The context.
	 * @param x
	 *            The x position of the center.
	 * @param y
	 *            The y position of the center
	 */
	public void drawAt(GLDrawContext gl, int x, int y) {
		drawAt(gl, x, y, null);
	}

	/**
	 * Draws an object for a given player. The player -1 means no player.
	 * 
	 * @param gl
	 *            The gl context.
	 * @param x
	 *            The x coordinate on the screen.
	 * @param y
	 *            The y coordinate on the screen.
	 * @param color
	 *            The player number.
	 */
	public void drawAt(GLDrawContext gl, int x, int y, Color color) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		draw(gl, color);
		gl.glPopMatrix();
	}

	@Override
	public ShortBuffer getData() {
		return this.data;
	}

	/**
	 * Draws the image around the center of the given gl context.
	 * 
	 * @param gl
	 *            The gl context
	 */
	public void draw(GLDrawContext gl) {
		draw(gl, null);
	}

	/**
	 * Draws the image around 0,0 with the given color.
	 * 
	 * @param gl
	 *            The gl context
	 * @param color
	 *            The color to use. If it is <code>null</code>, white is used.
	 */
	public void draw(GLDrawContext gl, Color color) {
		if (color == null) {
			gl.color(1, 1, 1, 1);
		} else {
			gl.color(color);
		}

		gl.drawQuadWithTexture(getTextureIndex(gl), getGeometryIndex(gl));
		// gl.glEnable(GL.GL_TEXTURE_2D);
		// bind(gl);
		//
		// if (vertexBufferId == 0) {
		// generateVBOs(gl);
		// }
		//
		// gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		// gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		//
		// if (gl.isExtensionAvailable("GL_ARB_vertex_buffer_object")) {
		// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufferId);
		// gl.glTexCoordPointer(2, GL.GL_FLOAT, 5 * 4, 0);
		// gl.glVertexPointer(3, GL.GL_FLOAT, 20, 8);
		// } else {
		// ByteBuffer buffer =
		// ByteBuffer.allocateDirect(5 * 4 * Buffers.SIZEOF_FLOAT);
		// loadBuffer(buffer);
		// buffer.position(0);
		// gl.glTexCoordPointer(2, GL.GL_FLOAT, 5 * 4, buffer);
		// buffer.position(8);
		// gl.glVertexPointer(3, GL.GL_FLOAT, 20, buffer);
		// }

		// gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
		// gl.glDisable(GL.GL_TEXTURE_2D);

		// gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		// gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

		return;
	}

	private float[] getGeometry() {
		int left = getOffsetX();
		int top = -getOffsetY();
		return new float[] {
		        // bottom left
		        left,
		        top - this.height,
		        0,
		        0,
		        0,
		        // top left
		        left + this.width,
		        top - this.height,
		        0,
		        (float) width / textureWidth,
		        0,
		        // top right
		        left + this.width,
		        top,
		        0,
		        (float) width / textureWidth,
		        (float) height / textureHeight,
		        // bottom right
		        left,
		        top,
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