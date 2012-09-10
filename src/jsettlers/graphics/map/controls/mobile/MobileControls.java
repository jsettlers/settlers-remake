package jsettlers.graphics.map.controls.mobile;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

public class MobileControls implements IControls {

	private final MainMenu mainMenu = new MainMenu();

	@Override
	public void action(Action action) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		mainMenu.drawAt(gl);
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		mainMenu.setScreenSize(newWidth, newHeight);
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		return mainMenu.containsPoint(position);
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
		// TODO Auto-generated method stub

	}

	@Override
	public Action getActionFor(UIPoint position) {
		if (mainMenu.containsPoint(position)) {
			return mainMenu.getActionFor(position);
		}
		return null;
	}

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displaySelection(ISelectionSet selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDrawContext(MapDrawContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public Action replaceAction(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
	    // TODO Auto-generated method stub

	}

}
