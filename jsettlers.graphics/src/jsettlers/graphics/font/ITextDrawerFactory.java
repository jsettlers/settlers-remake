package jsettlers.graphics.font;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

/**
 * Classes of this interface let you create a text drawer.
 * 
 * @author michael
 *
 */
public interface ITextDrawerFactory {

	TextDrawer getTextDrawer(GLDrawContext gl, EFontSize size);

}
