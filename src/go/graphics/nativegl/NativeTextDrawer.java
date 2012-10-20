package go.graphics.nativegl;

import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class NativeTextDrawer implements TextDrawer {

	public NativeTextDrawer(EFontSize size) {
	    // TODO Auto-generated constructor stub
    }

	@Override
	public void renderCentered(float cx, float cy, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(float x, float y, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getWidth(String string) {
		// TODO Auto-generated method stub
		return string.length();
	}

	@Override
	public double getHeight(String string) {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		// TODO Auto-generated method stub

	}

}
