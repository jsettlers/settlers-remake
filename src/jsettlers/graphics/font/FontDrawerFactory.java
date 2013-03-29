package jsettlers.graphics.font;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.DirectImageLink;
import jsettlers.graphics.map.IGLProvider;
import jsettlers.graphics.map.draw.DrawBuffer;

public class FontDrawerFactory implements ITextDrawerFactory, IGLProvider {
	private static final DirectImageLink TEXTURE = new DirectImageLink("font.0");
	
	private GLDrawContext lastGl = null;
	private FontDrawer[] cache;
	private final DrawBuffer buffer = new DrawBuffer(this);

	@Override
	public TextDrawer getTextDrawer(GLDrawContext gl, EFontSize size) {
		if (gl != lastGl) {
			lastGl = gl;
			cache = new FontDrawer[EFontSize.values().length];
		}
		
		FontDrawer drawer = cache[size.ordinal()];
		if (drawer == null) {
			drawer = new FontDrawer(gl, buffer, size);
			cache[size.ordinal()] = drawer;
		}
		return drawer;
	}

	@Override
    public GLDrawContext getGl() {
	    return lastGl;
    }
}
