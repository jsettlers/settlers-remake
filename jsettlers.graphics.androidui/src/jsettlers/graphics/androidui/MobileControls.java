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
package jsettlers.graphics.androidui;

import android.os.Handler;
import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.text.EFontSize;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.androidui.actions.ConstructBuilding;
import jsettlers.graphics.androidui.actions.ContextAction;
import jsettlers.graphics.androidui.actions.ContextActionListener;
import jsettlers.graphics.androidui.actions.MoveToOnClick;
import jsettlers.graphics.androidui.menu.AndroidMenu;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.PauseMenu;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

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

	private final PauseMenu pauseMenu;
	private ISelectionSet selection;
	private MapDrawContext context;

	public MobileControls(AndroidMenuPutable p) {
		androidMenuPutable = p;
		p.setContextActionListener(this);
		pauseMenu = new PauseMenu(p);
	}

	@Override
	public void drawAt(GLDrawContext gl) {
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
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		if (androidMenuPutable.getActiveMenu() != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Action getActionFor(UIPoint position, boolean selecting) {
		AndroidMenu activeMenu = androidMenuPutable.getActiveMenu();
		if (activeMenu != null) {
			return activeMenu.getActionFor(position);
		} else {
			return null;
		}
	}

	@Override
	public IAction replaceAction(IAction action) {
		IAction replaced = action;
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
		return false;
	}

	@Override
	public void action(IAction action) {
		if (action.getActionType() == EActionType.SPEED_SET_PAUSE) {
			setActiveMenu(pauseMenu);
		} else if (action.getActionType() == EActionType.SPEED_UNSET_PAUSE
				&& androidMenuPutable.getActiveMenu() == pauseMenu) {
			setActiveMenu(null);
		} else if (action.getActionType() == EActionType.SET_WORK_AREA
				|| action.getActionType() == EActionType.SELECT_POINT
				|| action.getActionType() == EActionType.SELECT_AREA
				|| action.getActionType() == EActionType.MOVE_TO
				|| action.getActionType() == EActionType.BUILD
				|| action.getActionType() == EActionType.ABORT) {
			setActiveAction(null);
		} else if (action.getActionType() == EActionType.SHOW_CONSTRUCTION_MARK) {
			EBuildingType type = ((ShowConstructionMarksAction) action).getBuildingType();
			if (type != null) {
				setActiveAction(new ConstructBuilding(type));
			}
		}
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		return null;
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
		androidMenuPutable.getChangeObserveable().fireMapViewChanged(screenArea);
	}

	private void setActiveAction(ContextAction activeAction) {
		synchronized (activeActionMutex) {
			if (activeAction != this.activeAction) {
				if (this.activeAction != null) {
					this.activeAction.onDeactivate(androidMenuPutable);
				}
				this.activeAction = activeAction;
			}
		}
	}

	@Override
	public void contextActionChanged(ContextAction newAction) {
		setActiveAction(newAction);
	}

	@Override
	public void displaySelection(ISelectionSet selection) {
		this.selection = selection;
		androidMenuPutable.getChangeObserveable().fireMapSelectionChanged(selection);
		if (selection != null
				&& (selection.getSelectionType() == ESelectionType.SOLDIERS || selection
						.getSelectionType() == ESelectionType.SPECIALISTS)) {
			setActiveAction(new MoveToOnClick());
		}
	}

	@Override
	public void setDrawContext(ActionFireable actionFireable,
			MapDrawContext context) {
		this.context = context;
		androidMenuPutable.setDrawContext(actionFireable, context);
		// naviPoint = new NavigationPoint(context);
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
