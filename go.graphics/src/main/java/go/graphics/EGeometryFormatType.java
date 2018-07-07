package go.graphics;

public enum EGeometryFormatType {
	Texture2D(4*4, 2*4),
	VertexOnly2D(2*4, -1),
	Background(3*4, -1);

	private int bytesPerVertexSize;
	private int texCoordPos;

	private EGeometryFormatType(int bytesPerVertexSize, int texCoordPos) {
		this.bytesPerVertexSize = bytesPerVertexSize;
		this.texCoordPos = texCoordPos;
	}

	public int getBytesPerVertexSize() {
		return this.bytesPerVertexSize;
	}

	public int getTexCoordPos() {
		return this.texCoordPos;
	}
}
