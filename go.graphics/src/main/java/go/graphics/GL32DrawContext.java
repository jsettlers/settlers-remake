package go.graphics;

public interface GL32DrawContext extends GL2DrawContext {
	void drawMultiUnified2D(TextureHandle texture, GeometryHandle geometry, GeometryHandle drawCalls, int drawCallCount);
}
