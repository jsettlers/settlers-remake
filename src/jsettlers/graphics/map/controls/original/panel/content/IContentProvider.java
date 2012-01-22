package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.graphics.map.controls.original.panel.IContextListener;
import jsettlers.graphics.utils.UIPanel;

/**
 * Classes of this type provide content for the main panel.
 * 
 * @author michael
 */
public interface IContentProvider extends ContentFactory {
	public UIPanel getPanel();

	public IContextListener getContextListener();

	public ESecondaryTabType getTabs();

}
