package jsettlers.logic.algorithms.fogofwar;

import java.io.Serializable;

/**
 * interface for a fog of war thread.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IFogOfWar extends Serializable {

	public abstract void toggleEnabled();

	public abstract boolean isVisible(int centerx, int centery);

	public abstract byte getVisibleStatus(int x, int y);

	public abstract void startThread(IFogOfWarGrid grid);

}
