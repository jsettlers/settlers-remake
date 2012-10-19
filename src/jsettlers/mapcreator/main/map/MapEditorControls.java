package jsettlers.mapcreator.main.map;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.mapcreator.main.action.DrawLineAction;
import jsettlers.mapcreator.main.action.EndDrawingAction;
import jsettlers.mapcreator.main.action.StartDrawingAction;

public class MapEditorControls implements IControls {

	private MapDrawContext context;
	private final ActionFireable firerer;

	private ShortPoint2D toMapPosition(UIPoint lastpoint) {
		return context.getPositionOnScreen((float) lastpoint.getX(), (float) lastpoint.getY());
	}

	private final class GOEventHandlerImplementation implements GOModalEventHandler {
		private ShortPoint2D last;
		private final double starty;

		public GOEventHandlerImplementation(UIPoint lastpoint) {
			last = toMapPosition(lastpoint);
			firerer.fireAction(new StartDrawingAction(last));
			starty = lastpoint.getY();
		}

		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			eventDataChanged(event);
			firerer.fireAction(new EndDrawingAction(last));
		}

		@Override
		public void aborted(GOEvent event) {
			finished(event);
			// firerer.fireAction(new AbortDrawingAction());
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			UIPoint pos = ((GODrawEvent) event).getDrawPosition();
			ShortPoint2D cur = toMapPosition(pos);
			firerer.fireAction(new DrawLineAction(last, cur, pos.getY() - starty));
			last = cur;
		}
	}

	public MapEditorControls(ActionFireable firerer) {
		this.firerer = firerer;
	}

	@Override
	public void action(Action action) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
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
		return toMapPosition(position).toString();
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
	}

	@Override
	public Action getActionFor(UIPoint position, boolean select) {
		return null;
	}

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		if (context != null) {
			event.setHandler(new GOEventHandlerImplementation(event.getDrawPosition()));
		}
		return true;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
	}

	@Override
	public void displaySelection(ISelectionSet selection) {
	}

	@Override
	public void setDrawContext(MapDrawContext context) {
		this.context = context;
	}

	@Override
	public Action replaceAction(Action action) {
		return action;
	}

	@Override
    public void stop() {
	    // TODO Auto-generated method stub
	    
    }

}
