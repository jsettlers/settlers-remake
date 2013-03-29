package jsettlers.graphics.font;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.DirectImageLink;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.DrawBuffer;
import jsettlers.graphics.map.draw.ImageProvider;

/**
 * This is a special font drawer class. It draws fonts using our builtin font.
 * 
 * @author michael
 */
public class FontDrawer implements TextDrawer {
	private final EFontSize size;
	private final DrawBuffer drawBuffer;

	private static final String chars =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!.?,„“()+-%_ÄÖÜ ";
	private static final int CHARS_PER_ROW = 16;
	private static final int CHARS_PER_COLUMN = 16;
	private final GLDrawContext gl;

	public FontDrawer(GLDrawContext gl, DrawBuffer drawBuffer, EFontSize size) {
		this.gl = gl;
		this.drawBuffer = drawBuffer;
		this.size = size;
	}

	@Override
	public void renderCentered(float cx, float cy, String text) {
		drawString(cx - (float) getWidth(text) / 2, cy
		        - (float) getHeight(text) / 2, text);
	}

	private int getCharIndex(char c) {
		return chars.indexOf(Character.toUpperCase(c));
	}

	private float getWidth(int charIndex) {
		// TODO: Variable widths
		return charIndex >= 0 ? size.getSize() : 0;
	}

	@Override
	public void drawString(float x, float y, String string) {
		float cursorX = 0;
		float top = y + size.getSize();

		for (int i = 0; i < string.length(); i++) {
			int idx = getCharIndex(string.charAt(i));
			/*float w = getWidth(idx);
			float right = cursorX + w;
			float umin = (float) (idx & (CHARS_PER_ROW - 1)) / CHARS_PER_ROW;
			float umax = umin + (w / size.getSize() / CHARS_PER_ROW);
			float vmin = (float) (idx / CHARS_PER_ROW) / CHARS_PER_COLUMN;
			float vmax = vmin + (1.0f / CHARS_PER_COLUMN);

			drawBuffer.addImage(textureid, cursorX, y, right, top, umin, vmax,
			        umax, vmin, 0xffffffff);*/
			
			//TODO: Chars, color
			DirectImageLink texture = new DirectImageLink("font.0");
			Image image= ImageProvider.getInstance().getImage(texture);
			
			image.drawAt(gl, drawBuffer, cursorX, top, 0xffffffff);
			image.drawAt(gl, drawBuffer, 0, 0, 0xffffffff);
			cursorX+= image.getWidth();
		}
		
		drawBuffer.flush();
		System.out.println("Drawed " + string + " chars");
	}

	@Override
	public double getWidth(String string) {
		float w = 0;
		for (int i = 0; i < string.length(); i++) {
			int idx = getCharIndex(string.charAt(i));
			w += getWidth(idx);
		}
		return w;
	}

	@Override
	public double getHeight(String string) {
		return size.getSize();
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		// TODO Auto-generated method stub

	}
}
