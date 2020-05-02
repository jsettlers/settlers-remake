package go.graphics;

public class UnifiedDrawHandle extends GLResourceIndex {
	public final BufferHandle vertices;
	public TextureHandle texture;

	public int offset;
	public final int vertexCount;

	public UnifiedDrawHandle(GLDrawContext dc, int id, int offset, int vertexCount, TextureHandle texture, BufferHandle vertices) {
		super(dc, id);
		this.vertexCount = vertexCount;
		this.vertices = vertices;
		this.texture = texture;
		this.offset = offset;
	}

	private float[] trans;
	private float[] colors;
	private int cache_index = 0;

	private int cache_start_bias = 0;
	private int frame_drawcalls = 0;
	private long frameIndex = -1;
	private boolean forceNoCache = false;

	public static final int CACHE_START_AT_BIAS = 100;
	public static final int MAX_CACHE_ENTRIES = 100;

	public void forceNoCache() {
		forceNoCache = true;
	}

	private void enableCaching() {
		if(forceNoCache) return;

		trans = new float[MAX_CACHE_ENTRIES*4];
		colors = new float[MAX_CACHE_ENTRIES*4];

		dc.add(this);
	}

	private void disableCaching() {
		trans = null;
		colors = null;
		dc.remove(this);
	}

	private boolean nextFrame() {
		frameIndex = dc.frameIndex;

		boolean modified = false;
		if(trans == null && frame_drawcalls >= MAX_CACHE_ENTRIES) {
			cache_start_bias++;
			if(cache_start_bias == CACHE_START_AT_BIAS) {
				enableCaching();
				modified = true;
			}
		} else if(trans != null && frame_drawcalls < MAX_CACHE_ENTRIES){
			cache_start_bias--;

			if(cache_start_bias == -CACHE_START_AT_BIAS) {
				disableCaching();
				modified = true;
			}
		}

		frame_drawcalls = 0;
		return modified;
	}

	public boolean flush() {
		boolean mod = (frameIndex != dc.frameIndex) && nextFrame();
		if(cache_index == 0) return mod;

		dc.drawUnifiedArray(this, EPrimitiveType.Quad, 4, trans, colors, cache_index);
		cache_index = 0;

		return mod;
	}

	public void drawProgress(int primitive, float x, float y, float z, float sx, float sy, AbstractColor progressRange, float intensity) {
		dc.drawUnified(this, primitive, vertexCount, EUnifiedMode.PROGRESS, x, y, z, sx, sy, progressRange, intensity);
	}

	public void drawSimple(int primitive, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		dc.drawUnified(this, primitive, vertexCount, texture!=null?EUnifiedMode.TEXTURE : EUnifiedMode.COLOR_ONLY, x, y, z, sx, sy, color, intensity);
	}

	public void drawComplexQuad(int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(frameIndex != dc.frameIndex) nextFrame();

		if(trans != null && sx == 1 && sy == 1) {
			if(cache_index == MAX_CACHE_ENTRIES) flush();

			trans[cache_index*4] = x;
			trans[cache_index*4+1] = y;
			trans[cache_index*4+2] = z;
			trans[cache_index*4+3] = (mode*10)+intensity+1;

			colors[cache_index*4] = color!=null?color.red:1;
			colors[cache_index*4+1] = color!=null?color.green:1;
			colors[cache_index*4+2] = color!=null?color.blue:1;
			colors[cache_index*4+3] = color!=null?color.alpha:1;
			cache_index++;
		} else {
			dc.drawUnified(this, EPrimitiveType.Quad, 4, mode, x, y, z, sx, sy, color, intensity);
		}
		frame_drawcalls++;
	}

	public int getVertexArrayId() {
		return id;
	}
}
