package jsettlers.main.android;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

/**
 * Created by tompr on 19/11/2016.
 */

public class DummyControls implements IControls {
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
        return null;
    }

    @Override
    public void setMapViewport(MapRectangle screenArea) {

    }

    @Override
    public Action getActionFor(UIPoint position, boolean selecting) {
        return null;
    }

    @Override
    public boolean handleDrawEvent(GODrawEvent event) {
        return false;
    }

    @Override
    public void displaySelection(ISelectionSet selection) {

    }

    @Override
    public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {

    }

    @Override
    public IAction replaceAction(IAction action) {
        return null;
    }

    @Override
    public String getMapTooltip(ShortPoint2D point) {
        return null;
    }

    @Override
    public void stop() {

    }

    @Override
    public void action(IAction action) {

    }
}
