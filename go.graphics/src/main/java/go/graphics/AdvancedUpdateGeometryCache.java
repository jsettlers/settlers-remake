package go.graphics;

import java.nio.ByteBuffer;
import java.util.BitSet;

import java8.util.function.Supplier;

public class AdvancedUpdateGeometryCache {
	private ByteBuffer buffer;
	private int bfr_data_steps;
	private Supplier<GLDrawContext> ctx_supp;
	private Supplier<GeometryHandle> bfr_supp;
	private BitSet[] updated;
	private int line_width;

	public AdvancedUpdateGeometryCache(ByteBuffer buffer, int bfr_data_steps, Supplier<GLDrawContext> ctx_supp, Supplier<GeometryHandle> bfr_supp, int line_width) {
		this.bfr_data_steps = bfr_data_steps;
		this.line_width = line_width;
		this.ctx_supp = ctx_supp;
		this.bfr_supp = bfr_supp;
		this.buffer = buffer;

		int lines = buffer.capacity()/bfr_data_steps/line_width;
		updated = new BitSet[lines];
		for(int i = 0;i != lines;i++) updated[i] = new BitSet(line_width);
	}

	public void gotoLine(int line, int start, int count) {
		updated[line].set(start, start + count);
		buffer.position((line*line_width+start) * bfr_data_steps);
	}

	public void clearCacheRegion(int line, int start, int end) throws IllegalBufferException {
		int urEnd = start;
		while(urEnd < end) {
			int urStart = updated[line].nextSetBit(urEnd);
			if(urStart > end || urStart == -1) return;
			urEnd = updated[line].nextClearBit(urStart);
			if(urEnd > end || urEnd == -1) urEnd = end;
			updateRegion(line, urStart, urEnd);
			updated[line].clear(urStart, urEnd);
		}
	}

	private void updateRegion(int line, int start, int end) throws IllegalBufferException {
		start += line*line_width;
		end += line*line_width;

		buffer.limit(end * bfr_data_steps);
		buffer.position(start * bfr_data_steps);
		ctx_supp.get().updateGeometryAt(bfr_supp.get(), start * bfr_data_steps, buffer);
		buffer.limit(buffer.capacity());
	}
}
