package networklib.server.actions.identify;

import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IUserAcceptor {

	boolean acceptNewPlayer(Player player);

}
