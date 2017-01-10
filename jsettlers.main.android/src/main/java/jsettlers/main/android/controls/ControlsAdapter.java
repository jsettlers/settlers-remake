package jsettlers.main.android.controls;

import java.util.LinkedList;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.mouse.GODrawEvent;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
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

public class ControlsAdapter implements IControls, ActionFireable {
    private ActionFireable actionFireable;
    private SelectionListener selectionListener;

    private final LinkedList<ActionListener> actionListeners = new LinkedList<>();


    /**
     * A task is something that requires multiple steps. E.g. show constructions markers, then choose build location.
     * If an action is part of a task then store it so we know how to react when the next action comes in.
     */
    private IAction taskAction;

    /**
     * The action is being sent to the game, here we just store it if it's part of a task. Depending on what's active and what's coming in
     * we may need to start a new task, update the current task or end the current task.
     */
    @Override
    public void action(IAction action) {
        switch (action.getActionType()) {
            case SHOW_CONSTRUCTION_MARK:
                ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) action;
                if (showConstructionMarksAction.getBuildingType() != null) { // null means dismissing the construction markers, so is not awaiting further actions
                    if (taskAction != null && taskAction.getActionType() == EActionType.SHOW_CONSTRUCTION_MARK) {
                        updateTask(action);
                    } else {
                        startTask(action);
                    }
                }
                break;
            case MOVE_TO:
                updateTask(action);
                break;
            case SET_WORK_AREA:
            case SELECT_POINT:
            case SELECT_AREA:
            case BUILD:
            case ABORT:
                endTask();
                break;
        }

        synchronized (actionListeners) {
            for (ActionListener listener : actionListeners) {
                listener.actionFired(action);
            }
        }
    }

    /**
     *
     * Replace the action based on the current task. E.g SELECT_POINT may be choosing a build location or moving soldiers depending on the current task
     */
    @Override
    public IAction replaceAction(IAction action) {
        if(taskAction != null) {
            if (action.getActionType() == EActionType.SELECT_POINT ) {
                PointAction pointAction = (PointAction) action;

                switch (taskAction.getActionType()) {
                    case SHOW_CONSTRUCTION_MARK:
                        ShowConstructionMarksAction showConstructionMarksAction = (ShowConstructionMarksAction) taskAction;
                        return new BuildAction(showConstructionMarksAction.getBuildingType(), pointAction.getPosition());
                    case MOVE_TO:
                        return new PointAction(EActionType.MOVE_TO, pointAction.getPosition());
                }
            }
        }

        return action;
    }

    @Override
    public void displaySelection(ISelectionSet selection) {
        //TODO tell the UI what type of selection this is so we can update the Settlers menu
        if (selection != null && (selection.getSelectionType() == ESelectionType.SOLDIERS || selection.getSelectionType() == ESelectionType.SPECIALISTS)) {
            startTask(new Action(EActionType.MOVE_TO));
        }

        if (selectionListener != null) {
            selectionListener.selectionChanged(selection);
        }
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
    public void setDrawContext(ActionFireable actionFireable, MapDrawContext context) {
        this.actionFireable = actionFireable;
    }

    @Override
    public String getMapTooltip(ShortPoint2D point) {
        return null;
    }

    @Override
    public void stop() {
    }

    public boolean isTaskActive() {
        return taskAction != null;
    }

    private void startTask(IAction action) {
        endTask();
        taskAction = action;
    }

    private void updateTask(IAction action) {
        taskAction = action;
    }

    public void endTask() {
        if(taskAction != null) {
            switch (taskAction.getActionType()) {
                case SHOW_CONSTRUCTION_MARK:
                    actionFireable.fireAction(new ShowConstructionMarksAction(null));
                    break;
                case MOVE_TO:
                    //TODO would be nice to deselect the settlers when this happens or it looks like nothing happened
                    break;
            }

            taskAction = null;
        }
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void addActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.add(actionListener);
        }
    }

    public void removeActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.remove(actionListener);
        }
    }

    /**
     * ActionFireable implementation
     */
    @Override
    public void fireAction(IAction action) {
        actionFireable.fireAction(action);
    }
}
