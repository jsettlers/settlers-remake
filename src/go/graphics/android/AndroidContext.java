package go.graphics.android;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES10;
import android.opengl.GLES11;

public class AndroidContext implements GLDrawContext {

	public AndroidContext() {
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
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
		
		glBindTexture(0);
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(quadData);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 3 * 4, floatBuff);
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
		color(color.getRed(), color.getGreen(), color.getBlue(),
		        color.getAlpha());
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
		glBindTexture(0);
		FloatBuffer floatBuff = generateTemporaryFloatBuffer(points);
		GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, floatBuff);
		GLES10.glDrawArrays(loop ? GLES10.GL_LINE_LOOP : GLES10.GL_LINE_STRIP,
		        0, points.length / 3);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
	}

	private int lastTexture = 0;
	private void glBindTexture(int texture) {
	    if (texture != lastTexture) {
	    	GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
	    	lastTexture = texture;
	    }
    }

	@Override
	public void drawQuadWithTexture(int textureid, float[] geometry) {
		if (quadEleementBuffer == null) {
			generateQuadElementBuffer();
		}
		quadEleementBuffer.position(0);
		glBindTexture(textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		FloatBuffer texbuffer = buffer.duplicate();
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, texbuffer);

		GLES10.glDrawElements(GLES10.GL_TRIANGLES, 6, GLES10.GL_UNSIGNED_BYTE,
		        quadEleementBuffer);
	}

	private void generateQuadElementBuffer() {
	    quadEleementBuffer = ByteBuffer.allocateDirect(6);
	    quadEleementBuffer.put((byte) 0);
	    quadEleementBuffer.put((byte) 1);
	    quadEleementBuffer.put((byte) 3);
	    quadEleementBuffer.put((byte) 3);
	    quadEleementBuffer.put((byte) 1);
	    quadEleementBuffer.put((byte) 2);
    }

	@Override
	public void drawTrianglesWithTexture(int textureid, float[] geometry) {
		glBindTexture(textureid);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, buffer);
		FloatBuffer texbuffer = buffer.duplicate();
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, texbuffer);
		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 5);
	}

	@Override
	public void drawTrianglesWithTextureColored(int textureid, float[] geometry) {
		glBindTexture(textureid);

		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);

		FloatBuffer buffer = generateTemporaryFloatBuffer(geometry);

		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 9 * 4, buffer);
		FloatBuffer texbuffer = buffer.duplicate();
		texbuffer.position(3);
		GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 9 * 4, texbuffer);
		FloatBuffer colorbuffer = buffer.duplicate();
		colorbuffer.position(5);
		GLES10.glColorPointer(4, GLES10.GL_FLOAT, 9 * 4, colorbuffer);

		GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, geometry.length / 9);
		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
	}

	private static int getPowerOfTwo(int value) {
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
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);

		int[] textureIndexes = new int[1];
		GLES10.glGenTextures(1, textureIndexes, 0);
		int texture = textureIndexes[0];
		if (texture == 0) {
			return -1;
		}

		glBindTexture(texture);
		GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, width,
		        height, 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_SHORT_5_5_5_1,
		        data);

		setTextureParameters();
		return texture;
	}

	/**
	 * Sets the texture parameters, assuming that the texture was just created
	 * and is bound.
	 */
	private void setTextureParameters() {
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,
		        GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,
		        GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,
		        GLES10.GL_REPEAT);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,
		        GLES10.GL_REPEAT);
	}

	@Override
	public void updateTexture(int textureIndex, int left, int bottom,
	        int width, int height, ShortBuffer data) {
		glBindTexture(textureIndex);
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
		if (quadEleementBuffer == null) {
			generateQuadElementBuffer();
		}
		quadEleementBuffer.position(0);
		
		glBindTexture(textureid);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex);
		GLES11.glVertexPointer(3, GLES10.GL_FLOAT, 5 * 4, 0);
		GLES11.glTexCoordPointer(2, GLES10.GL_FLOAT, 5 * 4, 3 * 4);

		GLES11.glDrawElements(GLES10.GL_TRIANGLES, 6, GLES10.GL_UNSIGNED_BYTE,
		        quadEleementBuffer);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public boolean isGeometryValid(int geometryindex) {
		return geometryindex > 0 && GLES11.glIsBuffer(geometryindex);
	}

	@Override
	public void removeGeometry(int geometryindex) {
		GLES11.glDeleteBuffers(1, new int[] {
			geometryindex
		}, 0);
	}

	public void reinit(int width, int height) {
		GLES10.glMatrixMode(GLES10.GL_PROJECTION);
		GLES10.glLoadIdentity();
		GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
		GLES10.glLoadIdentity();

		GLES10.glScalef(2f / width, 2f / height, 0);
		//TODO: do not scale depth by 0.

		GLES10.glTranslatef(-width / 2, -height / 2, 0);

		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

		GLES10.glAlphaFunc(GLES10.GL_GREATER, 0.1f);
		GLES10.glEnable(GLES10.GL_ALPHA_TEST);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

		GLES10.glDepthFunc(GLES10.GL_LEQUAL);
		GLES10.glEnable(GLES10.GL_DEPTH_TEST);

		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
	}

	@Override
	public void drawTrianglesWithTexture(int textureid, int geometryindex,
	        int triangleCount) {
//TODO
	}

	@Override
    public int generateGeometry(int bytes) {
			int[] vertexBuffIds = new int[] {
				0
			};
			GLES11.glGenBuffers(1, vertexBuffIds, 0);
			
			int vertexBufferId = vertexBuffIds[0];
			if (vertexBufferId == 0) {
				return -1;
			}
			
			GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vertexBufferId);
			GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, bytes, null, GLES11.GL_DYNAMIC_DRAW);
			GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
			return vertexBufferId;
    }
	
	@Override
	public void drawTrianglesWithTextureColored(int textureid,
	        int geometryindex, int triangleCount) {
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, textureid);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 6 * 4, 0);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 6 * 4, 3 * 4);
		GLES11.glColorPointer(4, GLES11.GL_UNSIGNED_BYTE, 6 * 4, 5 * 4);

		GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, triangleCount * 3);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);

		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public int storeGeometry(float[] geometry) {
		int bytes = 4 * geometry.length;
//		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
//		for (int i = 0; i < geometry.length; i++) {
//			buffer.putFloat(geometry[i]);
//		}
//		buffer.rewind();

//		int[] vertexBuffIds = new int[] {
//			0
//		};
//		GLES11.glGenBuffers(1, vertexBuffIds, 0);
//		
//		int vertexBufferId = vertexBuffIds[0];
//		if (vertexBufferId == 0) {
//			return -1;
//		}
//		
//		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, vertexBufferId);
//		GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, bytes, null, GLES11.GL_DYNAMIC_DRAW);
//		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
		
		int vertexBufferId = generateGeometry(bytes);
		
		GLBuffer buffer = startWriteGeometry(vertexBufferId);
		for (int i = 0; i < geometry.length; i++) {
			buffer.putFloat(geometry[i]);
		}
		endWriteGeometry(vertexBufferId);

		return vertexBufferId;
	}

	private GraphicsByteBuffer currentBuffer = null;

	@Override
	public GLBuffer startWriteGeometry(int geometryindex) {
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, geometryindex);
		currentBuffer = new GraphicsByteBuffer();
		return currentBuffer;
	}

	@Override
	public void endWriteGeometry(int geometryindex) {
		if (currentBuffer != null) {
			currentBuffer.writeBuffer();
			currentBuffer.position(0);
		}
		GLES11.glBindBuffer(GLES11.GL_ARRAY_BUFFER, 0);
	}

	public static class GraphicsByteBuffer implements GLDrawContext.GLBuffer {
		private static int BUFFER_LENGTH = 1024;
		private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_LENGTH).order(ByteOrder.nativeOrder());
		private static int bufferstart = 0;
		private static int bufferlength = 0;

		private void assertBufferHas(int remaining) {
			if (bufferlength + remaining > BUFFER_LENGTH) {
				writeBuffer();
				bufferstart += bufferlength;
				bufferlength = 0;
				buffer.position(0);
			}
		}
		
		@Override
		public void putFloat(float f) {
			assertBufferHas(4);
			buffer.putFloat(f);
			bufferlength+=4;
		}

		@Override
		public void putByte(byte b) {
			assertBufferHas(1);
			buffer.put(b);
			bufferlength++;
		}

		@Override
		public void position(int position) {
			if (bufferstart + bufferlength != position) {
				if (bufferlength > 0) {
					writeBuffer();
				}
				bufferstart = position;
				bufferlength = 0;
				buffer.position(0);
			}
		}

		private void writeBuffer() {
			buffer.position(0);
	        GLES11.glBufferSubData(GLES11.GL_ARRAY_BUFFER, bufferstart, bufferlength, buffer);
        }
	}
}
