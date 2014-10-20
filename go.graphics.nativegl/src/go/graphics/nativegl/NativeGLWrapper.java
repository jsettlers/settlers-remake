package go.graphics.nativegl;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

class NativeGLWrapper implements GLDrawContext, GLDrawContext.GLBuffer {

	@Override
	public native void fillQuad(float x1, float y1, float x2, float y2);

	@Override
	public native void drawLine(float[] points, boolean loop);

	@Override
	public native void glPushMatrix();

	@Override
	public native void glTranslatef(float x, float y, float z);

	@Override
	public native void glScalef(float x, float y, float z);

	@Override
	public native void glPopMatrix();

	@Override
	public native void color(float red, float green, float blue, float alpha);

	@Override
	public native int generateTexture(int width, int height, ShortBuffer data);

	@Override
	public native void deleteTexture(int textureid);

	@Override
	public native void drawQuadWithTexture(int textureid, float[] geometry);

	@Override
	public void drawQuadWithTexture(int textureid, int geometryindex) {
		// TODO Auto-generated method stub
		// UNUSED
	}

	@Override
	public native void drawTrianglesWithTexture(int textureid, float[] geometry);

	@Override
	public native void drawTrianglesWithTexture(int textureid,
	        int geometryindex, int triangleCount);

	@Override
	public void drawTrianglesWithTextureColored(int textureid, float[] geometry) {
		// TODO Auto-generated method stub
		// UNUSED
	}

	@Override
	public native void drawTrianglesWithTextureColored(int textureid,
	        int geometryindex, int triangleCount);

	@Override
	public native int makeWidthValid(int width);

	@Override
	public native int makeHeightValid(int height);

	@Override
	public native void glMultMatrixf(float[] matrix, int offset);

	@Override
	public native void updateTexture(int textureIndex, int left, int bottom,
	        int width, int height, ShortBuffer data);

	@Override
	public TextDrawer getTextDrawer(EFontSize size) {
		return new NativeTextDrawer(size);
	}

	@Override
	public int storeGeometry(float[] geometry) {
		int vertexBufferId =
		        generateGeometry(geometry.length * 4);
		if (vertexBufferId < 0) {
			return -1;
		}

		GLBuffer buffer = startWriteGeometry(vertexBufferId);
		for (int i = 0; i < geometry.length; i++) {
			buffer.putFloat(geometry[i]);
		}
		endWriteGeometry(vertexBufferId);

		return vertexBufferId;
	}

	@Override
	public native boolean isGeometryValid(int geometryindex);

	@Override
	public native void removeGeometry(int geometryindex);

	@Override
	public native GLBuffer startWriteGeometry(int geometryindex);

	@Override
	public native void endWriteGeometry(int geometryindex);

	@Override
	public native int generateGeometry(int bytes);

	@Override
	public native void drawTrianglesWithTextureColored(int currentTexture,
	        ByteBuffer byteBuffer, int currentTriangles);

	@Override
    public native void putFloat(float f);

	@Override
    public native void putByte(byte b);

	@Override
    public native void position(int position);

}
