package jsettlers.graphics.map;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.font.ITextDrawerFactory;

/**
 * This is a text drawer that uses the text drawer provided by opengl or the
 * provided text drawer if one is given.
 * 
 * @author michael
 */
public class ReplaceableTextDrawer implements ITextDrawerFactory {

	private ITextDrawerFactory drawerFactory;

	/**
	 * Sets the factory to use to generate text drawer if needed.
	 * 
	 * @param drawerFactory
	 *            The factory to use. <code>null</code> to use the default.
	 */
	public void setTextDrawerFactory(ITextDrawerFactory drawerFactory) {
		this.drawerFactory = drawerFactory;
	}

	/**
	 * Gets the text drawer to use.
	 * 
	 * @param size
	 * @return
	 */
	@Override
	public TextDrawer getTextDrawer(GLDrawContext gl, EFontSize size) {
		return drawerFactory != null ? drawerFactory.getTextDrawer(gl, size) : gl.getTextDrawer(size);
	}
	
}
