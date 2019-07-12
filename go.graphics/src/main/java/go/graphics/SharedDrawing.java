package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SharedDrawing {

	private static final int BYTES_PER_DRAW_CALL = EBufferFormatType.UnifiedDrawInfo.getBytesPerVertexSize();
	private static final AbstractColor WHITE = new AbstractColor(0, 1, 1, 1, 1) {};
	private static final int CAPACITY = 1000;

	private static GLDrawContext staticdc = null;
	private static BufferHandle drawCallBufferHandle;

	private static SharedTexture static_texture;
	private static SharedGeometry static_geometry;
	private static int drawCallCount = 0;

	private static final ByteBuffer drawCalls = ByteBuffer.allocateDirect(BYTES_PER_DRAW_CALL*CAPACITY).order(ByteOrder.nativeOrder());


	public static void schedule(GL32DrawContext dc, SharedTexture.SharedTextureHandle texture, SharedGeometry.SharedGeometryHandle geometry, float x, float y, float z, AbstractColor color, float intensity, boolean settler, boolean shadow) {
		setup(dc);

		if(static_geometry != geometry.parent || static_texture != texture.parent) {
			flushStatic();
			static_geometry = geometry.parent;
			static_texture = texture.parent;
		}

		if(drawCallCount == CAPACITY) {
			flushStatic();
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

	private static void setup(GLDrawContext dc) {
		if(staticdc != dc) {
			staticdc = dc;
			if(staticdc instanceof GL32DrawContext) drawCallBufferHandle = staticdc.generateBuffer(CAPACITY, EBufferFormatType.UnifiedDrawInfo, true, "shared-unified");
		}
	}

	public static void flush(GLDrawContext dc) {
		if(staticdc != dc) return;

		flushStatic();
		for(SharedDrawing drawing : localBuffers) drawing.flush();
	}

	private static void flushStatic() {
		if(static_texture == null || static_geometry == null || drawCallCount == 0) return;

		try {
			drawCalls.rewind();
			staticdc.updateBufferAt(drawCallBufferHandle, 0, drawCalls);

			((GL32DrawContext)staticdc).drawMultiUnified2D(static_texture.texture, static_geometry.geometry, drawCallBufferHandle, drawCallCount);
			drawCallCount = 0;

		} catch (IllegalBufferException e) {}
	}


	private static List<SharedDrawing> localBuffers = new ArrayList<>();


	private static final int OBJ_CAPACITY = 100;

	private final boolean image;
	private final boolean shadow;
	private TextureHandle texture;
	private BufferHandle geometry;
	private int primitive_type;
	private int vertex_offset;
	private int vertex_count;
	private int count = 0;

	private float[] x = new float[OBJ_CAPACITY];
	private float[] y = new float[OBJ_CAPACITY];
	private float[] z = new float[OBJ_CAPACITY];
	private AbstractColor[] color = new AbstractColor[OBJ_CAPACITY];
	private float[] intensity = new float[OBJ_CAPACITY];

	public SharedDrawing(boolean image, boolean shadow) {
		localBuffers.add(this);
		this.image = image;
		this.shadow = shadow;
	}

	public void setContent(GLDrawContext dc, TextureHandle texture, BufferHandle geometry, int primitive, int offset, int count) {
		setup(dc);

		this.texture = texture;
		this.geometry = geometry;
		primitive_type = primitive;
		vertex_offset = offset;
		vertex_count = count;
	}

	public void add(float x, float y, float z, AbstractColor color, float intensity) {
		if(count == OBJ_CAPACITY) flush();

		this.x[count] = x;
		this.y[count] = y;
		this.z[count] = z;
		this.color[count] = color;
		this.intensity[count] = intensity;
		count++;
	}

	public void flush() {
		if(count == 0) return;
		try {
			((GL2DrawContext)staticdc).drawUnified2DArray(geometry, texture, primitive_type, vertex_offset, vertex_count, image, shadow, x, y, z, color, intensity, count);
			count = 0;
		} catch (IllegalBufferException e) {}
	}
}
