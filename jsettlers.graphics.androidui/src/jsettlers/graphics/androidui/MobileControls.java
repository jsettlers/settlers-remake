package jsettlers.graphics.androidui;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.text.EFontSize;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.androidui.actions.ConstructBuilding;
import jsettlers.graphics.androidui.actions.ContextAction;
import jsettlers.graphics.androidui.actions.ContextActionListener;
import jsettlers.graphics.androidui.actions.MoveToOnClick;
import jsettlers.graphics.androidui.menu.AndroidMenu;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.BuildMenu;
import jsettlers.graphics.androidui.menu.PauseMenu;
import jsettlers.graphics.androidui.menu.selection.BuildingMenu;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.utils.Button;
import android.os.Handler;

/**
 * This is the main navigation and menu circle.
 * 
 * @author michael
 */
public class MobileControls implements IControls, ContextActionListener {

	private static final float SIZEFACTOR = .5f;
	/**
	 * Screen dimension
	 */
	private float width;
	private float height;

	private ContextAction activeAction = null;
	private final Object activeActionMutex = new Object();

	/**
	 * The container we need to display android menus.
	 */
	private final AndroidMenuPutable androidMenuPutable;

	private final AndroidMenu buildMenu;
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
			showSelectionMenu();
		}
	}, new OriginalImageLink(EImageLinkType.GUI, 3, 437, 0),
	        new OriginalImageLink(EImageLinkType.GUI, 3, 437, 0),
	        Labels.getString("show-build-menu"));

	private NavigationPoint naviPoint = new NavigationPoint(null);
	private final PauseMenu pauseMenu;
	private ISelectionSet selection;
	private MapDrawContext context;

	public MobileControls(AndroidMenuPutable p) {
		androidMenuPutable = p;
		p.setContextActionListener(this);
		buildMenu = new BuildMenu(androidMenuPutable);
		pauseMenu = new PauseMenu(androidMenuPutable);
	}

	@Override
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
		if (androidMenuPutable.getActiveMenu() != null) {
			gl.color(0, 0, 0, .5f);
			gl.fillQuad(0, 0, width, height);
			// activeMenu.drawAt(gl);
		}
		// TODO: Reuse, less frequent
		new Handler(androidMenuPutable.getContext().getMainLooper())
		        .post(new Runnable() {
			        @Override
			        public void run() {
				        pollActiveMenu();
			        }
		        });
	}

	private void pollActiveMenu() {
		AndroidMenu activeMenu = androidMenuPutable.getActiveMenu();
		if (activeMenu != null) {
			activeMenu.poll();
		}
	}

	protected void setActiveMenu(AndroidMenu activeMenu) {
		if (activeMenu != null) {
			androidMenuPutable.showMenuFragment(activeMenu);
		} else {
			androidMenuPutable.hideMenu();
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

		// buildMenu.setPosition(new FloatRectangle(buttonsize / 2,
		// buttonsize / 2, width - buttonsize * 3.5f, height - buttonsize
		// / 2));
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		if (androidMenuPutable.getActiveMenu() != null) {
			return true;
		} else {
			float rHeight = SIZEFACTOR * height;

			float lineOffset = -width + rHeight;
			return position.getX() + lineOffset > position.getY();
		}
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		AndroidMenu activeMenu = androidMenuPutable.getActiveMenu();
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

	@Override
	public Action replaceAction(Action action) {
		Action replaced = action;
		System.out.println("Ready to replace: " + action);
		synchronized (activeActionMutex) {
			if (activeAction != null) {
				replaced = activeAction.replaceAction(replaced);
			}
		}

		if (action.getActionType() == EActionType.EXECUTABLE) {
			((ExecutableAction) replaced).execute();
			replaced = null;
		} else if (action.getActionType() == EActionType.BACK) {
			AndroidMenu activeMenu = androidMenuPutable.getActiveMenu();
			replaced = null;
			if (activeMenu == null || !activeMenu.onBackButtonPressed()) {
				boolean menuWentBack = androidMenuPutable.goBackInMenu();
				if (!menuWentBack) {
					replaced = new Action(EActionType.SPEED_SET_PAUSE);
				}
			}
		}

		return replaced;
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
		if (action.getActionType() == EActionType.SPEED_SET_PAUSE) {
			setActiveMenu(pauseMenu);
		}  else if (action.getActionType() == EActionType.SPEED_UNSET_PAUSE
		        && androidMenuPutable.getActiveMenu() == pauseMenu) {
			setActiveMenu(null);
		} else if (action.getActionType() == EActionType.SET_WORK_AREA
		        || action.getActionType() == EActionType.SELECT_POINT
		        || action.getActionType() == EActionType.MOVE_TO) {
			setActiveAction(null);
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
		if (type == null) {
			setActiveAction(null);
		} else {
			setActiveAction(new ConstructBuilding(type));

		}
	}

	private void setActiveAction(ContextAction activeAction) {
		synchronized (activeActionMutex) {
			this.activeAction = activeAction;
		}
	}

	@Override
	public void contextActionChanged(ContextAction newAction) {
		setActiveAction(newAction);
	}

	@Override
	public void displaySelection(ISelectionSet selection) {
		this.selection = selection;
		if (selection != null
		        && (selection.getSelectionType() == ESelectionType.SOLDIERS || selection
		                .getSelectionType() == ESelectionType.SPECIALISTS)) {
			setActiveAction(new MoveToOnClick());
		}
	}

	protected void showSelectionMenu() {
		if (selection != null) {
			switch (selection.getSelectionType()) {
				case BUILDING:
					IBuilding building = (IBuilding) selection.get(0);
					ShortPoint2D pos = building.getPos();
					IPartitionSettings settings =
					        context.getMap().getPartitionSettings(pos.x, pos.y);
					setActiveMenu(new BuildingMenu(androidMenuPutable,
					        building, settings));
			}
		}
		showSelectionButton.setActive(selection != null);
	}

	@Override
	public void setDrawContext(ActionFireable actionFireable,
	        MapDrawContext context) {
		this.context = context;
		androidMenuPutable.setActionFireable(actionFireable);
		naviPoint = new NavigationPoint(context);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMapTooltip(ShortPoint2D point) {
		return null;
	}
}
