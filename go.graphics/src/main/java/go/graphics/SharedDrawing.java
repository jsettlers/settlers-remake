package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SharedDrawing {

	private static final int BYTES_PER_DRAW_CALL = EGeometryFormatType.UnifiedDrawInfo.getBytesPerVertexSize();
	private static final AbstractColor WHITE = new AbstractColor(0, 1, 1, 1, 1) {};
	private static final int CAPACITY = 1000;

	private static GL3DrawContext staticdc = null;
	private static GeometryHandle drawCallBufferHandle;

	private static SharedTexture texture;
	private static SharedGeometry geometry;
	private static int drawCallCount = 0;

	private static final ByteBuffer drawCalls = ByteBuffer.allocateDirect(BYTES_PER_DRAW_CALL*CAPACITY).order(ByteOrder.nativeOrder());


	public static void schedule(GL3DrawContext dc, SharedTexture.SharedTextureHandle texture, SharedGeometry.SharedGeometryHandle geometry, float x, float y, float z, AbstractColor color, float intensity, boolean settler, boolean shadow) {
		if(staticdc != dc) {
			staticdc = dc;
			drawCallBufferHandle = staticdc.generateGeometry(CAPACITY, EGeometryFormatType.UnifiedDrawInfo, true, "shared-unified");
		}

		if(SharedDrawing.geometry != geometry.parent || SharedDrawing.texture != texture.parent) {
			flush(dc);
			SharedDrawing.geometry = geometry.parent;
			SharedDrawing.texture = texture.parent;
		}

		if(drawCallCount == CAPACITY) {
			flush(dc);
		}

		drawCalls.putFloat(x);
		drawCalls.putFloat(y);
		drawCalls.putFloat(z);

		if(color == null) color = WHITE;

		drawCalls.putFloat(color.red);
		drawCalls.putFloat(color.green);
		drawCalls.putFloat(color.blue);
		drawCalls.putFloat(color.alpha);

		drawCalls.putFloat(geometry.index);
		drawCalls.putFloat((settler?-1:1) * ((shadow?10:5) + intensity));
		drawCallCount++;
	}

	public static void flush(GL3DrawContext dc) {
		if(staticdc != dc || texture == null || geometry == null) return;
		drawCalls.rewind();

		try {
			staticdc.updateGeometryAt(drawCallBufferHandle, 0, drawCalls);

			staticdc.drawMulti2D(texture.texture, geometry.geometry, drawCallBufferHandle, drawCallCount);
			drawCallCount = 0;

		} catch (IllegalBufferException e) {}


	}
}
