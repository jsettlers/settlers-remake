package go.graphics.android;

import go.graphics.ETextureType;
import go.graphics.TextureHandle;

import static android.opengl.GLES20.*;

class GLESTextureHandle extends TextureHandle {
	public GLESTextureHandle(GLESDrawContext dc, int texture) {
		super(dc, texture);
	}

	@Override
	public void setType(ETextureType type) {
		super.setType(type);

		((GLESDrawContext)dc).bindTexture(this);
		switch(getType()) {
			case LINEAR_FILTER:
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				break;
			case NEAREST_FILTER:
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				break;
		}
	}
}
