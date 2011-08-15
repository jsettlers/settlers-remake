package jsettlers.graphics.map;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.action.Action;

/**
 * This is a ma interface listener. <h2>Registering listeners</h2> At first you have to get the connector when adding the map va
 * {@link JOGLPanel#showHexMap(jsettlers.graphics.map.IHexMap)} Then you should add a listener to the connector with
 * {@link MapInterfaceConnector#addListener(MapInterfaceListener)}.
 * 
 * @author michael
 */
public interface IMapInterfaceListener {
	/**
	 * This method gets called when the user performed an action.
	 * 
	 * @param action
	 *            The action the user performed.
	 */
	void action(Action action);
}
