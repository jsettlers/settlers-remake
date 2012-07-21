package jsettlers.graphics.map.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import jsettlers.graphics.map.MapDrawContext;

public class DrawBuffer {
	
	private static final int BUFFERS = 5;
	private final MapDrawContext context;
	protected float z;

	class Buffer {
		/**
		 * Bytes we need for one vertex
		 */
		private static final int VERTEX_LENGTH = 5 * 4 + 4;
		private static final int TRIAMGLE_LENGTH = 3 * VERTEX_LENGTH;
		private static final int BUFFER_TRIANGLES = 1000;


		/**
		 * The last texture we set.
		 */
		private int currentTexture = 0;

		private int currentTriangles = 0;

		protected final ByteBuffer byteBuffer;

		protected Buffer() {
			byteBuffer =
			        ByteBuffer.allocateDirect(BUFFER_TRIANGLES * TRIAMGLE_LENGTH);
			byteBuffer.order(ByteOrder.nativeOrder());
		}

		protected void setForTexture(int texture) {
			if (currentTexture != texture) {
				if (currentTriangles != 0) {
					draw();
				}
				currentTexture = texture;
			}
		}

		protected void draw() {
			byteBuffer.rewind();
			context.getGl().drawTrianglesWithTextureColored(currentTexture, byteBuffer, currentTriangles);
			byteBuffer.rewind();
			currentTriangles = 0;
		}
		
		protected void addImage(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int activeColor) { 
			addPointPrimitive(x1, y1, u1, v1, activeColor);
			addPointPrimitive(x1, y2, u1, v2, activeColor);
			addPointPrimitive(x2, y1, u2, v1, activeColor);
			addPointPrimitive(x2, y1, u2, v1, activeColor);
			addPointPrimitive(x1, y2, u1, v2, activeColor);
			addPointPrimitive(x2, y2, u2, v2, activeColor);
			currentTriangles += 2;
        }
		
		private void addPointPrimitive(float x1, float y1, float u, float v, int activeColor) {
			byteBuffer.putFloat(x1);
			byteBuffer.putFloat(y1);
			byteBuffer.putFloat(z);
			byteBuffer.putFloat(u);
			byteBuffer.putFloat(v);
			byteBuffer.putInt(activeColor);
		}
	}

	private int lastFreedBuffer = 0;
	private final Buffer[] drawBuffers;

	public DrawBuffer(MapDrawContext context) {
		this.context = context;
		drawBuffers = new Buffer[BUFFERS];
		for (int i = 0; i < BUFFERS; i++) {
			drawBuffers[i] = new Buffer();
		}
    }
		
	public void addImage(int texture, float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int activeColor) {
		z += .0001f;
		getBuffer(texture).addImage(x1, y1, x2, y2, u1, v1, u2, v2, activeColor);
	}

	protected Buffer getBuffer(int texture) {
		for (int i = 0; i < BUFFERS; i++) {
			if (drawBuffers[i].currentTexture == texture) {
				return drawBuffers[i];
			}
		}

		lastFreedBuffer++;
		if (lastFreedBuffer >= BUFFERS) {
			lastFreedBuffer = 0;
		}

		Buffer buffer = drawBuffers[lastFreedBuffer];
		buffer.setForTexture(texture);
		return buffer;
	}


	public void flush() {
		for (int i = 0; i < BUFFERS; i++) {
			drawBuffers[i].draw();
		}
		z = 0;
	}

	
}
