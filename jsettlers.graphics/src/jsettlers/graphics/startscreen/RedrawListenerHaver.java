package jsettlers.graphics.startscreen;

import go.graphics.RedrawListener;

import java.util.ArrayList;

public class RedrawListenerHaver implements RedrawListener {
	
	private ArrayList<RedrawListener> listeners = new ArrayList<RedrawListener>();

	public void addRedrawListener(RedrawListener l) {
		listeners.add(l);
	}

	public void removeRedrawListener(RedrawListener l) {
		listeners.remove(l);
	}
	
	@Override
    public void requestRedraw() {
		for (RedrawListener l : listeners) {
			l.requestRedraw();
		}
	}

}
