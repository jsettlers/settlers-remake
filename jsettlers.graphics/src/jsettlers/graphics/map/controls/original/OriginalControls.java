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
package jsettlers.graphics.map.controls.original;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.map.controls.original.panel.TabButton;
import jsettlers.graphics.map.controls.original.panel.content.BearerSelection;
import jsettlers.graphics.map.controls.original.panel.content.BuildingSelectionContent;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.map.controls.original.panel.content.SoilderSelection;
import jsettlers.graphics.map.controls.original.panel.content.SpecialistSelection;
import jsettlers.graphics.map.minimap.Minimap;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

public class OriginalControls implements IControls {
	private UIPanel uiBase;

	private Minimap minimap;
	private final Button btnABC = new TabButton(EContentType.EMPTY, MainPanel.BUTTONS_FILE, 318, 321, "");
	private final Button btnScroll = new TabButton(EContentType.EMPTY, MainPanel.BUTTONS_FILE, 342, 348, ""); // 345 - image for 3rd setting.
	private final Button btnSettlers = new TabButton(EContentType.EMPTY, MainPanel.BUTTONS_FILE, 351, 354, ""); // 357 - image for 3rd setting (show
																												// soldiers?).
	private final Button btnBuildings = new TabButton(EContentType.EMPTY, MainPanel.BUTTONS_FILE, 360, 367, "");

	private final MainPanel mainPanel;

	private ControlPanelLayoutProperties layoutProperties;

	boolean lastSelectionWasNull = true;

	private MapDrawContext context;

	public OriginalControls(ActionFireable actionFireable) {
		layoutProperties = ControlPanelLayoutProperties.getLayoutPropertiesFor(480);
		mainPanel = new MainPanel(actionFireable);
		uiBase = createInterface();
		mainPanel.layoutPanel(layoutProperties);
	}

	private UIPanel createInterface() {
		UIPanel panel = new UIPanel();

		addMiniMap(panel);

		mainPanel.setBackground(layoutProperties.UI_BG_SEQ_MAIN);
		panel.addChild(mainPanel, 0, 0, layoutProperties.UI_CENTER_X, layoutProperties.UI_CENTER_Y);

		UIPanel rightDecoration = new UIPanel();
		rightDecoration.setBackground(layoutProperties.UI_BG_SEQ_RIGHT);
		panel.addChild(rightDecoration, layoutProperties.UI_CENTER_X, 0, layoutProperties.UI_DECORATIONRIGHT, layoutProperties.UI_CENTER_Y);

		return panel;
	}

