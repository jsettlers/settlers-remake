package jsettlers.main.android.bg;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

public class BgControls implements IControls {

	private MapDrawContext context;
	private long startTime;

	@Override
	public void action(Action action) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		double x = (System.currentTimeMillis() - startTime) / 50.0;
		context.getScreen().setPanProgress(this, new UIPoint(-x, 0));
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		return true;
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		return null;
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		return new ExecutableAction() {
			@Override
			public void execute() {
			}
		};
	}

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		return true;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
	}

	@Override
	public void displaySelection(ISelectionSet selection) {
	}

	@Override
	public void setDrawContext(ActionFireable actionFireable,
	        MapDrawContext context) {
		this.context = context;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public Action replaceAction(Action action) {
		return null;
	}

	@Override
	public void stop() {
	}

	@Override
	public String getMapTooltip(ShortPoint2D point) {
		return null;
	}

}
