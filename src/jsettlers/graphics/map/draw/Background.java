package jsettlers.graphics.map.draw;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IHexTile;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.TextureCalculator;
import jsettlers.graphics.map.MapDrawContext;

/**
 * The map background
 * 
 * @author michael
 */
public class Background {
	private static final int LAND_FILE = 0;

	private static final int CONTINOUS_SIZE = 128;

	ImageProvider imageProvider = ImageProvider.getInstance();

	private IntRectangle bufferScreen;

	private int buffer;

	private int renderbuffer;

	private int texture;

	private int textureWidth;

	private int textureHeight;

	private int checksum;

	/**
	 * gets the image number of the border
	 * 
	 * @param left
	 * @param bottom
	 * @return
	 */
	private Image getBorder(ELandscapeType outer, ELandscapeType inner) {
		int index;

		// grass <=> dessert
		if (outer == ELandscapeType.GRASS && inner == ELandscapeType.DESERT) {
			index = 5;
		} else if (outer == ELandscapeType.DESERT
		        && inner == ELandscapeType.GRASS) {
			index = 5;// TODO

			// water <=> sand
		} else if (outer == ELandscapeType.WATER
		        && inner == ELandscapeType.SAND) {
			index = 39;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.WATER) {
			index = 37;

			// grass <=> mountain
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 116;
		} else if (outer == ELandscapeType.MOUNTAINBORDER
		        && inner == ELandscapeType.GRASS) {
			index = 118;

		} else if (outer == ELandscapeType.MOUNTAINBORDER
		        && inner == ELandscapeType.MOUNTAIN) {
			index = 120;
		} else if (outer == ELandscapeType.MOUNTAIN
		        && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 122;
			// TODO: one more outer circle with 124/126

			// mountain <=> snow
		} else if (outer == ELandscapeType.MOUNTAIN
		        && inner == ELandscapeType.SNOW) {
			index = 156;
		} else if (outer == ELandscapeType.SNOW
		        && inner == ELandscapeType.MOUNTAIN) {
			index = 158;

			// earth <=> grass
		} else if (outer == ELandscapeType.EARTH
		        && inner == ELandscapeType.GRASS) {
			index = 170;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.EARTH) {
			index = 168;

			// grass <=> dry grass
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.DRY_GRASS) {
			index = 116;
		} else if (outer == ELandscapeType.DRY_GRASS
		        && inner == ELandscapeType.GRASS) {
			index = 118;

			// dry grass <=> desert
		} else if (outer == ELandscapeType.DRY_GRASS
		        && inner == ELandscapeType.DESERT) {
			index = 136;
		} else if (outer == ELandscapeType.DESERT
		        && inner == ELandscapeType.DRY_GRASS) {
			index = 138;

			// river <=> grass
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER1) {
			index = 52;
		} else if (outer == ELandscapeType.RIVER1
		        && inner == ELandscapeType.GRASS) {
			index = 54;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER1) {
			index = 56;
		} else if (outer == ELandscapeType.RIVER2
		        && inner == ELandscapeType.GRASS) {
			index = 58;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER3) {
			index = 60;
		} else if (outer == ELandscapeType.RIVER3
		        && inner == ELandscapeType.GRASS) {
			index = 62;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER4) {
			index = 64;
		} else if (outer == ELandscapeType.RIVER4
		        && inner == ELandscapeType.GRASS) {
			index = 66;

			// sand <=> river
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER1) {
			index = 68;
		} else if (outer == ELandscapeType.RIVER1
		        && inner == ELandscapeType.SAND) {
			index = 70;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER1) {
			index = 72;
		} else if (outer == ELandscapeType.RIVER2
		        && inner == ELandscapeType.SAND) {
			index = 74;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER3) {
			index = 76;
		} else if (outer == ELandscapeType.RIVER3
		        && inner == ELandscapeType.SAND) {
			index = 78;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER4) {
			index = 80;
		} else if (outer == ELandscapeType.RIVER4
		        && inner == ELandscapeType.SAND) {
			index = 82;

			// grass <=> sand
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.SAND) {
			index = 114;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.GRASS) {
			index = 112;

			// grass <=> flattened
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.FLATTENED) {
			index = 172;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.FLATTENED) {
			index = 174;

		} else {
			index = outer.getImageNumber();
		}
		return this.imageProvider.getLandscapeImage(LAND_FILE, index);
	}

