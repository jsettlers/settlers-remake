package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.utils.UIPanel;

public interface ContentFactory {

	UIPanel getPanel();

	/**
	 * Gets a listener that is notified of context changes. May be null.
	 * 
	 * @return
	 */
	IContextListener getContextListener();

}
