package go.graphics.android;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.opengl.GLES10;
import android.opengl.GLUtils;

public class AndroidContext implements GLDrawContext {
	
	private ArrayList<float[]> geometries = new ArrayList<float[]>();

	public AndroidContext() {
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

		GLES10.glAlphaFunc(GLES10.GL_GREATER, 0.1f);
		GLES10.glEnable(GLES10.GL_ALPHA_TEST);

		GLES10.glClear(GLES10.GL_DEPTH_BUFFER_BIT);
		GLES10.glDepthFunc(GLES10.GL_LEQUAL);
		GLES10.glEnable(GLES10.GL_DEPTH_TEST);

		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
		System.out.println("fillQuad");
		float[] quadData =
		        new float[] {
		                x1,
		                y1,
		                0,
		                x2,
		                y1,
		                0,
		                x1,
		                y2,
		                0,
		                x1,
		                y2,
		                0,
		                x2,
		                y1,
		                0,
		                x2,
		                y2,
		                0,
		        };

		FloatBuffer floatBuff = generateTemporaryFloatBuffer(quadData);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuff);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, quadData.length / 3);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

	}

	@Override
	public void glPushMatrix() {
		GLES10.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GLES10.glTranslatef(x, y, z);
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GLES10.glScalef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		GLES10.glPopMatrix();
	}

	@Override
	public void color(float red, float green, float blue, float alpha) {
		GLES10.glColor4f(red, green, blue, alpha);
	}

	@Override
	public void color(Color color) {
		color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	private FloatBuffer reuseableBuffer = null;
	private ByteBuffer quadEleementBuffer;

	private FloatBuffer generateTemporaryFloatBuffer(float[] points) {
		if (reuseableBuffer == null
		        || reuseableBuffer.position(0).capacity() < points.length) {

			if (reuseableBuffer != null) {
				System.out.println("reallocated! needed: " + points.length
				        + ", old:" + reuseableBuffer.capacity());

			}
			ByteBuffer quadPoints = ByteBuffer.allocateDirect(points.length * 4);
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
		System.out.println("drawLine");
		if (points.length % 3 != 0) {
			throw new IllegalArgumentException("Point array length needs to be multiple of 3.");
		}
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(points);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuff);
		GLES10.glDrawArrays(loop ? GLES10.GL_LINE_LOOP : GLES10.GL_LINE_STRIP,
		        0, points.length / 3);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void drawQuadWithTexture(int textureid, float[] geometry) {
		System.out.println("drawQuadWithTexture");
		if (quadEleementBuffer == null) {
			quadEleementBuffer = ByteBuffer.allocateDirect(6);
			quadEleementBuffer.put((byte) 0);
			quadEleementBuffer.put((byte) 1);
			quadEleementBuffer.put((byte) 3);
			quadEleementBuffer.put((byte) 3);
			quadEleementBuffer.put((byte) 1);
			quadEleementBuffer.put((byte) 2);
		}
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		FloatBuffer texbuffer = buffer.duplicate();
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, texbuffer);

		quadEleementBuffer.position(0);
		GLES10.glDrawElements(GLES10.GL_TRIANGLES, 6, GLES10.GL_UNSIGNED_BYTE,
		        quadEleementBuffer);

		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
	}

	@Override
	public void drawTrianglesWithTexture(int textureid, float[] geometry) {
		System.out.println("drawTrianglesWithTexture");
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, buffer);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 5);

		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
	}

	@Override
	public void drawTrianglesWithTextureColored(int textureid, float[] geometry) {
		System.out.println("drawTrianglesWithTextureColored");
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 8 * 4, buffer);
		buffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 8 * 4, buffer);
		buffer.position(5);
		GLES10.glColorPointer(3, GLES10.GL_FLOAT, 8 * 4, buffer);

		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 8);
		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);

		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
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
		System.out.println("Generating texture");
		//TESTING CODE!
		
		// ByteBuffer bb = ByteBuffer.allocateDirect(width * height * 4);
		// bb.order(ByteOrder.BIG_ENDIAN);
		// IntBuffer ib = bb.asIntBuffer();
		// for (int i = 0; i < width + height; i++) {
		// ib.put(0x88888888);
		// }
		// bb.position(0);
		//
		// 1 byte aligned.
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);

		int[] textureIndexes = new int[1];
		GLES10.glGenTextures(1, textureIndexes, 0);
		checkGLError();
		int texture = textureIndexes[0];
		if (texture == 0) {
			return -1;
		}

		Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(0xff888888);
		System.out.println("generated bitmap");

		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
		checkGLError();
		// GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width,
		// height, 0,
		// GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, bb);
		GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();

		checkGLError();
		setTextureParameters();
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,
		        GLES10.GL_REPEAT);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,
		        GLES10.GL_REPEAT);
		checkGLError();
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);

		System.out.println("Generated texture " + texture);
		return texture;
	}

	private void checkGLError() {
		int glGetError = GLES10.glGetError();
		if (glGetError > 0) {
			System.err.println("GL error: " + glGetError);
		}
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created and is bound.
	 */
	private void setTextureParameters() {
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,
		        GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,
		        GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
	}

	@Override
	public void updateTexture(int textureIndex, int left, int bottom, int width, int height, ShortBuffer data) {
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureIndex);
		GLES10.glTexSubImage2D(GLES10.GL_TEXTURE_2D, 0, left, bottom, width,
		        height, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_5_5_5_1, data);
	}

	@Override
	public void deleteTexture(int textureid) {
		GLES10.glDeleteTextures(1, new int[] {
			textureid
		}, 0);
	}

	@Override
	public void glMultMatrixf(float[] matrix, int offset) {
		GLES10.glMultMatrixf(matrix, offset);
	}

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return AndroidTextDrawer.getInstance(size);
	}

	@Override
	public void drawQuadWithTexture(int textureid, int geometryindex) {
		drawQuadWithTexture(textureid, geometries.get(geometryindex));
	}

	@Override
	public void drawTrianglesWithTexture(int textureid, int geometryindex) {
		drawTrianglesWithTexture(textureid, geometries.get(geometryindex));
	}

	@Override
	public void drawTrianglesWithTextureColored(int textureid, int geometryindex) {
		drawTrianglesWithTextureColored(textureid, geometries.get(geometryindex));
	}

	@Override
	public int storeGeometry(float[] geometry) {
		geometries.add(geometry);
		return geometries.size() - 1;
	}

	@Override
	public boolean isGeometryValid(int geometryindex) {
		return geometryindex >= 0 && geometryindex < geometries.size();
	}

	@Override
	public void removeGeometry(int geometryindex) {
		// TODO Auto-generated method stub

	}

}
