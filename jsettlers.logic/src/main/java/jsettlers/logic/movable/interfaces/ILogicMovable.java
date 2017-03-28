package jsettlers.logic.movable.interfaces;

import java.io.Serializable;

import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.IGuiMovable;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;

public interface ILogicMovable extends
        IScheduledTimerable,
        IPathCalculatable,
        IDebugable,
        Serializable,
        IViewDistancable,
        IGuiMovable,
        IAttackableMovable {
    boolean push(ILogicMovable pushingMovable);
    Path getPath();
    void goSinglePathStep();
    ShortPoint2D getPosition();
    ILogicMovable getPushedFrom();
    boolean isProbablyPushable(ILogicMovable pushingMovable);
    void leavePosition();
    boolean canOccupyBuilding();
    void checkPlayerOfPosition(Player playerOfPosition);
    void convertTo(EMovableType newMovableType);
    Player getPlayer();
    IBuildingOccupyableMovable setOccupyableBuilding(IOccupyableBuilding building);
    void moveTo(ShortPoint2D targetPosition);
}
