package jsettlers.graphics.startscreen.interfaces;

/**
 * A
 * 
 * @author Andreas Eberle
 */
public interface IJoinableGame {
	/*
	 * TODO: Send a list of players / how many places are still free / ... Currently not needed.
	 */

	String getId();

	String getName();

	IMapDefinition getMap();

}
