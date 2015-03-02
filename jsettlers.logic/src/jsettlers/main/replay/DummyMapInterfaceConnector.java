package jsettlers.main.replay;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.messages.Message;

class DummyMapInterfaceConnector implements IMapInterfaceConnector {

	private UIState uiState;

	public DummyMapInterfaceConnector() {
		uiState = new UIState(new ShortPoint2D(0, 0));
	}

	@Override
	public void showMessage(Message message) {
	}

	@Override
	public UIState getUIState() {
		return uiState;
	}

	@Override
	public void loadUIState(UIState uiStateData) {
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
