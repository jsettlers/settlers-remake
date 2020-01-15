package go.graphics.swing.opengl;

import go.graphics.ETextureType;
import go.graphics.GLDrawContext;
import go.graphics.TextureHandle;

import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.glTexParameteri;

public class LWJGLTextureHandle extends TextureHandle {
	public LWJGLTextureHandle(LWJGLDrawContext dc, int texture) {
		super(dc, texture);
	}

	@Override
	public void setType(ETextureType type) {
		super.setType(type);

		((LWJGLDrawContext)dc).bindTexture(this);
		switch(getType()) {
			case LINEAR_FILTER:
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				break;
			case NEAREST_FILTER:
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				break;
		}
	}
}
