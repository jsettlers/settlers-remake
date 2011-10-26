package go.graphics.android;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class AndroidContext implements GLDrawContext {

	private final GL10 gl;

	
	public AndroidContext(GL10 gl) {
		this.gl = gl;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		gl.glEnable(GL10.GL_ALPHA_TEST);

		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
		float[] quadData = new float[] {
				x1, y1, 0,
				x2, y1, 0,
				x1, y2, 0,
				x1, y2, 0,
				x2, y1, 0,
				x2, y2, 0,
		};
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(quadData);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0,
				quadData.length / 3);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void glPushMatrix() {
		gl.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		gl.glTranslatef(x, y, z);
	}

	@Override
	public void glScalef(float x, float y, float z) {
		gl.glScalef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		gl.glPopMatrix();
	}

	@Override
	public void color(float red, float green, float blue, float alpha) {
		gl.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void color(Color color) {
		color(color.getRed(), color.getGreen(), color.getBlue(),
		        color.getAlpha());
	}

	private FloatBuffer reuseableBuffer = null;
	private ByteBuffer quadEleementBuffer;

	private FloatBuffer generateTemporaryFloatBuffer(float[] points) {
		if (reuseableBuffer == null
		        || reuseableBuffer.capacity() < points.length) {
			if (reuseableBuffer != null) {
				System.out.println("reallocated! needed: " + points.length
				        + ", got:" + reuseableBuffer.capacity());
			}
			ByteBuffer quadPoints =
			        ByteBuffer.allocateDirect(points.length * 4);
			quadPoints.order(ByteOrder.nativeOrder());
			reuseableBuffer = quadPoints.asFloatBuffer();
		} else {
			reuseableBuffer.position(0);
		}
		reuseableBuffer.put(points);
		reuseableBuffer.position(0);
		return reuseableBuffer;
	}
	
	@Override
	public void drawLine(float[] points, boolean loop) {
		if (points.length % 3 != 0) {
			throw new IllegalArgumentException(
			        "Point array length needs to be multiple of 3.");
		}
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(points);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);
		gl.glDrawArrays(loop ? GL10.GL_LINE_LOOP : GL10.GL_LINE_STRIP, 0,
		        points.length / 3);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	@Override
	public void drawQuadWithTexture(int textureid, float[] geometry) {
		if (quadEleementBuffer == null) {
			quadEleementBuffer = ByteBuffer.allocateDirect(6);
			quadEleementBuffer.put((byte) 0);
			quadEleementBuffer.put((byte) 1);
			quadEleementBuffer.put((byte) 3);
			quadEleementBuffer.put((byte) 3);
			quadEleementBuffer.put((byte) 1);
			quadEleementBuffer.put((byte) 2);
		}
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 5 * 4, buffer);
		buffer.position(0);
		
		quadEleementBuffer.position(0);
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, quadEleementBuffer);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}

	@Override
	public void drawTrianglesWithTexture(int textureid, float[] geometry) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 5 * 4, buffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, geometry.length / 5);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}

	@Override
	public void drawTrianglesWithTextureColored(int textureid, float[] geometry) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 8 * 4, buffer);
		buffer.position(3);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 8 * 4, buffer);
		buffer.position(5);
		gl.glColorPointer(3, GL10.GL_FLOAT, 8 * 4, buffer);

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, geometry.length / 8);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}


	private int getPowerOfTwo(int value) {
		int guess = 1;
		while (guess < value) {
			guess *= 2;
		}
		return guess;
	}
	
	@Override
	public int makeWidthValid(int width) {
		return getPowerOfTwo(width);
	}

	@Override
	public int makeHeightValid(int height) {
		return getPowerOfTwo(height);
	}

	@Override
	public int generateTexture(int width, int height, ShortBuffer data) {
		// 1 byte aligned.
		gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);

		int[] textureIndexes = new int[1];
		gl.glGenTextures(1, textureIndexes, 0);
		int texture = textureIndexes[0];
		if (texture == 0) {
			return -1;
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,
				GL10.GL_RGBA, GL10.GL_UNSIGNED_SHORT_5_5_5_1, data);
		setTextureParameters();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

		return texture;
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created
	 * and is bound.
	 */
	private void setTextureParameters() {
	}

	@Override
	public void updateTexture(int textureIndex, int left, int bottom,
	        int width, int height, ShortBuffer data) {

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIndex);
		gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, left, bottom, width, height,
		        GL10.GL_RGBA, GL10.GL_UNSIGNED_SHORT_5_5_5_1, data);
	}

	@Override
	public void deleteTexture(int textureid) {
		gl.glDeleteTextures(1, new int[] {
			textureid
		}, 0);
	}
	@Override
	public void glMultMatrixf(float[] matrix, int offset) {
		gl.glMultMatrixf(matrix, offset);
	}

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return AndroidTextDrawer.getInstance(size);
	}

}
