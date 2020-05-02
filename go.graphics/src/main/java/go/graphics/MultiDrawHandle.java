package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MultiDrawHandle extends GLResourceIndex {

	public static final int MAX_CACHE_ENTRIES = 1000;

	public final UnifiedDrawHandle sourceQuads;
	public final BufferHandle drawCalls;
	public final int size;
	private final float[] drawCallArray = new float[MAX_CACHE_ENTRIES*12];
	private final ByteBuffer drawCallBuffer = ByteBuffer.allocateDirect(drawCallArray.length*4).order(ByteOrder.nativeOrder());

	public int used = 0;

	public MultiDrawHandle(GLDrawContext dc, int id, int size, ManagedHandle vertexProvider, BufferHandle drawCalls) {
		super(dc, id);

		this.sourceQuads = vertexProvider.bufferHolder;
		this.drawCalls = drawCalls;
		this.size = size;
	}

	public void schedule(ManagedUnifiedDrawHandle handle, int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(used == MAX_CACHE_ENTRIES) flush();

		drawCallArray[used*12] = x;
		drawCallArray[used*12+1] = y;
		drawCallArray[used*12+2] = z;

		drawCallArray[used*12+3] = sx;
		drawCallArray[used*12+4] = sy;

		drawCallArray[used*12+5] = color!=null?color.red:1;
		drawCallArray[used*12+6] = color!=null?color.green:1;
		drawCallArray[used*12+7] = color!=null?color.blue:1;
		drawCallArray[used*12+8] = color!=null?color.alpha:1;
		drawCallArray[used*12+9] = intensity;

		drawCallArray[used*12+10] = handle.offset;
		drawCallArray[used*12+11] = mode;

		used++;
	}

	public void flush() {
		if(used == 0) return;

		drawCallBuffer.limit(used*12*4);
		drawCallBuffer.asFloatBuffer().put(drawCallArray, 0, used*12);


		try {
			dc.updateBufferAt(drawCalls, 0, drawCallBuffer);
		} catch (IllegalBufferException e) {}

		dc.drawMulti(this);
		used = 0;
	}

	public int getVertexArrayId() {
		return id;
	}
}
