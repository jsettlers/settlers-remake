package go.graphics;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import go.graphics.text.AbstractTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public abstract class GLDrawContext {

	public GLDrawContext() {
		ManagedHandle.instance_count = 0;
	}

	protected List<ManagedHandle> managedHandles = new ArrayList<>();

	public abstract void setShadowDepthOffset(float depth);

	/**
	 * Returns a texture id which is positive or 0. It returns a negative number on error.
	 *
	 * @param width
	 * @param height
	 *            The height of the image.
	 * @param data
	 *            The data as array. It needs to have a length of width * height and each element is a color with: 4 bits red, 4 bits green, 4 bits
	 *            blue and 4 bits alpha.
	 * @return The id of the generated texture.
	 */
	public abstract TextureHandle generateTexture(int width, int height, ShortBuffer data, String name);

	protected abstract void drawMulti(MultiDrawHandle call);
	protected abstract void drawUnifiedArray(UnifiedDrawHandle call, int primitive, int vertexCount, float[] trans, float[] colors, int array_len);
	protected abstract void drawUnified(UnifiedDrawHandle call, int primitive, int vertices, int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity);

	public abstract void drawBackground(BackgroundDrawHandle call);

	public abstract void setHeightMatrix(float[] matrix);

	public abstract void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz);

	/**
	 * Updates a part of a texture image.
	 *
	 * @param textureIndex
	 *            The texture to use.
	 * @param left
	 * @param bottom
	 * @param width
	 * @param height
	 * @param data
	 * @throws IllegalBufferException
	 */
	public abstract void updateTexture(TextureHandle textureIndex, int left, int bottom, int width, int height, ShortBuffer data) throws IllegalBufferException;

	public abstract TextureHandle resizeTexture(TextureHandle textureIndex, int width, int height, ShortBuffer data);

	public abstract void updateBufferAt(BufferHandle handle, int pos, ByteBuffer data) throws IllegalBufferException;

	public abstract BackgroundDrawHandle createBackgroundDrawCall(int vertices, TextureHandle texture);

	protected AbstractTextDrawer textDrawer;
	private final TextDrawer[] sizedTextDrawers = new TextDrawer[EFontSize.values().length];

	/**
	 * Gets a text drawer for the given text size.
	 *
	 * @param size
	 *            The size for the drawer.
	 * @return An instance of a drawer for that size.
	 */
	public TextDrawer getTextDrawer(EFontSize size) {
		if (sizedTextDrawers[size.ordinal()] == null) {
			sizedTextDrawers[size.ordinal()] = textDrawer.derive(size);
		}
		return sizedTextDrawers[size.ordinal()];
	}

	/**
	 *
	 * @param vertices
	 * 		Maximum number of vertices
	 * @param name
	 * 		The label that the OpenGL handles get (nullable)
	 * @param texture
	 * 		It determines whether this handle is textured or only single colored
	 * @param data
	 * 		If data is not equal null this will be a readonly buffer filled with data
	 * @return
	 * 		A handle to draw via the unified shader
	 */
	public abstract UnifiedDrawHandle createUnifiedDrawCall(int vertices, String name, TextureHandle texture, float[] data);

	protected abstract MultiDrawHandle createMultiDrawCall(String name, ManagedHandle source);

	public static float[] createQuadGeometry(float lx, float ly, float hx, float hy, float lu, float lv, float hu, float hv) {
		return new float[] {
				// bottom right
				hx, ly, hu, lv,
				// top right
				hx, hy, hu, hv,
				// top left
				lx, hy, lu, hv,
				// bottom left
				lx, ly, lu, lv,
		};
	}

	public static final int MAX_CACHE_COUNT = 10;
	private void addNewHandle() {
		if(MAX_CACHE_COUNT == managedHandles.size()) throw new Error("ManangedHandle slots exceeded!");
		TextureHandle tex = generateTexture(ManagedHandle.TEX_DIM, ManagedHandle.TEX_DIM, null, "managed" + ManagedHandle.instance_count);
		UnifiedDrawHandle parent = createUnifiedDrawCall(ManagedHandle.MAX_QUADS*4, "managed" + ManagedHandle.instance_count, tex, null);
		managedHandles.add(new ManagedHandle(parent));
	}

	public ManagedUnifiedDrawHandle createManagedUnifiedDrawCall(ShortBuffer texData, float offsetX, float offsetY, int width, int height) {
		for(ManagedHandle handle : managedHandles) {
			int position;
			if(handle.quad_index != ManagedHandle.MAX_QUADS && (position = handle.findTextureHole(width, height)) != -1) {
				UIPoint corner;
				if((corner = handle.addTexture(texData, width, height, position)) == null) continue;


				float lu = (float) corner.getX();
				float lv = (float) corner.getY();
				float hu = lu + width/(float) ManagedHandle.TEX_DIM;
				float hv = lv + height/(float) ManagedHandle.TEX_DIM;

				float[] data = createQuadGeometry(offsetX, -offsetY, offsetX+width, -offsetY-height, lu, lv, hu, hv);

				handle.addQuad(data);

				return new ManagedUnifiedDrawHandle(handle, lu, lv, hu, hv);
			}
		}

		addNewHandle();
		return createManagedUnifiedDrawCall(texData, offsetX, offsetY, width, height);
	}

	private boolean valid = true;

	public void invalidate() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}

	public abstract void clearDepthBuffer();

	protected void add(UnifiedDrawHandle cache) {
		caches.add(cache);
	}

	protected void remove(UnifiedDrawHandle cache) {
		caches.remove(cache);
	}

	private List<UnifiedDrawHandle> caches = new ArrayList<>();

	public void finishFrame() {
		for(int i = 0;i != caches.size(); i++) {
			if(caches.get(i).flush()) i--;
		}

		for(ManagedHandle mh : managedHandles) {
			if(mh.multiCache != null) mh.multiCache.flush();
		}
	}

	protected long frameIndex = 0;

	public void startFrame() {
		frameIndex++;
	}

    public abstract void resize(int width, int height);
}
