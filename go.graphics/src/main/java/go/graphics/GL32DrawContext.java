package go.graphics;

public interface GL32DrawContext {
	public abstract void drawMultiUnified2D(TextureHandle texture, BufferHandle geometry, BufferHandle drawCalls, int drawCallCount);
}
