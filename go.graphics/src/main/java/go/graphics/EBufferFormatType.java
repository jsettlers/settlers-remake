package go.graphics;

public enum EBufferFormatType {
	Texture2D(4*4, 2*4, true),
	VertexOnly2D(2*4, -1, true);

	private int bytesPerVertexSize;
	private int texCoordPos;
	private boolean singleBuffer;

	EBufferFormatType(int bytesPerVertexSize, int texCoordPos, boolean singleBuffer) {
		this.bytesPerVertexSize = bytesPerVertexSize;
		this.texCoordPos = texCoordPos;
		this.singleBuffer = singleBuffer;
	}

	public int getBytesPerVertexSize() {
		return this.bytesPerVertexSize;
	}

	public int getTexCoordPos() {
		return this.texCoordPos;
	}

	public boolean isSingleBuffer() {
		return singleBuffer;
	}
}
