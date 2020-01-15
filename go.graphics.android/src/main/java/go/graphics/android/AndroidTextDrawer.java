package go.graphics.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import go.graphics.text.AbstractTextDrawer;
import go.graphics.text.EFontSize;

import static android.opengl.GLES20.*;

public class AndroidTextDrawer extends AbstractTextDrawer<GLESDrawContext> {

	public AndroidTextDrawer(GLESDrawContext gl) {
		super(gl, 0);
	}

	@Override
	protected float calculateScalingFactor() {
		return drawContext.getAndroidContext().getResources().getDisplayMetrics().density;
	}

	private Paint paint;

	@Override
	protected int init() {
		paint = new Paint();
		paint.setTextSize(TEXTURE_GENERATION_SIZE);

		float[] float_char_widths = new float[CHARACTER_COUNT];
		paint.getTextWidths(CHARACTERS, float_char_widths);
		for(int i = 0;i != CHARACTER_COUNT; i++) char_widths[i] = (int)float_char_widths[i];

		Paint.FontMetricsInt fm = paint.getFontMetricsInt();
		gentex_line_height = fm.leading-fm.ascent+fm.descent;


		Paint sizedFont = new Paint(paint);

		EFontSize[] values = EFontSize.values();
		for(int i = 0; i != values.length; i++) {
			sizedFont.setTextSize(values[i].getSize());

			Paint.FontMetricsInt sized_fm = sizedFont.getFontMetricsInt();

			heightPerSize[i] = (sized_fm.leading-sized_fm.ascent+sized_fm.descent);
		}

		return fm.descent;
	}

	private Bitmap pre_render;
	private Canvas canvas;

	@Override
	protected void setupBitmapDraw() {
		pre_render = Bitmap.createBitmap(tex_width, tex_height, Bitmap.Config.ALPHA_8);
		canvas = new Canvas(pre_render);
		paint.setColor(0);
		canvas.drawPaint(paint);
		paint.setColor(0xFFFFFFFF);
	}

	@Override
	protected void drawChar(char[] character, int x, int y) {
		canvas.drawText(character, 0, 1, x, y, paint);
	}

	@Override
	protected int[] getRGB() {
		int[] pixels = new int[tex_width*tex_height];
		pre_render.getPixels(pixels, 0, tex_width, 0, 0, tex_width, tex_height);
		return pixels;
	}

	@Override
	protected void endDraw() {
		pre_render = null;
		canvas = null;
	}
}
