package jsettlers.main.android.controls;

import java.util.LinkedList;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.android.AndroidSoundPlayer;
import go.graphics.event.mouse.GODrawEvent;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.player.IInGamePlayer;
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
import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.menus.SettlersSoldiersMenu;

import android.content.Context;

/**
 * Created by tompr on 21/11/2016.
 */

public class ControlsAdapter implements IControls, ActionControls, DrawControls, SelectionControls, TaskControls, MenuProvider {
    private final Context context;
    private final AndroidSoundPlayer soundPlayer;
    private final IInGamePlayer player;

    private final LinkedList<SelectionListener> selectionListeners = new LinkedList<>();
    private final LinkedList<ActionListener> actionListeners = new LinkedList<>();
    private final LinkedList<DrawListener> drawListeners = new LinkedList<>();

    private ActionFireable actionFireable;
    private ISelectionSet selection;


    /**
     * A task is something that requires multiple steps. E.g. show constructions markers, then choose build location.
     * If an action is part of a task then store it so we know how to react when the next action comes in.
     */
    private IAction taskAction;

    public ControlsAdapter(Context context, AndroidSoundPlayer soundPlayer, IInGamePlayer player) {
        this.context = context;
        this.soundPlayer = soundPlayer;
        this.player = player;
    }

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
            case ASK_SET_WORK_AREA:
                startTask(action);
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
                    case ASK_SET_WORK_AREA:
                        return new PointAction(EActionType.SET_WORK_AREA, pointAction.getPosition());
                }
            }
        }

        return action;
    }

    /**
     * The controls send out null for nothing selected.
     */
    @Override
    public void displaySelection(ISelectionSet selection) {
        if (selection != null && selection.getSize() > 0) {
            this.selection = selection;

            if (selection.getSelectionType() == ESelectionType.SOLDIERS || selection.getSelectionType() == ESelectionType.SPECIALISTS) {
                startTask(new Action(EActionType.MOVE_TO));
            }
        } else {
            this.selection = null;
        }

        synchronized (selectionListeners) {
            for (SelectionListener listener : selectionListeners) {
                listener.selectionChanged(this.selection);
            }
        }
    }

    @Override
    public void drawAt(GLDrawContext gl) {
        synchronized (drawListeners) {
            for (DrawListener listener : drawListeners) {
                listener.draw();
            }
        }
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



    /**
     * ActionControls implementation
     */
    @Override
    public void fireAction(IAction action) {
        actionFireable.fireAction(action);
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.add(actionListener);
        }
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        synchronized (actionListeners) {
            actionListeners.remove(actionListener);
        }
    }

    /**
     * DrawControls implementation
     */
    @Override
    public void addDrawListener(DrawListener drawListener) {
        synchronized (drawListeners) {
            drawListeners.add(drawListener);
        }
    }

    @Override
    public void removeDrawListener(DrawListener drawListener) {
        synchronized (drawListeners) {
            drawListeners.remove(drawListener);
        }
    }

    /**
     * SelectionControls implementation
     */
    @Override
    public ISelectionSet getCurrentSelection() {
        return selection;
    }

    @Override
    public void addSelectionListener(SelectionListener selectionListener) {
        synchronized (selectionListeners) {
            selectionListeners.add(selectionListener);
        }
    }

    @Override
    public void removeSelectionListener(SelectionListener selectionListener) {
        synchronized (selectionListeners) {
            selectionListeners.remove(selectionListener);
        }
    }

    /**
     * TaskControls implementation
     * @return
     */
    @Override
    public boolean isTaskActive() {
        return taskAction != null;
    }

    @Override
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

    private void startTask(IAction action) {
        endTask();
        taskAction = action;
    }

    private void updateTask(IAction action) {
        taskAction = action;
    }


    /**
     * MenuProvider implementation
     */
    private GameMenu gameMenu;
    @Override
    public GameMenu getGameMenu() {
        if (gameMenu == null) {
            gameMenu = new GameMenu(context, actionFireable, soundPlayer);
        }
        return gameMenu;
    }

    @Override
    public BuildingsMenu getBuildingsMenu() {
        return new BuildingsMenu(this);
    }

    @Override
    public SettlersSoldiersMenu getSettlersSoldiersMenu() {
        return new SettlersSoldiersMenu(this);
    }
}
