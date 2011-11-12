package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.utils.UIPanel;

public interface ContentFactory {

	UIPanel getPanel();

	IContextListener getContextListener();
	
}
