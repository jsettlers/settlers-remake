package jsettlers.graphics.progress;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.event.GOEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.util.LinkedList;

import jsettlers.graphics.SettlersContent;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;

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
		gl.glPushMatrix();
		
		
		SingleImage image = provider.getGuiImage(2, 29);
		gl.glScalef((float) width / image.getWidth(),
		        (float) height / image.getHeight(), 0);
		image.drawAt(gl, 0, image.getHeight());

		gl.glPopMatrix();

		TextDrawer drawer = gl.getTextDrawer(EFontSize.HEADLINE);

		String text = Labels.getProgress(state);
		drawer.renderCentered(width / 2, 40, text);
	}

	/**
	 * Sets the state to display
	 * 
	 * @param state
	 *            A valid EProgressState
	 * @param progress 
	 */
	public void setProgressState(EProgressState state, float progress) {
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
