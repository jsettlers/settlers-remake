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
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.map.controls.original.panel.content.BearerSelection;
import jsettlers.graphics.map.controls.original.panel.content.BuildingSelectionPanel;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.map.controls.original.panel.content.MessageContent;
import jsettlers.graphics.map.controls.original.panel.content.SoilderSelection;
import jsettlers.graphics.map.controls.original.panel.content.SpecialistSelection;
import jsettlers.graphics.map.minimap.Minimap;
import jsettlers.graphics.map.minimap.Minimap.MapFeaturesViewMode;
import jsettlers.graphics.map.minimap.Minimap.SettlersViewMode;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

public class OriginalControls implements IControls {
    private ControlPanelLayoutProperties layoutProperties;
	private UIPanel uiBase;

	private Minimap minimap;
	private final Button btnChat;
	private final Button btnFeatures;
	private final Button btnSettlers;
	private final Button btnBuildings;

	private final MainPanel mainPanel = new MainPanel();

	boolean lastSelectionWasNull = true;

	private MapDrawContext context;

    public OriginalControls() {
        layoutProperties = ControlPanelLayoutProperties.getLayoutPropertiesFor(480);
        final MiniMapLayoutProperties miniMap = layoutProperties.miniMap;

        btnChat = new Button(
                new ExecutableAction() {
                    MessageContent messageContent =
                            new MessageContent(
                                    "This is not yet implemented.",
                                    "Cancel",
                                    new ExecutableAction() {
                                        @Override
                                        public void execute() {
                                            mainPanel.setContent(EContentType.BUILD_NORMAL);
                                            btnChat.setActive(false);
                                        }
                                    },
                                    "Ok",
                                    new ExecutableAction() {
                                        @Override
                                        public void execute() {
                                            mainPanel.setContent(EContentType.BUILD_NORMAL);
                                            btnChat.setActive(false);
                                        }
                                    }
                            );

                    @Override
                    public void execute() {
                        mainPanel.setContent(messageContent);
                        btnChat.setActive(true); // TODO needs to be unset when content changes.
                    }
                }, miniMap.IMAGELINK_BUTTON_CHAT_ACTIVE, miniMap.IMAGELINK_BUTTON_CHAT_INACTIVE, "");
        btnFeatures = new Button(
                new ExecutableAction() {
                    MapFeaturesViewMode mapFeaturesViewMode = MapFeaturesViewMode.SHOW;

                    @Override
                    public void execute() {
                        OriginalImageLink imageLink;
                        switch (mapFeaturesViewMode) {
                        default:
                        case HIDE:
                            imageLink = miniMap.IMAGELINK_BUTTON_FEATURES_SHOW;
                            mapFeaturesViewMode = MapFeaturesViewMode.SHOW;
                            break;
                        case SHOW:
                            imageLink = miniMap.IMAGELINK_BUTTON_FEATURES_BORDERFILL;
                            mapFeaturesViewMode = MapFeaturesViewMode.BORDERFILL;
                            break;
                        case BORDERFILL:
                            imageLink = miniMap.IMAGELINK_BUTTON_FEATURES_PLAIN;
                            mapFeaturesViewMode = MapFeaturesViewMode.HIDE;
                            break;
                        }
                        minimap.setMapFeaturesViewMode(mapFeaturesViewMode);
                        btnFeatures.setImage(imageLink);
                    }
                }, miniMap.IMAGELINK_BUTTON_FEATURES_SHOW, miniMap.IMAGELINK_BUTTON_FEATURES_PLAIN, "");
        btnSettlers = new Button(
                new ExecutableAction() {
                    SettlersViewMode settlersViewMode = SettlersViewMode.SETTLERS;

                    @Override
                    public void execute() {
                        OriginalImageLink imageLink;
                        switch (settlersViewMode) {
                        default:
                        case NONE:
                            imageLink = miniMap.IMAGELINK_BUTTON_SETTLERS_SHOW;
                            settlersViewMode = SettlersViewMode.SETTLERS;
                            break;
                        case SETTLERS:
                            imageLink = miniMap.IMAGELINK_BUTTON_SETTLERS_SOLDIERSONLY;
                            settlersViewMode = SettlersViewMode.SOLDIERS;
                            break;
                        case SOLDIERS:
                            imageLink = miniMap.IMAGELINK_BUTTON_SETTLERS_HIDE;
                            settlersViewMode = SettlersViewMode.NONE;
                            break;
                        }
                        minimap.setSettlersViewMode(settlersViewMode);
                        btnSettlers.setImage(imageLink);
                    }
                }, miniMap.IMAGELINK_BUTTON_SETTLERS_SHOW, miniMap.IMAGELINK_BUTTON_SETTLERS_HIDE, "");
        btnBuildings = new Button(
                new ExecutableAction() {
                    boolean showBuildings = false;

                    @Override
                    public void execute() {
                        // showBuildings = !showBuildings;
                        // minimap.setShowBuildings(showBuildings);
                        btnBuildings.setImage(showBuildings ? miniMap.IMAGELINK_BUTTON_BUILDINGS_SHOW : miniMap.IMAGELINK_BUTTON_BUILDINGS_HIDE);
                    }
                }, miniMap.IMAGELINK_BUTTON_BUILDINGS_HIDE, miniMap.IMAGELINK_BUTTON_BUILDINGS_SHOW, "");

        uiBase = createInterface();
        mainPanel.layoutPanel(layoutProperties);
	}

