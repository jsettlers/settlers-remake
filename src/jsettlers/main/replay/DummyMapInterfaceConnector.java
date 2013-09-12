package jsettlers.main.replay;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.UIStateData;
import jsettlers.graphics.messages.Message;

public class DummyMapInterfaceConnector implements IMapInterfaceConnector {

	private UIStateData uiState;

	public DummyMapInterfaceConnector() {
		uiState = new UIStateData(new ShortPoint2D(0, 0));
	}

	@Override
	public void showMessage(Message message) {
	}

	@Override
	public UIStateData getUIState() {
		return uiState;
	}

	@Override
	public void loadUIState(UIStateData uiStateData) {
		this.uiState = uiStateData;
	}

	@Override
	public void addListener(IMapInterfaceListener listener) {
	}

	@Override
	public void removeListener(IMapInterfaceListener guiInterface) {
	}

	@Override
	public void setPreviewBuildingType(EBuildingType buildingType) {
	}

	@Override
	public void scrollTo(ShortPoint2D point, boolean mark) {
	}

	@Override
	public void setSelection(ISelectionSet selection) {
	}

	@Override
	public void shutdown() {
	}

}
