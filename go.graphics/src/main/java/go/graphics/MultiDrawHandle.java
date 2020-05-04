package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MultiDrawHandle extends GLResourceIndex {

	public static final int MAX_CACHE_ENTRIES = 1000;

	public final UnifiedDrawHandle sourceQuads;
	public final BufferHandle drawCalls;
	public final int size;
	private final ByteBuffer drawCallBuffer = ByteBuffer.allocateDirect(MAX_CACHE_ENTRIES*12*4).order(ByteOrder.nativeOrder());

	public int used = 0;

	public MultiDrawHandle(GLDrawContext dc, int id, int size, ManagedHandle vertexProvider, BufferHandle drawCalls) {
		super(dc, id);

		this.sourceQuads = vertexProvider.bufferHolder;
		this.drawCalls = drawCalls;
		this.size = size;
	}

	public void schedule(ManagedUnifiedDrawHandle handle, int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(used == MAX_CACHE_ENTRIES) flush();

		int off = used*12*4;
		drawCallBuffer.putFloat(off, x);
		drawCallBuffer.putFloat(off+4, y);
		drawCallBuffer.putFloat(off+8, z);

		drawCallBuffer.putFloat(off+12, sx);
		drawCallBuffer.putFloat(off+16, sy);

		drawCallBuffer.putFloat(off+20, color!=null?color.red:1);
		drawCallBuffer.putFloat(off+24, color!=null?color.green:1);
		drawCallBuffer.putFloat(off+28, color!=null?color.blue:1);
		drawCallBuffer.putFloat(off+32, color!=null?color.alpha:1);
		drawCallBuffer.putFloat(off+36, intensity);

		drawCallBuffer.putFloat(off+40, handle.offset);
		drawCallBuffer.putFloat(off+44, mode);

		used++;
	}

	public void flush() {
		if(used == 0) return;

		drawCallBuffer.limit(used*12*4);


		try {
			dc.updateBufferAt(drawCalls, 0, drawCallBuffer);
		} catch (IllegalBufferException e) {}
		dc.drawMulti(this);

		drawCallBuffer.limit(MAX_CACHE_ENTRIES*12*4);
		used = 0;
	}

	public int getVertexArrayId() {
		return id;
	}
}
