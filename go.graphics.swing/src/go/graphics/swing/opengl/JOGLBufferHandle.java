package go.graphics.swing.opengl;

import go.graphics.GLBufferHandle;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

public abstract class JOGLBufferHandle implements GLBufferHandle {

	public static class JOGLGeometryHandle extends JOGLBufferHandle implements GeometryHandle {
		public JOGLGeometryHandle(JOGLDrawContext context, int geometryindex) {
			super(context, geometryindex);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("JOGLGeometryHandle [index=");
			builder.append(index);
			builder.append("]");
			return builder.toString();
		}
	}

	public static class JOGLTextureHandle extends JOGLBufferHandle implements TextureHandle {
		public JOGLTextureHandle(JOGLDrawContext context, int textureindex) {
			super(context, textureindex);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("JOGLTextureHandle [index=");
			builder.append(index);
			builder.append("]");
			return builder.toString();
		}
	}

	protected final JOGLDrawContext context;
	private boolean deleted;
	protected int index;

	public JOGLBufferHandle(JOGLDrawContext context, int index) {
		this.context = context;
		this.index = index;
	}

	@Override
	public boolean isValid() {
		return context.isValid() && context.checkGeometryIndex(index) && !deleted;
	}

	@Override
	public void delete() {
		context.deleteGeometry(index);
		deleted = true;
	}

	@Override
	public int getInternalId() {
		return index;
	}

}