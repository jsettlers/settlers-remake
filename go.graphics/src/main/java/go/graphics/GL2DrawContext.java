package go.graphics;

public interface GL2DrawContext extends GLDrawContext {
	void drawUnified2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, boolean image, boolean shadow, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) throws IllegalBufferException;
	void drawUnified2DArray(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, boolean image, boolean shadow, float[] x, float[] y, float[] z, AbstractColor[] color, float[] intensity, int count) throws IllegalBufferException;

	void setShadowDepthOffset(float depth);
}