	private void addMiniMap(UIPanel panel)
	{
		UIPanel minimapbg_left = new UIPanel();
		minimapbg_left.setBackground(layoutProperties.UI_BG_SEQ_MINIMAPL);
		minimapbg_left.addChild(
				btnABC,
				layoutProperties.MINIMAP_BUTTON_ABC_LEFT,
				layoutProperties.MINIMAP_BUTTON_ABC_TOP - layoutProperties.MINIMAP_BUTTON_HEIGHT,
				layoutProperties.MINIMAP_BUTTON_ABC_LEFT + layoutProperties.MINIMAP_BUTTON_WIDTH,
				layoutProperties.MINIMAP_BUTTON_ABC_TOP
				);
		minimapbg_left.addChild(
				btnScroll,
				layoutProperties.MINIMAP_BUTTON_SCROLL_LEFT,
				layoutProperties.MINIMAP_BUTTON_SCROLL_TOP - layoutProperties.MINIMAP_BUTTON_HEIGHT,
				layoutProperties.MINIMAP_BUTTON_SCROLL_LEFT + layoutProperties.MINIMAP_BUTTON_WIDTH,
				layoutProperties.MINIMAP_BUTTON_SCROLL_TOP
				);
		minimapbg_left.addChild(
				btnSettlers,
				layoutProperties.MINIMAP_BUTTON_SETTLERS_LEFT,
				layoutProperties.MINIMAP_BUTTON_SETTLERS_TOP - layoutProperties.MINIMAP_BUTTON_HEIGHT,
				layoutProperties.MINIMAP_BUTTON_SETTLERS_LEFT + layoutProperties.MINIMAP_BUTTON_WIDTH,
				layoutProperties.MINIMAP_BUTTON_SETTLERS_TOP
				);
		minimapbg_left.addChild(
				btnBuildings,
				layoutProperties.MINIMAP_BUTTON_BUILDINGS_LEFT,
				layoutProperties.MINIMAP_BUTTON_BUILDINGS_TOP - layoutProperties.MINIMAP_BUTTON_HEIGHT,
				layoutProperties.MINIMAP_BUTTON_BUILDINGS_LEFT + layoutProperties.MINIMAP_BUTTON_WIDTH,
				layoutProperties.MINIMAP_BUTTON_BUILDINGS_TOP
				);

		UIPanel minimapbg_right = new UIPanel();
		minimapbg_right.setBackground(layoutProperties.UI_BG_SEQ_MINIMAPR);

		panel.addChild(minimapbg_left, 0, layoutProperties.UI_CENTER_Y, layoutProperties.UI_CENTER_X, 1);
		panel.addChild(minimapbg_right, layoutProperties.UI_CENTER_X, layoutProperties.UI_CENTER_Y, layoutProperties.UI_MINIMAP_DECORATORRIGHT, 1);
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		ControlPanelLayoutProperties newConstants = ControlPanelLayoutProperties.getLayoutPropertiesFor(newHeight);
		if (!newConstants.equals(layoutProperties)) {
			layoutProperties = newConstants;

			uiBase = createInterface();
			mainPanel.layoutPanel(layoutProperties);
		}
		int width = (int) (newHeight / layoutProperties.UI_RATIO);
		this.uiBase.setPosition(new FloatRectangle(0, 0, width, newHeight));

		minimap.setSize(
				(int) (layoutProperties.MINIMAP_WIDTH * width),
				(int) (layoutProperties.MINIMAP_HEIGHT * (1 - layoutProperties.UI_CENTER_Y) * newHeight));
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		if (minimap != null) {
			gl.glPushMatrix();
			gl.glTranslatef(getMinimapLeft(), getMinimapBottom(), 0);
			minimap.draw(gl);
			gl.glPopMatrix();
		}
		uiBase.drawAt(gl); // Frame decoration should be drawn after and over the edges of the minimap.
	}

	private float getMinimapLeft() {
		return layoutProperties.MINIMAP_BOTTOMLEFT_X * uiBase.getPosition().getWidth();
	}

	private float getMinimapBottom() {
		return (layoutProperties.UI_CENTER_Y + (1 - layoutProperties.UI_CENTER_Y) * layoutProperties.MINIMAP_BOTTOM_Y)
				* uiBase.getPosition().getHeight();
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		float width = uiBase.getPosition().getWidth();
		float uicenter = width * layoutProperties.UI_CENTER_X;
		return position.getX() < Math.max(uicenter, getMinimapOffset(position.getY()) + layoutProperties.UI_CENTER_X * width);
	}

