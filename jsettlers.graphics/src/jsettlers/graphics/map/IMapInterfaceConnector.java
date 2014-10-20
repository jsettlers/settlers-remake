package jsettlers.graphics.map;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;

/**
 * @author Andreas Eberle
 */
public interface IMapInterfaceConnector extends IMessenger {

	UIState getUIState();

	void loadUIState(UIState uiStateData);

	void addListener(IMapInterfaceListener listener);

	void removeListener(IMapInterfaceListener guiInterface);

	void setPreviewBuildingType(EBuildingType buildingType);

	void scrollTo(ShortPoint2D point, boolean mark);

	void setSelection(ISelectionSet selection);

	void shutdown();

}