	/**
	 * Draws a given map content.
	 * 
	 * @param context
	 *            The context to draw at.
	 */
	public void drawMapContent(MapDrawContext context) {
		if (useRenderbuffer(context.getGl())) {
			drawWithRenderbuffer(context);
		} else {
			draw(context);
		}
	}

	private boolean useRenderbuffer(GL2 gl) {
		return gl.isExtensionAvailable("GL_EXT_framebuffer_object");
	}

	private void drawWithRenderbuffer(MapDrawContext context) {
		IntRectangle screen = context.getScreen().getPosition();
		GL2 gl = context.getGl();
		int width = screen.getWidth();
		int height = screen.getHeight();
		int newChecksum = computeChecksum(context);
		if (!screen.equals(bufferScreen) || checksum != newChecksum) {
			if (bufferScreen == null || width != bufferScreen.getWidth()
			        || height != bufferScreen.getHeight()) {
				createNewBuffer(gl, width, height);
			}
			bufferScreen = screen;
			checksum = newChecksum;
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buffer);
			gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
			gl.glViewport(0, 0, textureWidth, textureHeight);
			gl.glClearColor(0, 0, 0, 1);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);
			draw(context);
			gl.glPopAttrib();
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		}

		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(screen.getMinX(), screen.getMinY());
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(screen.getMinX(), screen.getMinY() + textureHeight);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(screen.getMinX() + textureWidth, screen.getMinY()
		        + textureHeight);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(screen.getMinX() + textureWidth, screen.getMinY());
		gl.glEnd();
	}

	private int computeChecksum(MapDrawContext context) {
		IMapArea area = context.getScreenArea();
		int hash = 0;
		for (ISPosition2D pos : area) {
			IHexTile tile = context.getTile(pos);
			if (tile != null) {
				int key =
				        tile.getHeight() << 16 + tile.getLandscapeType()
				                .ordinal();
				hash += key;
				hash += (hash << 12);
				hash ^= hash >> 10;
			}
		}
		return hash;
	}

	/**
	 * Newly creates the buffer.
	 * 
	 * @param width
	 * @param height
	 */
	private void createNewBuffer(GL2 gl, int width, int height) {
		if (buffer != 0) {
			gl.glDeleteFramebuffers(1, new int[] {
				buffer
			}, 0);
			gl.glDeleteRenderbuffers(1, new int[] {
				renderbuffer
			}, 0);
			gl.glDeleteTextures(0, new int[] {
				texture
			}, 0);
		}

		textureWidth = TextureCalculator.supportedTextureSize(gl, width);
		textureHeight = TextureCalculator.supportedTextureSize(gl, height);

		int[] buffers = new int[1];
		gl.glGenFramebuffers(1, buffers, 0);
		buffer = buffers[0];
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buffer);

		// renderbuffer
		gl.glGenRenderbuffers(1, buffers, 0);
		renderbuffer = buffers[0];
		gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderbuffer);
		gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_RGBA8, textureWidth,
		        textureHeight);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER,
		        GL.GL_COLOR_ATTACHMENT0, GL.GL_RENDERBUFFER, renderbuffer);

		// texture
		gl.glGenTextures(1, buffers, 0);
		texture = buffers[0];
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, textureWidth,
		        textureHeight, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		        GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		        GL.GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0,
		        GL.GL_TEXTURE_2D, texture, 0);

		int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);

		if (status != GL.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("incomplete framebuffer");
		}
	}

	private void draw(MapDrawContext context) {
		// set up gl context
		GL2 gl = context.getGl();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor3f(1, 1, 1);
		gl.glPushMatrix();
		gl.glScalef(1, 1, 0);
		gl.glMultMatrixf(context.getConverter().getMatrixWithHeight(), 0);

		IntRectangle screen = context.getScreen().getPosition();

		// calculate draw rect.
		ISPosition2D lefttop =
		        context.getPositionUnder(screen.getMinX(), screen.getMaxY());
		ISPosition2D righttop =
		        context.getPositionUnder(screen.getMaxX(), screen.getMaxY());
		ISPosition2D leftbottom =
		        context.getPositionUnder(screen.getMinX(), screen.getMinY());

		short drawWidth = (short) (righttop.getX() - lefttop.getX() + 30); // TODO
		short drawHeight = (short) (leftbottom.getY() - lefttop.getY() + 30);
		short drawXOffset = (short) (lefttop.getX() - 10);
		short drawYOffset = (short) (lefttop.getY() - 10);

		// this is an array of (x,y)-touples that indicate the position of the
		// fields in the screen
		int[][] currentLine = new int[drawWidth][3];
		ELandscapeType[] currentLandscapes = new ELandscapeType[drawWidth];

		int[][] nextLine = new int[drawWidth][3];
		ELandscapeType[] nextLandscapes = new ELandscapeType[drawWidth];
		loadLine(context, drawXOffset, drawYOffset, currentLine,
		        currentLandscapes);

		for (int line = 0; line < drawHeight; line++) {
			short y = (short) (line + drawYOffset);
			loadLine(context, (short) (drawXOffset + line / 2),
			        (short) (y + 1), nextLine, nextLandscapes);

			drawLine(context, currentLine, currentLandscapes, nextLine,
			        nextLandscapes, line % 2 == 0,
			        (short) (drawXOffset + (line - 1) / 2), y);

			int[][] temp = currentLine;
			currentLine = nextLine;
			nextLine = temp;
			ELandscapeType[] temp2 = currentLandscapes;
			currentLandscapes = nextLandscapes;
			nextLandscapes = temp2;
		}

		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}

	private void drawLine(MapDrawContext context, int[][] currentLine,
	        ELandscapeType[] currentLandscapes, int[][] nextLine,
	        ELandscapeType[] nextLandscapes, boolean nextLineRight, short x,
	        short y) {

		int nextOffset = nextLineRight ? -1 : 0;
		GL2 gl = context.getGl();
		gl.glColor3f(1, 1, 1);

		for (int i = -nextOffset; i < currentLine.length - 1; i++) {
			ELandscapeType current = currentLandscapes[i];
			ELandscapeType right = currentLandscapes[i + 1];
			ELandscapeType bottomleft = nextLandscapes[i + nextOffset];
			ELandscapeType bottomright = nextLandscapes[i + 1 + nextOffset];
			if (current == null || right == null || bottomleft == null
			        || bottomright == null) {
				continue;
			}

			int myHeight = currentLine[i][2];
			int nextHeight = nextLine[i + nextOffset][2];
			float color =
			        Math.min(Math.max(1 + (myHeight - nextHeight) * .2f, 0), 1);
			gl.glColor3f(color, color, color);

			// left triangle
			if (current == bottomright && current == bottomleft) {
				getContinousImage(current).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setContinousTexCoord(gl, x + i, y);
				gl.glVertex3iv(currentLine[i], 0);
				setContinousTexCoord(gl, x + i, y + 1);
				gl.glVertex3iv(nextLine[i + nextOffset], 0);
				setContinousTexCoord(gl, x + i + 1, y + 1);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (current == bottomright) {
				getBorder(current, bottomleft).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordTopRight(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordCenter(gl);
				gl.glVertex3iv(nextLine[i + nextOffset], 0);
				setTexCoordRight(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (current == bottomleft) {
				getBorder(current, bottomright).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordTopLeft(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordLeft(gl);
				gl.glVertex3iv(nextLine[i + nextOffset], 0);
				setTexCoordCenter(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (bottomleft == bottomright) {
				getBorder(bottomleft, current).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordCenter(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordBottomLeft(gl);
				gl.glVertex3iv(nextLine[i + nextOffset], 0);
				setTexCoordBottomRight(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} // TODO: draw tiles with 3 different borders.

			// right triangle that faces down
			if (current == bottomright && current == right) {
				getContinousImage(current).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setContinousTexCoord(gl, x + i, y);
				gl.glVertex3iv(currentLine[i], 0);
				setContinousTexCoord(gl, x + i + 1, y);
				gl.glVertex3iv(currentLine[i + 1], 0);
				setContinousTexCoord(gl, x + i + 1, y + 1);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (current == bottomright) {
				getBorder(current, right).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordLeft(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordCenter(gl);
				gl.glVertex3iv(currentLine[i + 1], 0);
				setTexCoordBottomLeft(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (current == right) {
				getBorder(current, bottomright).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordTopLeft(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordTopRight(gl);
				gl.glVertex3iv(currentLine[i + 1], 0);
				setTexCoordCenter(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} else if (right == bottomright) {
				getBorder(right, current).bind(gl);

				gl.glBegin(GL.GL_TRIANGLES);
				setTexCoordCenter(gl);
				gl.glVertex3iv(currentLine[i], 0);
				setTexCoordRight(gl);
				gl.glVertex3iv(currentLine[i + 1], 0);
				setTexCoordBottomRight(gl);
				gl.glVertex3iv(nextLine[i + 1 + nextOffset], 0);
				gl.glEnd();
			} // TODO: draw tiles with 3 different borders.

		}

	}

	private Image getContinousImage(ELandscapeType type) {
		return this.imageProvider.getLandscapeImage(LAND_FILE,
		        type.getImageNumber());
	}

	/**
	 * Sets a text coordinate for continuous mapping.
	 * 
	 * @param gl
	 * @param x
	 *            The tile x position
	 * @param y
	 *            The tile y position
	 */
	private void setContinousTexCoord(GL2 gl, float tilex, int tiley) {
		float x = (tilex - tiley / 2.0f) * IHexTile.X_DISTANCE / CONTINOUS_SIZE;
		float y = tiley * (float) IHexTile.Y_DISTANCE / CONTINOUS_SIZE;
		gl.glTexCoord2f(x, y);
	}

	/**
	 * Sets the tex coord to the center of a hex-part texture.
	 */
	private void setTexCoordCenter(GL2 gl) {
		gl.glTexCoord2f(0.5f, .5f);
	}

	/**
	 * Sets the tex coord to the center of a hex-part texture.
	 */
	private void setTexCoordTopLeft(GL2 gl) {
		gl.glTexCoord2f(.25f, 0);
	}

	/**
	 * Sets the tex coord to the top right of a hex-part texture.
	 */
	private void setTexCoordTopRight(GL2 gl) {
		gl.glTexCoord2f(.75f, 0);
	}

	/**
	 * Sets the tex coord to the right of a hex-part texture.
	 */
	private void setTexCoordRight(GL2 gl) {
		gl.glTexCoord2f(1, 0.5f);
	}

	/**
	 * Sets the tex coord to the bottom right of a hex-part texture.
	 */
	private void setTexCoordBottomRight(GL2 gl) {
		gl.glTexCoord2f(0.75f, 1);
	}

	/**
	 * Sets the tex coord to the bottom left of a hex-part texture.
	 */
	private void setTexCoordBottomLeft(GL2 gl) {
		gl.glTexCoord2f(0.25f, 1);
	}

	/**
	 * Sets the tex coord to the left of a hex-part texture.
	 */
	private void setTexCoordLeft(GL2 gl) {
		gl.glTexCoord2f(0, 0.5f);
	}

	/**
	 * Loads the landscape array with the landscape types or <code>null</code>.<br>
	 * Loads the position array with the position (for valid tiles only).
	 * 
	 * @param context
	 * @param offsetx
	 * @param y
	 * @param line
	 * @param landscapes
	 */
	private void loadLine(MapDrawContext context, short offsetx, short y,
	        int[][] line, ELandscapeType[] landscapes) {
		assert landscapes.length == line.length;

		for (short i = 0; i < landscapes.length; i++) {
			IHexTile tile = context.getTile((short) (offsetx + i), y);

			if (tile == null) {
				landscapes[i] = null;
			} else {
				landscapes[i] = tile.getLandscapeType();
				line[i][0] = tile.getX();
				line[i][1] = tile.getY();
				line[i][2] = tile.getHeight();
			}
		}
	}
}
