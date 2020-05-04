package go.graphics;

public class ManagedUnifiedDrawHandle extends UnifiedDrawHandle {

	public final float texX, texY, texWidth, texHeight;
	private final ManagedHandle parent;

	protected ManagedUnifiedDrawHandle(ManagedHandle parent, float texX, float texY, float texWidth, float texHeight) {
		super(parent.bufferHolder.dc, parent.bufferHolder.id, 4*parent.quad_index++, 4, parent.bufferHolder.texture, parent.bufferHolder.vertices);
		this.texX = texX;
		this.texY = texY;
		this.parent = parent;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
	}

	@Override
	public void drawComplexQuad(int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(parent.multiCache != null) {
			parent.multiCache.schedule(this, mode, x, y, z, sx, sy, color, intensity);
		} else {
			super.drawComplexQuad(mode, x, y, z, sx, sy, color, intensity);
		}
	}
}
