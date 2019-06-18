package go.graphics;

public interface GL3DrawContext extends GL2DrawContext {
	void drawMulti2D(TextureHandle texture, GeometryHandle geometry, GeometryHandle drawCalls, int drawCallCount);
}
