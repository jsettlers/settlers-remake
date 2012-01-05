package go.graphics.android;

import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.TextView;

public class AndroidTextDrawer implements TextDrawer {

	private static final int TEXTURE_HEIGHT = 256;
	private static final int TEXTURE_WIDTH = 256;

	private static AndroidTextDrawer[] instances = new AndroidTextDrawer[EFontSize
	        .values().length];
	
	private final EFontSize size;
	private final AndroidContext context;
	private int texture = 0;
	private int lines;
	private String[] linestrings;
	private int lineheight;
	private int[] linewidths;
	private int lastUsedCount = 0;

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
	private int[] lastused;

	public AndroidTextDrawer(EFontSize size, AndroidContext context) {
		this.size = size;
		this.context = context;
	}

	@Override
	public void renderCentered(float cx, float cy, String text) {
		// TODO: we may want to optimize this.
		drawString(cx - (float) getWidth(text) / 2, cy
		        - (float) getHeight(text) / 2, text);
	}

	@Override
	public void drawString(float x, float y, String string) {
		initialize();

		int line = findLineFor(string);

		// texture mirrored
		float bottom = (float) ((line + 1) * lineheight) / TEXTURE_HEIGHT;
		float top = (float) (line * lineheight) / TEXTURE_HEIGHT;
		texturepos[4] = top;
		texturepos[9] = bottom;
		texturepos[14] = bottom;
		texturepos[19] = top;

		context.glPushMatrix();
		context.glTranslatef(x, y, 0);
		// context.color(1, 1, 0, 1);
		// context.drawQuadWithTexture(0, texturepos);
		context.color(1, 1, 1, 1);
		context.drawQuadWithTexture(texture, texturepos);
		context.glPopMatrix();
	}

	private int findLineFor(String string) {
		int unnededline = 0;
		int unnededrating = Integer.MAX_VALUE;
		int length = linestrings.length;
		for (int i = 0; i < length; i++) {
			if (string.equals(linestrings[i])) {
				lastused[i] = lastUsedCount++;
				return i;
			}
		}

		for (int i = 0; i < length; i++) {
			if (lastused[i] < unnededrating) {
				unnededline = i;
				unnededrating = lastused[i];
			}
		}
		
		//System.out.println("string cache miss for " + string + ", allocating new line: " + unnededline);

		// render the new text to that line.
		Bitmap bitmap =
		        Bitmap.createBitmap(TEXTURE_WIDTH, lineheight,
		                Bitmap.Config.ALPHA_8);
		Canvas canvas = new Canvas(bitmap);
		TextView renderer = new TextView(context.getAndroidContext());
		renderer.layout(0, 0, TEXTURE_WIDTH, lineheight);
		renderer.setText(string);
		renderer.setTextColor(Color.WHITE);
		renderer.setSingleLine(true);
		renderer.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.getSize());
		renderer.draw(canvas);
		canvas.translate(50, .8f * lineheight);
		ByteBuffer dst = ByteBuffer.allocateDirect(lineheight * TEXTURE_WIDTH);
		bitmap.copyPixelsToBuffer(dst);
		dst.rewind();

		context.updateTextureAlpha(texture, 0, unnededline * lineheight,
		        TEXTURE_WIDTH, lineheight, dst);
		linewidths[unnededline] = renderer.getMeasuredWidth();

		lastused[unnededline] = lastUsedCount++;
		linestrings[unnededline] = string;
		return unnededline;
	}

	private void initialize() {
		if (texture == 0) {
			texture =
			        context.generateTextureAlpha(TEXTURE_WIDTH, TEXTURE_HEIGHT);
			lineheight = (int) (size.getSize() * 1.3);
			lines = TEXTURE_HEIGHT / lineheight;
			linestrings = new String[lines];
			linewidths = new int[lines];
			lastused = new int[lines];

			texturepos[1] = lineheight;
			texturepos[16] = lineheight;
		}
	}

	@Override
	public double getWidth(String string) {
		Paint paint = new Paint();
		paint.setTextSize(size.getSize());
		return paint.measureText(string);
	}

	@Override
	public double getHeight(String string) {
		return size.getSize();
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		// TODO
	}

	public static TextDrawer getInstance(EFontSize size, AndroidContext context) {
		int ordinal = size.ordinal();
		if (instances[ordinal] == null) {
			instances[ordinal] = new AndroidTextDrawer(size, context);
		}
		return instances[ordinal];
	}

}
