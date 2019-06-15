package go.graphics;

public interface GL2DrawContext extends GLDrawContext {
	void drawUnified2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, boolean image, boolean shadow, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) throws IllegalBufferException;

	void setShadowDepthOffset(float depth);
}
