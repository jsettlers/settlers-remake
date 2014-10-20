package go.graphics.nativegl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This is an improved gl wrapper that can handle indirect buffers
 * 
 * @author michael
 */
public class IndirectBufferLoadableGlWrapper extends NativeGLWrapper {
	public void drawTrianglesWithTextureColored(int currentTexture,
	        java.nio.ByteBuffer byteBuffer, int currentTriangles) {
		super.drawTrianglesWithTextureColored(currentTexture,
		        makeDirect(byteBuffer), currentTriangles);
	}

	@Override
	public void updateTexture(int textureIndex, int left, int bottom,
	        int width, int height, ShortBuffer data) {
		super.updateTexture(textureIndex, left, bottom, width, height,
		        makeDirect(data));
	}

	@Override
	public int generateTexture(int width, int height, ShortBuffer data) {
		return super.generateTexture(width, height, makeDirect(data));
	}

	private static ShortBuffer makeDirect(ShortBuffer data) {
		if (data.isDirect()) {
			return data;
		} else {
			ShortBuffer newBuffer =
			        ByteBuffer.allocateDirect(data.capacity() * 2)
			                .order(ByteOrder.nativeOrder()).asShortBuffer();
			int pos = data.position();
			data.position(0);
			newBuffer.put(data);
			data.position(pos);
			newBuffer.position(pos);
			return newBuffer;
		}
	}

	/**
	 * Converts any buffer to a direct buffer.
	 * 
	 * @param byteBuffer
	 *            The buffer to convert
	 * @return A (maby new) buffer.
	 */
	private static ByteBuffer makeDirect(ByteBuffer byteBuffer) {
		if (byteBuffer.isDirect()) {
			return byteBuffer;
		} else {
			ByteBuffer newBuffer =
			        ByteBuffer.allocateDirect(byteBuffer.capacity());
			int pos = byteBuffer.position();
			byteBuffer.position(0);
			newBuffer.put(byteBuffer);
			byteBuffer.position(pos);
			newBuffer.position(pos);
			return newBuffer;
		}
	};
}
