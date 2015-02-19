package jsettlers.input;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.selectable.ISelectable;
import jsettlers.logic.movable.interfaces.IIDable;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGuiMovable extends IIDable, ISelectable, IPlayerable {

	EMovableType getMovableType();

}