	/**
	 * Gets the X-Offset of the minimap at the given y position.
	 * 
	 * @param y
	 * @return
	 */
	private double getMinimapOffset(double y) {
		float width = uiBase.getPosition().getWidth();
		float height = uiBase.getPosition().getHeight();
		float m =
				1 / (1 - layoutProperties.UI_CENTER_X) / width
						* (1 - layoutProperties.UI_CENTER_Y) * height;
		return y / m - layoutProperties.UI_CENTER_Y
				* height / m;
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		float relativex =
				(float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
				(float) position.getY() / this.uiBase.getPosition().getHeight();
		Action action;
		if (minimap != null && relativey > layoutProperties.UI_CENTER_Y && getMinimapOffset(position.getY()) < position.getX()) {
			action = getForMinimap(relativex, relativey, selecting);
			startMapPosition = null; // to prevent it from jumping back.
		} else {
			action = uiBase.getAction(relativex, relativey);
		}
		if (action != null
				&& action.getActionType() == EActionType.CHANGE_PANEL) {
			mainPanel.setContent(((ChangePanelAction) action).getContent());
			return null;
		} else {
			return action;
		}
	}

	private Action getForMinimap(float relativex, float relativey,
			boolean selecting) {
		float minimapx =
				(relativex - layoutProperties.MINIMAP_BOTTOMLEFT_X)
						/ layoutProperties.MINIMAP_WIDTH;
		float minimapy =
				((relativey - layoutProperties.UI_CENTER_Y)
						/ (1 - layoutProperties.UI_CENTER_Y) - layoutProperties.MINIMAP_BOTTOM_Y)
						/ layoutProperties.MINIMAP_HEIGHT;
		ShortPoint2D clickPosition =
				minimap.getClickPositionIfOnMap(minimapx, minimapy);
		if (clickPosition != null) {
			if (selecting) {
				return new PointAction(EActionType.PAN_TO, clickPosition);
			} else {
				return new PointAction(EActionType.MOVE_TO, clickPosition);
			}
		} else {
			return null;
		}
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		float relativex =
				(float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
				(float) position.getY() / this.uiBase.getPosition().getHeight();
		return uiBase.getDescription(relativex, relativey);
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
		if (minimap != null) {
			minimap.setMapViewport(screenArea);
		}
		if (context != null) {
			mainPanel.setMapViewport(screenArea, context.getMap());
		}
	}

	@Override
	public void action(Action action) {
	}

	private ShortPoint2D startMapPosition;

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		if (!containsPoint(event.getDrawPosition())) {
			return false;
		}

		Action action = getActionForDraw(event);
		if (action != null && context != null && minimap != null) {
			float y = context.getScreen().getHeight() / 2;
			float x = context.getScreen().getWidth() / 2;
			startMapPosition = context.getPositionOnScreen(x, y);
			event.setHandler(new DrawMinimapHandler());
			return true;
		} else {
			return false;
		}
	}

	private Action getActionForDraw(GODrawEvent event) {
		UIPoint position = event.getDrawPosition();

		float width = this.uiBase.getPosition().getWidth();
		float relativex = (float) position.getX() / width;
		float height = this.uiBase.getPosition().getHeight();
		float relativey = (float) position.getY() / height;

		return getForMinimap(relativex, relativey, true);
	}

	private class DrawMinimapHandler implements GOModalEventHandler {
		@Override
		public void phaseChanged(GOEvent event) {
		}

		@Override
		public void finished(GOEvent event) {
			eventDataChanged(event);
		}

		@Override
		public void aborted(GOEvent event) {
			if (startMapPosition != null) {
				minimap.getContext().scrollTo(startMapPosition);
			}
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			Action action = getActionForDraw((GODrawEvent) event);
			if (action != null && action.getActionType() == EActionType.PAN_TO) {
				minimap.getContext().scrollTo(
						((PointAction) action).getPosition());
			}
		}

	}

	@Override
	public void displaySelection(ISelectionSet selection) {
		if (selection == null || selection.getSize() == 0) {
			if (!lastSelectionWasNull) {
				lastSelectionWasNull = true;
				mainPanel.setContent(EContentType.EMPTY);
			}// else: nothing to do
		} else {
			lastSelectionWasNull = false;

			switch (selection.getSelectionType()) {
			case PEOPLE:
				mainPanel.setContent(new BearerSelection(selection));
				break;
			case SOLDIERS:
				mainPanel.setContent(new SoilderSelection(selection));
				break;
			case SPECIALISTS:
				mainPanel.setContent(new SpecialistSelection(selection));
				break;

			case BUILDING:
				mainPanel.setContent(new BuildingSelectionContent(selection));
				break;
			default:
				System.err
						.println("got Selection but couldn't handle it! SelectionType: "
								+ selection.getSelectionType());
				break;
			}
		}
	}

	@Override
	public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {
		this.context = context;
		this.minimap = new Minimap(context);
	}

	@Override
	public Action replaceAction(Action action) {
		return mainPanel.catchAction(action);
	}

	@Override
	public String getMapTooltip(ShortPoint2D point) {
		return null;
	}

	@Override
	public void stop() {
		minimap.stop();
	}
}
