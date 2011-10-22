package go.graphics.android;

import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class AndroidTextDrawer implements TextDrawer {

	public static TextDrawer getInstance(EFontSize size) {
	    return new AndroidTextDrawer();
    }

	@Override
    public void renderCentered(int cx, int cy, String text) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void drawString(int x, int y, String string) {
	    // TODO Auto-generated method stub
	    
    }

}
