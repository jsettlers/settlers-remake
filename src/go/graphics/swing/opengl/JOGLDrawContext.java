package go.graphics.swing.opengl;

import go.graphics.GLDrawContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class JOGLDrawContext implements GLDrawContext {

	private final GL2 gl2;

	public JOGLDrawContext(GL2 gl2) {
		this.gl2 = gl2;
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        
		gl2.glAlphaFunc(GL2.GL_GREATER, 0.1f);
		gl2.glEnable(GL2.GL_ALPHA_TEST);
		
		gl2.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		gl2.glDepthFunc(GL2.GL_LEQUAL);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		
		gl2.glEnable(GL2.GL_TEXTURE_2D);
	}

	@Override
	public void color(float r, float g, float b, float a) {
		gl2.glColor4f(r, g, b, a);
	}

	@Override
	public void fillQuad(float x1, float y1, float x2, float y2) {
		ByteBuffer quadPoints = ByteBuffer.allocateDirect(4 * 2 * 4);
		quadPoints.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuff = quadPoints.asFloatBuffer();
		floatBuff.put(x1);
		floatBuff.put(y1);
		
		floatBuff.put(x1);
		floatBuff.put(y2);
		
		floatBuff.put(x2);
		floatBuff.put(y2);
		
		floatBuff.put(x2);
		floatBuff.put(y1);

		floatBuff.position(0);

        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, floatBuff);
        gl2.glDrawArrays(GL2.GL_QUADS, 0, 4);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void drawLine(float[] points, boolean loop) {
		if (points.length % 3 != 0) {
			throw new IllegalArgumentException("Point array length needs to be multiple of 3.");
		}
		FloatBuffer floatBuff = generateFloatBuffer(points);
        gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, floatBuff);
        gl2.glDrawArrays(loop ? GL2.GL_LINE_LOOP : GL2.GL_LINE_STRIP, 0, points.length / 3);
        gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	private FloatBuffer generateFloatBuffer(float[] points) {
	    ByteBuffer quadPoints = ByteBuffer.allocateDirect(points.length * 4);
		quadPoints.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuff = quadPoints.asFloatBuffer();
		floatBuff.put(points);
		floatBuff.position(0);
	    return floatBuff;
    }

	@Override
	public void glPushMatrix() {
		gl2.glPushMatrix();
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		gl2.glTranslatef(x, y, z);
	}

	@Override
	public void glPopMatrix() {
		gl2.glPopMatrix();
	}

	@Deprecated
	public GL2 getGl2() {
	    return gl2;
    }

	@Override
    public void glScalef(float x, float y, float z) {
	    gl2.glScalef(x, y, z);
    }

	@Override
    public void color(float[] rgbComponents) {
	    gl2.glColor4fv(rgbComponents, 0);
    }

	@Override
    public int generateTexture(int width, int height, ShortBuffer data) {
		// 1 byte aligned.
		gl2.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		int[] textureIndexes = new int[1];
		gl2.glGenTextures(1, textureIndexes, 0);
		int texture = textureIndexes[0];
		if (texture == 0) {
			return -1;
		}

		gl2.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl2.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB5_A1, width,
		        height, 0, GL.GL_RGBA,
		        GL.GL_UNSIGNED_SHORT_5_5_5_1, data);
		setTextureParameters();
		gl2.glBindTexture(GL.GL_TEXTURE_2D, 0);

		return texture;
    }

	/**
	 * Sets the textures parameters, assuming that the texture was just created
	 * and is bound.
	 */
	private void setTextureParameters() {
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		        GL.GL_LINEAR);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		        GL.GL_LINEAR);
    }

	@Override
    public void deleteTexture(int textureid) {
	    gl2.glDeleteTextures(GL.GL_TEXTURE_2D, new int[] {textureid}, 0);
    }

	@Override
    public void drawQuadsWithTexture(int textureid, float[] geometry) {
		gl2.glBindTexture(GL.GL_TEXTURE_2D, textureid);
		
		FloatBuffer buffer = generateFloatBuffer(geometry);
				
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3);
		gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, buffer);
        gl2.glDrawArrays(GL2.GL_QUADS, 0, geometry.length / 5);
		
		gl2.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

	@Override
    public void drawTrianglesWithTexture(int textureid, float[] geometry) {
		gl2.glBindTexture(GL.GL_TEXTURE_2D, textureid);
		
		FloatBuffer buffer = generateFloatBuffer(geometry);
				
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 5 * 4, buffer);
		buffer.position(3);
		gl2.glTexCoordPointer(2, GL2.GL_FLOAT, 5 * 4, buffer);
        gl2.glDrawArrays(GL2.GL_QUADS, 0, geometry.length / 5);
		
		gl2.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

	@Override
    public int makeWidthValid(int width) {
		return TextureCalculator.supportedTextureSize(gl2, width);
    }

	@Override
    public int makeHeightValid(int height) {
		return TextureCalculator.supportedTextureSize(gl2, height);
    }

	@Override
    public void glMultMatrixf(float[] matrix, int offset) {
	    gl2.glMultMatrixf(matrix, offset);
    }
}
