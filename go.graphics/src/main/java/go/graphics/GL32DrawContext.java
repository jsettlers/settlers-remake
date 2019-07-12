package go.graphics;

public interface GL32DrawContext extends GL2DrawContext {
	void drawMultiUnified2D(TextureHandle texture, BufferHandle geometry, BufferHandle drawCalls, int drawCallCount);
}
