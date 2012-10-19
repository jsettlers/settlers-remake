package jsettlers.graphics.map.controls.mobile;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.text.EFontSize;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.mobile.actions.ConstructBuilding;
import jsettlers.graphics.map.controls.mobile.actions.ContextAction;
import jsettlers.graphics.map.controls.small.BuildMenu;
import jsettlers.graphics.utils.Button;

/**
 * This is the main navigation and menu circle.
 * 
 * @author michael
 */
public class MobileControls implements IControls {

	private static final float SIZEFACTOR = .5f;
	/**
	 * Screen dimension
	 */
	private float width;
	private float height;

	private ContextAction activeAction = null;
	private final Object activeActionMutex = new Object();

	private MobileMenu activeMenu = null;
	private final Object activeMenuMutex = new Object();

	private final MobileMenu buildMenu = new BuildMenu();
	Button showBuildButton = new Button(new ExecutableAction() {
		@Override
		public void execute() {
			setActiveMenu(buildMenu);
		}
	}, new OriginalImageLink(EImageLinkType.GUI, 3, 395, 0),
	        new OriginalImageLink(EImageLinkType.GUI, 3, 395, 0),
	        Labels.getString("show-build-menu"));

	Button showSelectionButton = new Button(new ExecutableAction() {
		@Override
		public void execute() {

		}
	}, new OriginalImageLink(EImageLinkType.GUI, 3, 437, 0),
	        new OriginalImageLink(EImageLinkType.GUI, 3, 437, 0),
	        Labels.getString("show-build-menu"));

	private NavigationPoint naviPoint = new NavigationPoint(null);

	public void drawAt(GLDrawContext gl) {
		if (!naviPoint.isPanInProgress()) {
			showBuildButton.drawAt(gl);
			showSelectionButton.drawAt(gl);
		}
		naviPoint.drawAt(gl);
		synchronized (activeActionMutex) {
			if (activeAction != null) {
				gl.getTextDrawer(EFontSize.NORMAL).drawString(10, 10,
				        activeAction.getDesciption());
			}
		}
		synchronized (activeMenuMutex) {
			if (activeMenu != null) {
				gl.color(0, 0, 0, .5f);
				gl.fillQuad(0, 0, width, height);
				activeMenu.drawAt(gl);
			}
		}
	}

	protected void setActiveMenu(MobileMenu activeMenu) {
		synchronized (activeMenuMutex) {
			this.activeMenu = activeMenu;
		}
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		this.width = newWidth;
		this.height = newHeight;

		float buttonsize = height * .125f;
		showBuildButton.setPosition(new FloatRectangle(width - buttonsize,
		        buttonsize * 2, width, buttonsize * 3));
		showSelectionButton.setPosition(new FloatRectangle(width - 3
		        * buttonsize, 0, width - 2 * buttonsize, buttonsize));
		naviPoint.setPosition(width - 1.7f * buttonsize, 1.7f * buttonsize,
		        buttonsize * 2.5f);

		buildMenu.setPosition(new FloatRectangle(buttonsize / 2,
		        buttonsize / 2, width - buttonsize * 3.5f, height - buttonsize
		                / 2));
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		if (activeMenu != null) {
			return true;
		} else {
			float rHeight = SIZEFACTOR * height;

			float lineOffset = -width + rHeight;
			return position.getX() + lineOffset > position.getY();
		}
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		synchronized (activeMenuMutex) {
			if (activeMenu != null) {
				return activeMenu.getActionFor(position);
			} else {
				float y = (float) position.getY();
				float x = (float) position.getX();
				if (showBuildButton.getPosition().contains(x, y)) {
					return showBuildButton.getAction();
				} else if (showSelectionButton.getPosition().contains(x, y)) {
					return showSelectionButton.getAction();
				} else {
					return null;
				}
			}
		}
	}

	@Override
	public Action replaceAction(Action action) {
		synchronized (activeMenuMutex) {
			if (action.getActionType() == EActionType.BACK
			        && activeMenu != null) {
				activeMenu = null;
				return null;
			} else {
				return action;
			}
		}
	}

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		if (!containsPoint(event.getDrawPosition())) {
			return false;
		} else {
			if (naviPoint.centerContains(event.getDrawPosition())) {
				event.setHandler(new NavigationPointDrawHandler(naviPoint));
			}

			return true;
		}
	}

	@Override
	public void action(Action action) {
		if (action.getActionType() == EActionType.EXECUTABLE) {
			((ExecutableAction) action).execute();
		}
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
		synchronized (activeActionMutex) {
			if (type == null) {
				activeAction = null;
			} else {
				activeAction = new ConstructBuilding(type);
			}
		}
	}

	@Override
	public void displaySelection(ISelectionSet selection) {
		
	}

	@Override
	public void setDrawContext(MapDrawContext context) {
		naviPoint = new NavigationPoint(context);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
}
