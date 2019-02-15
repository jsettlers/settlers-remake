package go.graphics;

import java.nio.ByteBuffer;
import java.util.BitSet;

import java8.util.function.Supplier;

public class AdvancedUpdateGeometryCache {
	private ByteBuffer buffer;
	private int bfr_data_steps;
	private Supplier<GLDrawContext> ctx_supp;
	private Supplier<GeometryHandle> bfr_supp;
	private BitSet updated;

	public AdvancedUpdateGeometryCache(ByteBuffer buffer, int bfr_data_steps, Supplier<GLDrawContext> ctx_supp, Supplier<GeometryHandle> bfr_supp) {
		this.bfr_data_steps = bfr_data_steps;
		this.ctx_supp = ctx_supp;
		this.bfr_supp = bfr_supp;
		this.buffer = buffer;
		updated = new BitSet(buffer.capacity()/bfr_data_steps);
	}

	public void gotoLine(int start, int count) {
		updated.set(start, start+count);
		buffer.position(start * bfr_data_steps);
	}

	public void clearCacheRegion(int start, int end) throws IllegalBufferException {
		int urEnd = start;
		while(urEnd < end) {
			int urStart = updated.nextSetBit(urEnd);
			if(urStart > end || urStart == -1) return;
			urEnd = updated.nextClearBit(urStart);
			if(urEnd > end || urEnd == -1) urEnd = end;
			updateRegion(urStart, urEnd);
			updated.clear(urStart, urEnd);
		}
	}

	private void updateRegion(int start, int end) throws IllegalBufferException {
		buffer.limit(end * bfr_data_steps);
		buffer.position(start * bfr_data_steps);
		ctx_supp.get().updateGeometryAt(bfr_supp.get(), start * bfr_data_steps, buffer);
		buffer.limit(buffer.capacity());
	}
}
