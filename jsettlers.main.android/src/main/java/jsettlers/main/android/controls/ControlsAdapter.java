package jsettlers.main.android.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;

/**
 * Created by tompr on 21/11/2016.
 */

public class ControlsAdapter implements IControls {
    private IControls controls;

    private IAction activeAction;

    public void setControls(IControls controls) {
        this.controls = controls;
    }

    @Override
    public void action(IAction action) {
//        if (controls != null) {
//            controls.action(action);
//        }

        switch (action.getActionType()) {
            case SHOW_CONSTRUCTION_MARK:
                activeAction = action;
                break;
        }
    }

    @Override
    public IAction replaceAction(IAction action) {
//        if (controls != null) {
//            controls.replaceAction(action);
//        }
        if (action.getActionType() == EActionType.SELECT_POINT && activeAction.getActionType() == EActionType.SHOW_CONSTRUCTION_MARK) {

            EBuildingType type = ((ShowConstructionMarksAction) activeAction).getBuildingType();

            return new BuildAction(type, ((PointAction) action).getPosition());
        }

        return action;
    }

    @Override
    public void drawAt(GLDrawContext gl) {
        if (controls != null) {
            controls.drawAt(gl);
        }
    }

    @Override
    public void resizeTo(float newWidth, float newHeight) {
        if (controls != null) {
            controls.resizeTo(newWidth, newHeight);
        }
    }

    @Override
    public boolean containsPoint(UIPoint position) {
//        if (controls != null) {
//            return controls.containsPoint(position);
//        }
        return false;
    }

    @Override
    public String getDescriptionFor(UIPoint position) {
        if (controls != null) {
            return getDescriptionFor(position);
        }
        return null;
    }

    @Override
    public void setMapViewport(MapRectangle screenArea) {
        if (controls != null) {
            controls.setMapViewport(screenArea);
        }
    }

    @Override
    public Action getActionFor(UIPoint position, boolean selecting) {
        if (controls != null) {
            return  getActionFor(position, selecting);
        }
        return null;
    }

    @Override
    public boolean handleDrawEvent(GODrawEvent event) {
        if (controls != null) {
            controls.handleDrawEvent(event);
        }
        return false;
    }

    @Override
    public void displaySelection(ISelectionSet selection) {
        if (controls != null) {
            controls.displaySelection(selection);
        }
    }

    @Override
    public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {
        if (controls != null) {
            controls.setDrawContext(actionFireable, context);
        }
    }

    @Override
    public String getMapTooltip(ShortPoint2D point) {
        if (controls != null) {
            controls.getMapTooltip(point);
        }
        return null;
    }

    @Override
    public void stop() {
        if (controls != null) {
            controls.stop();
        }
    }
}
