package jsettlers.graphics.map.controls.small;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.IControls;

/**
 * These controls provide:
 * <ul>
 * <li>A scroll area</li>
 * <li>A button to open the build menu</li>
 * <li>A button to open the options menu</li>
 * </ul>
 * 
 * @author michael
 */
public class SmallControls implements IControls {
	private TabableButton buildMenuOpener =
	        new TabableButton(new Action(EActionType.TOGGLE_BUILD_MENU),
	                new ImageLink(EImageLinkType.SETTLER, 11, 0, 0),
	                Labels.getString("action_BUILD"));

	private ScrollArea scrollArea = new ScrollArea();

	private BuildMenu buildMenu = new BuildMenu();

	private boolean buildMenuVisible = false;

	@Override
	public void drawAt(GLDrawContext gl) {
		scrollArea.drawAt(gl);
		buildMenuOpener.drawAt(gl);
		if (buildMenuVisible) {
			buildMenu.drawAt(gl);
		}
	}

	@Override
	public void resizeTo(float newWidth, float newHeight) {
		if (newWidth < newHeight) {
			// portrait
			float scrollsize = newWidth / 2;
			float buttonsize = scrollsize / 2;
			scrollArea.setPosition(new FloatRectangle(newWidth - scrollsize, 0,
			        newWidth, scrollsize));
			buildMenuOpener.setPosition(new FloatRectangle(0, 0, buttonsize,
			        buttonsize));
			buildMenu.setPosition(new FloatRectangle(0, buttonsize, newWidth,
			        newHeight));
		} else {
			// landscape
			float scrollsize = newHeight / 2;
			float buttonsize = scrollsize / 2;
			scrollArea.setPosition(new FloatRectangle(newWidth - scrollsize, 0,
			        newWidth, scrollsize));
			buildMenuOpener.setPosition(new FloatRectangle(newWidth - buttonsize,
			        newHeight - 2 * buttonsize, newWidth, newHeight
			                - buttonsize));
			buildMenu.setPosition(new FloatRectangle(0, 0, newWidth - buttonsize,
			        newHeight));
		}
	}

	@Override
	public boolean containsPoint(UIPoint position) {
		int x = (int) position.getX();
		int y = (int) position.getY();
		if (scrollArea.getPosition().contains(x, y)) {
			return true;
		} else if (buildMenuOpener.getPosition().contains(x, y)) {
			return true;
		} else if (buildMenuVisible && buildMenu.getPosition().contains(x, y)) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescriptionFor(UIPoint position) {
		int x = (int) position.getX();
		int y = (int) position.getY();
		if (buildMenuOpener.getPosition().contains(x, y)) {
			return "build";
		}
		return null;
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {

	}

	@Override
	public Action getActionFor(UIPoint position) {
		int x = (int) position.getX();
		int y = (int) position.getY();
		if (buildMenuOpener.getPosition().contains(x, y)) {
			return buildMenuOpener.getAction();
		} else if (buildMenuVisible && buildMenu.getPosition().contains(x, y)) {
			return buildMenu.getActionFor(x, y);
		}
		return null;
	}

	@Override
	public void action(Action action) {
		if (action.getActionType() == EActionType.TOGGLE_BUILD_MENU) {
			buildMenuVisible = !buildMenuVisible;
		} else if (action.getActionType() == EActionType.BUILD) {
			buildMenuVisible = false;
		}
	}

	@Override
	public boolean handleDrawEvent(GODrawEvent event) {
		if (buildMenuVisible) {
			event.setHandler(buildMenu.getScrollHandler());
			return true;
		} else {
			return false;
		}
	}

	@Override
    public void displayBuildingBuild(EBuildingType type) {
	    // TODO Auto-generated method stub
	    
    }

}
