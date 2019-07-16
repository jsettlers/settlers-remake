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

	public static final int MAX_CACHE_ENTRIES = 100;

	public void enableCaching() {
		if(trans != null) return;

		trans = new float[MAX_CACHE_ENTRIES*4];
		colors = new float[MAX_CACHE_ENTRIES*4];

		dc.add(this);
	}

	protected void flush() {
		if(cache_index == 0) return;

		dc.drawUnifiedArray(this, EPrimitiveType.Quad, 4, trans, colors, cache_index);
		cache_index = 0;
	}

	public void drawSimple(int primitive, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		dc.drawUnified(this, primitive, vertexCount, texture!=null?EUnifiedMode.TEXTURE : EUnifiedMode.COLOR_ONLY, x, y, z, sx, sy, color, intensity);
	}

	public void drawComplexQuad(int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(trans != null) {
			if(cache_index == MAX_CACHE_ENTRIES) flush();

			trans[cache_index*4] = x;
			trans[cache_index*4+1] = y;
			trans[cache_index*4+2] = z;
			trans[cache_index*4+3] = (mode*10)+intensity+1;

			colors[cache_index*4] = color.red;
			colors[cache_index*4+1] = color.green;
			colors[cache_index*4+2] = color.blue;
			colors[cache_index*4+3] = color.alpha;
			cache_index++;
		} else {
			dc.drawUnified(this, EPrimitiveType.Quad, 4, mode, x, y, z, sx, sy, color, intensity);
		}
	}

	public int getVertexArrayId() {
		return id;
	}
}
