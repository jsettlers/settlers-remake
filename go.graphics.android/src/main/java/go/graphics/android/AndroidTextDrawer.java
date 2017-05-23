/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package go.graphics.android;

import java.nio.ByteBuffer;
import java.util.Arrays;

import go.graphics.TextureHandle;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.TextView;

public class AndroidTextDrawer implements TextDrawer {

	private static final int TEXTURE_HEIGHT = 512;
	private static final int TEXTURE_WIDTH = 512;

	private static AndroidTextDrawer[] instances = new AndroidTextDrawer[EFontSize.values().length];

	private final EFontSize size;
	private final AndroidContext context;
	private TextureHandle texture = null;
	/**
	 * The number of lines we use on our texture.
	 */
	private int lines;
	/**
	 * The current string starting in line i.
	 * <p>
	 */
	private String[] linestrings;

	/**
	 * The width of line i. This width can be higher than TEXTURE_WIDTH. Then the string is split to multiple lines.
	 */
	private int[] linewidths;

	/**
	 * An index of the next tile if the width of the current line is bigger than TEXTURE_WIDTH. This forms an linked list. -1 means no next tile.
	 */
	private int[] nextTile;

	private int lineheight;

	/**
	 * Data to do LRU
	 */
	private int lastUsedCount = 0;
	private int[] lastused;

	private TextView renderer;
	private float pixelScale;

	private float[] texturepos = {
			// top left
			0,
			0,
			0,
			0,
			0,

			// bottom left
			0,
			0,
			0,
			0,
			0,

			// bottom right
			TEXTURE_WIDTH,
			0,
			0,
			1,
			0,

			// top right
			TEXTURE_WIDTH,
			0,
			0,
			1,
			0,
	};

	private AndroidTextDrawer(EFontSize size, AndroidContext context) {
		this.size = size;
		this.context = context;
		pixelScale = context.getAndroidContext().getResources().getDisplayMetrics().scaledDensity;
	}

	private void checkInvariants() {
		boolean[] isNextTile = new boolean[lines];
		for (int i = 0; i < lines; i++) {
			int next = nextTile[i];
			if (next >= 0) {
				if (isNextTile[next]) {
					System.err.println("WARNING: The line " + next + " is linked multiple times as next line.");
				}
				isNextTile[next] = true;
			}
		}
		for (int i = 0; i < lines; i++) {
			if (isNextTile[i]) {
				if (linestrings[i] != null) {
					System.out.println("Linestring should be null for line " + i);
				}
				if (lastused[i] != Integer.MAX_VALUE) {
					System.out.println("Last used should not be set for line " + i);
				}
			}
		}
	}

	@Override
	public void renderCentered(float cx, float cy, String text) {
		// TODO: we may want to optimize this.
		drawString(cx - getWidth(text) / 2, cy - getHeight(text) / 2, text);
	}

	@Override
	public void drawString(float x, float y, String string) {
		initialize();

		int line = findLineFor(string);

		for (; line >= 0; line = nextTile[line], x += TEXTURE_WIDTH) {
			// texture mirrored
			float bottom = (float) ((line + 1) * lineheight) / TEXTURE_HEIGHT;
			float top = (float) (line * lineheight) / TEXTURE_HEIGHT;
			texturepos[4] = top;
			texturepos[9] = bottom;
			texturepos[14] = bottom;
			texturepos[19] = top;

			context.glPushMatrix();
			context.glTranslatef(x, y, 0);
			context.drawQuadWithTexture(texture, texturepos);
			context.glPopMatrix();
		}
	}

	private int findExistingString(String string) {
		int length = lines;
		for (int i = 0; i < length; i++) {
			if (string.equals(linestrings[i])) {
				lastused[i] = lastUsedCount++;
				return i;
			}
		}
		return -1;
	}

	private int findLineToUse() {
		int unnededline = 0;
		int unnededrating = Integer.MAX_VALUE;

		for (int i = 0; i < lines; i++) {
			if (lastused[i] < unnededrating) {
				unnededline = i;
				unnededrating = lastused[i];
			}
		}

		// now free the next lines
		for (int next = unnededline; next > -1; next = nextTile[next]) {
			nextTile[next] = -1;
			lastused[next] = 0;
			linestrings[next] = null;
		}

		return unnededline;
	}

	private int findLineFor(String string) {
		int line = findExistingString(string);
		if (line >= 0) {
			return line;
		}

		int width = (int) Math.ceil(computeWidth(string) + 25);
		renderer = new TextView(context.getAndroidContext());
		renderer.setTextColor(Color.WHITE);
		renderer.setSingleLine(true);
		renderer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getScaledSize());
		renderer.setText(string);

		int firstLine = findLineToUse();
		// System.out.println("string cache miss for " + string +
		// ", allocating new line: " + firstLine);
		int lastLine = firstLine;

		for (int x = 0; x < width; x += TEXTURE_WIDTH) {
			if (x == 0) {
				line = firstLine;
			} else {
				line = findLineToUse();
				nextTile[lastLine] = line;
				linestrings[line] = null;
				linewidths[line] = -1;
			}
			// important to not allow cycles.
			lastused[line] = Integer.MAX_VALUE;
			// just to be sure.
			nextTile[line] = -1;

			// render the new text to that line.
			Bitmap bitmap = Bitmap.createBitmap(TEXTURE_WIDTH, lineheight, Bitmap.Config.ALPHA_8);
			Canvas canvas = new Canvas(bitmap);
			renderer.layout(0, 0, width, lineheight);
			canvas.translate(-x, 0);
			renderer.draw(canvas);
			// canvas.translate(50, .8f * lineheight);
			ByteBuffer dst = ByteBuffer.allocateDirect(lineheight * TEXTURE_WIDTH);
			bitmap.copyPixelsToBuffer(dst);
			dst.rewind();
			context.updateTextureAlpha(texture, 0, line * lineheight,
					TEXTURE_WIDTH, lineheight, dst);
			lastLine = line;
		}
		lastused[firstLine] = lastUsedCount++;
		linestrings[firstLine] = string;
		linewidths[firstLine] = width;

		checkInvariants();
		return firstLine;
	}

	private void initialize() {
		if (texture == null || !texture.isValid()) {
			texture = context.generateTextureAlpha(TEXTURE_WIDTH, TEXTURE_HEIGHT);
			lineheight = (int) (getScaledSize() * 1.3);
			lines = TEXTURE_HEIGHT / lineheight;
			linestrings = new String[lines];
			linewidths = new int[lines];
			lastused = new int[lines];
			nextTile = new int[lines];
			Arrays.fill(nextTile, -1);

			texturepos[1] = lineheight;
			texturepos[16] = lineheight;

		}
	}

	@Override
	public float getWidth(String string) {
		int index = findExistingString(string);
		if (index < 0) {
			return computeWidth(string);
		} else {
			return linewidths[index];
		}
	}

	private float computeWidth(String string) {
		Paint paint = new Paint();
		paint.setTextSize(getScaledSize());
		return paint.measureText(string);
	}

	@Override
	public float getHeight(String string) {
		return getScaledSize();
	}

	private float getScaledSize() {
		return size.getSize() * pixelScale;
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		context.color(red, green, blue, alpha);
	}

	public static TextDrawer getInstance(EFontSize size, AndroidContext context) {
		int ordinal = size.ordinal();
		if (instances[ordinal] == null) {
			instances[ordinal] = new AndroidTextDrawer(size, context);
		}
		return instances[ordinal];
	}

	public static void invalidateTextures() {
		for (int i = 0; i < instances.length; i++) {
			instances[i] = null;
		}
	}

}
