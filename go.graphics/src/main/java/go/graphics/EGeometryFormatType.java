package go.graphics;

public enum EGeometryFormatType {
	Texture3D(5*4, 3*4, false),
	Texture2D(4*4, 2*4, true),
	VertexOnly2D(2*4, -1, true),
	UnifiedDrawInfo(9*4, 0, false),
	ColorOnly(4, 0, false);

	private int bytesPerVertexSize;
	private int texCoordPos;
	private boolean staticData;
	private boolean singleBuffer;

	EGeometryFormatType(int bytesPerVertexSize, int texCoordPos, boolean singleBuffer) {
		this.bytesPerVertexSize = bytesPerVertexSize;
		this.texCoordPos = texCoordPos;
		this.staticData = staticData;
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

	public boolean isStaticData() {
		return staticData;
	}
}