	private UIPanel createInterface() {
		UIPanel panel = new UIPanel();

		addMiniMap(panel);

		mainPanel.setBackground(layoutProperties.IMAGELINK_MAIN);
		panel.addChild(mainPanel, 0, 0, layoutProperties.miniMap.RIGHT_DECORATION_LEFT, layoutProperties.MAIN_PANEL_TOP);

		UIPanel rightDecoration = new UIPanel();
		rightDecoration.setBackground(layoutProperties.IMAGELINK_DECORATION_RIGHT);
		panel.addChild(
		        rightDecoration,
		        layoutProperties.miniMap.RIGHT_DECORATION_LEFT,
		        0,
		        layoutProperties.miniMap.RIGHT_DECORATION_LEFT + layoutProperties.RIGHT_DECORATION_WIDTH,
		        layoutProperties.MAIN_PANEL_TOP
		        );

		return panel;
	}

	private void addMiniMap(UIPanel panel)
	{
	    MiniMapLayoutProperties miniMap = layoutProperties.miniMap;
		UIPanel minimapbg_left = new UIPanel();
		minimapbg_left.setBackground(miniMap.LEFT_DECORATION);
		minimapbg_left.addChild(
				btnChat,
				miniMap.BUTTON_CHAT_LEFT,
				miniMap.BUTTON_CHAT_TOP - miniMap.BUTTON_HEIGHT,
				miniMap.BUTTON_CHAT_LEFT + miniMap.BUTTON_WIDTH,
				miniMap.BUTTON_CHAT_TOP
				);
		minimapbg_left.addChild(
				btnFeatures,
				miniMap.BUTTON_FEATURES_LEFT,
				miniMap.BUTTON_FEATURES_TOP - miniMap.BUTTON_HEIGHT,
				miniMap.BUTTON_FEATURES_LEFT + miniMap.BUTTON_WIDTH,
				miniMap.BUTTON_FEATURES_TOP
				);
		minimapbg_left.addChild(
				btnSettlers,
				miniMap.BUTTON_SETTLERS_LEFT,
				miniMap.BUTTON_SETTLERS_TOP - miniMap.BUTTON_HEIGHT,
				miniMap.BUTTON_SETTLERS_LEFT + miniMap.BUTTON_WIDTH,
				miniMap.BUTTON_SETTLERS_TOP
				);
		minimapbg_left.addChild(
				btnBuildings,
				miniMap.BUTTON_BUILDINGS_LEFT,
				miniMap.BUTTON_BUILDINGS_TOP - miniMap.BUTTON_HEIGHT,
				miniMap.BUTTON_BUILDINGS_LEFT + miniMap.BUTTON_WIDTH,
				miniMap.BUTTON_BUILDINGS_TOP
				);

		UIPanel minimapbg_right = new UIPanel();
		minimapbg_right.setBackground(miniMap.RIGHT_DECORATION);

		panel.addChild(minimapbg_left, 0, layoutProperties.MAIN_PANEL_TOP, miniMap.RIGHT_DECORATION_LEFT, 1);
		panel.addChild(minimapbg_right, miniMap.RIGHT_DECORATION_LEFT, layoutProperties.MAIN_PANEL_TOP, 1, 1);
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		ControlPanelLayoutProperties newLayoutProperties = ControlPanelLayoutProperties.getLayoutPropertiesFor(newHeight);
		if (!newLayoutProperties.equals(layoutProperties)) {
			layoutProperties = newLayoutProperties;
			uiBase = createInterface();
			mainPanel.layoutPanel(layoutProperties);
		}
		int width = (int) (layoutProperties.ASPECT_RATIO * newHeight);
		this.uiBase.setPosition(new FloatRectangle(0, 0, width, newHeight));

		minimap.setSize(
				(int) Math.ceil(layoutProperties.miniMap.MAP_WIDTH * width),
				(int) Math.ceil(layoutProperties.miniMap.MAP_HEIGHT * (1 - layoutProperties.MAIN_PANEL_TOP) * newHeight));
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
		return layoutProperties.miniMap.MAP_LEFT * uiBase.getPosition().getWidth();
	}

	private float getMinimapBottom() {
		return (layoutProperties.MAIN_PANEL_TOP + (1f - layoutProperties.MAIN_PANEL_TOP) * layoutProperties.miniMap.MAP_BOTTOM)
				* uiBase.getPosition().getHeight();
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		float width = uiBase.getPosition().getWidth();
        float uicenter =  layoutProperties.miniMap.RIGHT_DECORATION_LEFT * width;
        return position.getX() < Math.max(uicenter, getMinimapOffset(position.getY()) + uicenter);
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
		float m = 1 / (1 - layoutProperties.miniMap.RIGHT_DECORATION_LEFT) / width * (1 - layoutProperties.MAIN_PANEL_TOP) * height;
		return y / m - layoutProperties.MAIN_PANEL_TOP * height / m;
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		float relativex =
				(float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
				(float) position.getY() / this.uiBase.getPosition().getHeight();
		Action action;
		if (minimap != null && relativey > layoutProperties.MAIN_PANEL_TOP && getMinimapOffset(position.getY()) < position.getX()) {
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
				(relativex - layoutProperties.miniMap.MAP_LEFT)
						/ layoutProperties.miniMap.MAP_WIDTH;
		float minimapy =
				((relativey - layoutProperties.MAIN_PANEL_TOP)
						/ (1 - layoutProperties.MAIN_PANEL_TOP) - layoutProperties.miniMap.MAP_BOTTOM)
						/ layoutProperties.miniMap.MAP_HEIGHT;
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
	public void displayBuildingBuild(EBuildingType type) {
		mainPanel.displayBuildingBuild(type);
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
				mainPanel.setContent(new BuildingSelectionPanel(selection));
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