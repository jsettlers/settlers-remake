package jsettlers.graphics.progress;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.event.GOEvent;

import java.util.LinkedList;

import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.utils.EFontSize;
import jsettlers.graphics.utils.TextDrawer;

/**
 * This content displays progress information for wait screens.
 * 
 * @author michael
 */
public class ProgressContent implements SettlersContent {

	private final ImageProvider provider = ImageProvider.getInstance();
	private EProgressState state = EProgressState.LOADING;
	private LinkedList<RedrawListener> listeners =
	        new LinkedList<RedrawListener>();

	@Override
	public void drawContent(GLDrawContext gl, int width, int height) {
		provider.preload(2);
		provider.waitForPreload(2);

		gl.glPushMatrix();

		Image image = provider.getGuiImage(2, 29);
		gl.glScalef((float) width / image.getWidth(),
		        (float) height / image.getHeight(), 0);
		image.drawAt(gl, 0, image.getHeight());

		gl.glPopMatrix();

		TextDrawer drawer = TextDrawer.getTextDrawer(EFontSize.HEADLINE);

		String text = Labels.getProgress(state);
		drawer.renderCentered(width / 2, 40, text);
	}

	/**
	 * Sets the state to display
	 * 
	 * @param state
	 *            A valid EProgressState
	 */
	public void setProgressState(EProgressState state) {
		this.state = state;

		for (RedrawListener l : listeners) {
			l.requestRedraw();
		}
	}

	@Override
	public void addRedrawListener(RedrawListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeRedrawListener(RedrawListener l) {
		this.listeners.remove(l);
	}

	@Override
    public void handleEvent(GOEvent event) {
	    
    }

}
