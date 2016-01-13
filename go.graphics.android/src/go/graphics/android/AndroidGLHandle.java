package go.graphics.android;

import go.graphics.GLBufferHandle;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;
import android.opengl.GLES10;
import android.opengl.GLES11;

public class AndroidGLHandle implements GLBufferHandle {
	public static class AndroidTextureHandle extends AndroidGLHandle implements TextureHandle {
		public AndroidTextureHandle(int internalId) {
			super(internalId);
		}

		@Override
		public void delete() {
			GLES10.glDeleteTextures(1, new int[] {
					getInternalId()
			}, 0);
		}

		@Override
		public boolean isValid() {
			return super.isValid() && GLES11.glIsTexture(getInternalId());
		}
	}

	public static class AndroidGeometryHandle extends AndroidGLHandle implements GeometryHandle {
		public AndroidGeometryHandle(int internalId) {
			super(internalId);
		}

		@Override
		public void delete() {
			GLES11.glDeleteBuffers(1, new int[] {
					getInternalId()
			}, 0);
		}

		@Override
		public boolean isValid() {
			return super.isValid() && GLES11.glIsBuffer(getInternalId());
		}
	}

	private int internalId;
	private boolean deleted;

	public AndroidGLHandle(int internalId) {
		this.internalId = internalId;
	}

	@Override
	public boolean isValid() {
		return !deleted;
	}

	@Override
	public void delete() {
		// TODO: Free memeory
		deleted = true;
	}

	@Override
	public int getInternalId() {
		return internalId;
	}
}
