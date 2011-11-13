package go.graphics.android;

import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

public class AndroidTextDrawer implements TextDrawer {

	public static TextDrawer getInstance(EFontSize size) {
	    return new AndroidTextDrawer();
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
	    return 20;
    }

	@Override
    public double getHeight(String string) {
	    // TODO Auto-generated method stub
	    return 20;
    }

	@Override
    public void setColor(float red, float green, float blue, float alpha) {
	    // TODO Auto-generated method stub
	    
    }

}
