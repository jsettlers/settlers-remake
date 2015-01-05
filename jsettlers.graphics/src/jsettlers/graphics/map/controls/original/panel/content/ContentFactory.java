package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.utils.UIPanel;

public interface ContentFactory {

	UIPanel getPanel();

	void displayBuildingBuild(EBuildingType type);

	void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid);
}
