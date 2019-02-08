package go.graphics;

import java.nio.ByteBuffer;

import java8.util.function.Supplier;

public class UpdateGeometryCache {
	private int position = 0;
	private int cache_size = 0;
	private int cache_start = 0;

	private ByteBuffer buffer;
	private int bfr_data_steps;
	private Supplier<GLDrawContext> ctx_supp;
	private Supplier<GeometryHandle> bfr_supp;

	public UpdateGeometryCache(ByteBuffer buffer, int bfr_data_steps, Supplier<GLDrawContext> ctx_supp, Supplier<GeometryHandle> bfr_supp) {
		this.bfr_data_steps = bfr_data_steps;
		this.ctx_supp = ctx_supp;
		this.bfr_supp = bfr_supp;
		this.buffer = buffer;
	}

	public void gotoPos(int new_position) throws IllegalBufferException {
		if(new_position == position+1) {
			cache_size += bfr_data_steps;
		} else {
			clearCache();
			cache_size = bfr_data_steps;
			cache_start = new_position;
		}
		position = new_position;
	}

	public void clearCache() throws IllegalBufferException {
		if(cache_size == 0) return;

		buffer.limit(cache_size);
		buffer.rewind();
		ctx_supp.get().updateGeometryAt(bfr_supp.get(), cache_start*bfr_data_steps, buffer);
		buffer.limit(buffer.capacity());
		position = 0;
		cache_size = 0;
		cache_start = 0;
	}
}
