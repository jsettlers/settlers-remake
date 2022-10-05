package go.graphics;

public class BackgroundDrawHandle extends GLResourceIndex {

	public final BufferHandle vertices;
	public final TextureHandle texture;
	public final BufferHandle colors;

	public BackgroundDrawHandle(GLDrawContext dc, int id, TextureHandle texture, BufferHandle vertices, BufferHandle colors) {
		super(dc, id);
		this.vertices = vertices;
		this.texture = texture;
		this.colors = colors;
	}

	public int offset, lines, width, stride;

	public int getVertexArrayId() {
		return id;
	}
}
