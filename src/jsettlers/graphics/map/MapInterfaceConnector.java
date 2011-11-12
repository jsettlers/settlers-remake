package jsettlers.graphics.map;

import java.util.LinkedList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionFirerer;
import jsettlers.graphics.map.selection.ISelectionSet;

/**
 * This is the main interface connector.
 * <p>
 * It holds the current selection displayed in the interface (not on the map).
 * See {@link #setSelection(ISelectionSet)}.
 * <p>
 * It also propagates interface events, to get them you can add a interface
 * listener. See {@link IMapInterfaceListener}.
 * 
 * @author michael
 */
public class MapInterfaceConnector implements ActionFireable {

	private LinkedList<IMapInterfaceListener> listeners =
	        new LinkedList<IMapInterfaceListener>();

	private ActionFirerer actionFirerer = new ActionFirerer(
	        new ActionFireable() {
		        @Override
		        public void fireAction(Action action) {
			        synchronized (listeners) {
				        for (IMapInterfaceListener listener : listeners) {
					        listener.action(action);
				        }
			        }
		        }
	        });

	private final MapContent content;

	/**
	 * Creates a new connector for the given interface.
	 * 
	 * @param mapInterface
	 *            The interface.
	 */
	public MapInterfaceConnector(MapContent content) {
		this.content = content;
	}

	/**
	 * Sets the current selection that should be displayed in the side panel.
	 * 
	 * @param selection
	 *            The selection.
	 */
	public void setSelection(ISelectionSet selection) {
		this.content.setSelection(selection);
	}

	/**
	 * Scrolls a given point to the center of the view. It needn't be on the
	 * map.
	 * 
	 * @param point
	 *            The point to show.
	 * @param mark
	 *            If there should be a mark displayed at the point.
	 */
	public void scrollTo(ISPosition2D point, boolean mark) {
		this.content.scrollTo(point, mark);
	}

	/**
	 * Adds a listener that listens to interface commands.
	 * 
	 * @see IMapInterfaceListener
	 * @param listener
	 *            The listener.
	 * @see #removeListener(IMapInterfaceListener)
	 */
	public void addListener(IMapInterfaceListener listener) {
		synchronized (this.listeners) {
			if (listener != null && !this.listeners.contains(listener)) {
				this.listeners.add(listener);
			}
		}
	}

	/**
	 * Removes a given Listener, if it is regisered.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(IMapInterfaceListener listener) {
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public void fireAction(Action action) {
		this.actionFirerer.fireAction(action);
	}

	/**
	 * Sets the building the user is currently building.
	 * @param buildingType The type of the building.
	 */
	public void setPreviewBuildingType(EBuildingType buildingType) {
	    content.setPreviewBuildingType(buildingType);
    }
}
