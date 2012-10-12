package jsettlers.graphics.map.controls.original;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.PanToAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.map.controls.original.panel.content.BearerSelection;
import jsettlers.graphics.map.controls.original.panel.content.BuildingSelectionPanel;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.map.controls.original.panel.content.SoilderSelection;
import jsettlers.graphics.map.controls.original.panel.content.SpecialistSelection;
import jsettlers.graphics.map.minimap.Minimap;
import jsettlers.graphics.utils.UIPanel;

public class OriginalControls implements IControls {

	private UIPanel uiBase;

	private Minimap minimap;

	private final MainPanel mainPanel = new MainPanel();

	private IOriginalConstants constants;

	public OriginalControls() {
		constants = new SmallOriginalConstants();
		uiBase = createInterface();
		mainPanel.useConstants(constants);
	}

	private UIPanel createInterface() {
		UIPanel panel = new UIPanel();

		UIPanel minimapbg_left = new UIPanel();
		minimapbg_left.setBackground(constants.UI_BG_SEQ_MINIMAPL);
		panel.addChild(minimapbg_left, 0, constants.UI_CENTERY,
		        constants.UI_CENTERX, 1);

		UIPanel minimapbg_right = new UIPanel();
		minimapbg_right.setBackground(constants.UI_BG_SEQ_MINIMAPR);
		panel.addChild(minimapbg_right, constants.UI_CENTERX,
		        constants.UI_CENTERY, 1, 1);

		mainPanel.setBackground(constants.UI_BG_SEQ_MAIN);
		panel.addChild(mainPanel, 0, 0, constants.UI_CENTERX,
		        constants.UI_CENTERY);

		UIPanel rightDecoration = new UIPanel();
		rightDecoration.setBackground(constants.UI_BG_SEQ_RIGHT);
		panel.addChild(rightDecoration, constants.UI_CENTERX, 0,
		        constants.UI_DECORATIONRIGHT, constants.UI_CENTERY);

		return panel;
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		IOriginalConstants newConstants;
		if (newHeight <= 480) {
			newConstants = new SmallOriginalConstants();
		} else {
			// TODO: higher resolution for controls.
			newConstants = new SmallOriginalConstants();
		}
		if (!newConstants.equals(constants)) {
			constants = newConstants;
			uiBase = createInterface();
			mainPanel.useConstants(constants);
		}
		int width = (int) (newHeight / constants.UI_RATIO);
		this.uiBase.setPosition(new FloatRectangle(0, 0, width, newHeight));

		minimap.setSize(
		        (int) (constants.MINIMAP_WIDTH * width),
		        (int) (constants.MINIMAP_HEIGHT * (1 - constants.UI_CENTERY) * newHeight));
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		uiBase.drawAt(gl);

		if (minimap != null) {
			gl.glPushMatrix();
			gl.glTranslatef(getMinimapLeft(), getMinimapBottom(), 0);
			minimap.draw(gl);
			gl.glPopMatrix();
		}
	}

	private float getMinimapLeft() {
		return constants.MINIMAP_BOTTOMLEFT_X * uiBase.getPosition().getWidth();
	}

	private float getMinimapBottom() {
		return (constants.UI_CENTERY + (1 - constants.UI_CENTERY)
		        * constants.MINIMAP_BOTTOM_Y)
		        * uiBase.getPosition().getHeight();
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		float width = uiBase.getPosition().getWidth();
		float height = uiBase.getPosition().getHeight();
		float uicenter = width * constants.UI_CENTERX;
		float m =
		        1 / (1 - constants.UI_CENTERX) / width
		                * (1 - constants.UI_CENTERY) * height;
		return position.getX() < uicenter
		        || position.getY() > position.getX() * m + constants.UI_CENTERY
		                * height - m * constants.UI_CENTERX * width;
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		float relativex =
		        (float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
		        (float) position.getY() / this.uiBase.getPosition().getHeight();
		Action action;
		if (minimap != null && relativey > constants.UI_CENTERY) {
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
		        (relativex - constants.MINIMAP_BOTTOMLEFT_X)
		                / constants.MINIMAP_WIDTH;
		float minimapy =
		        ((relativey - constants.UI_CENTERY)
		                / (1 - constants.UI_CENTERY) - constants.MINIMAP_BOTTOM_Y)
		                / constants.MINIMAP_HEIGHT;
		ShortPoint2D clickPosition =
		        minimap.getClickPositionIfOnMap(minimapx, minimapy);
		if (clickPosition != null) {
			if (selecting) {
				return new PanToAction(clickPosition);
			} else {
				return new MoveToAction(clickPosition);
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
			if (action instanceof PanToAction) {
				minimap.getContext().scrollTo(
				        ((PanToAction) action).getCenter());
			}
		}

	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
		mainPanel.displayBuildingBuild(type);
	}

	boolean lastSelectionWasNull = true;

	private MapDrawContext context;

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
	public void setDrawContext(MapDrawContext context) {
		this.context = context;
		this.minimap = new Minimap(context);

	}

	@Override
	public Action replaceAction(Action action) {
		return mainPanel.catchAction(action);
	};

	@Override
	public void stop() {
		minimap.stop();
	}
}
