/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.mapcreator.main.map;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.IAction;
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
	public void action(IAction action) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		return false;
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		return "";
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
	public void displaySelection(ISelectionSet selection) {
	}

	@Override
	public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {
		this.context = context;
	}

	@Override
	public IAction replaceAction(IAction action) {
		return action;
	}

	@Override
	public String getMapTooltip(ShortPoint2D point) {
		return point.toString();
	}

	@Override
	public void stop() {
		/* we ignore this. stop means exit... */
	}

}
