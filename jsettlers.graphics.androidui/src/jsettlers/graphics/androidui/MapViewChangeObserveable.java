package jsettlers.graphics.androidui;

import java.util.concurrent.CopyOnWriteArrayList;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.selectable.ISelectionSet;
import android.content.Context;
import android.os.Handler;

/**
 * This class allows you to register observers for the selection state and the displayed map area.
 * 
 * @author Michael Zangl
 *
 */
public class MapViewChangeObserveable {
	public interface IMapViewListener {
		/**
		 * Called whenever the map view changed. Always called in the UI thread.
		 * 
		 * @param newSelection
		 *            The selection.
		 */
		public void mapViewChanged(MapRectangle mapView);
	}

	public interface IMapSelectionListener {
		/**
		 * Called whenever the selection changed. Always called in the UI thread.
		 * 
		 * @param newSelection
		 */
		public void mapSelectionChanged(ISelectionSet newSelection);
	}

	private final class FireMapViewChanged implements Runnable {
		private final MapRectangle newView;

		private FireMapViewChanged(MapRectangle newView) {
			this.newView = newView;
		}

		@Override
		public void run() {
			for (IMapViewListener l : viewListeners) {
				l.mapViewChanged(newView);
			}
		}
	}

	private final class FireMapSelectionChanged implements Runnable {
		private final ISelectionSet newSelection;

		private FireMapSelectionChanged(ISelectionSet newSelection) {
			this.newSelection = newSelection;
		}

		@Override
		public void run() {
			for (IMapSelectionListener l : selectionListeners) {
				l.mapSelectionChanged(newSelection);
			}
		}
	}

	private MapRectangle mapView;
	private final Handler handler;

	private CopyOnWriteArrayList<IMapViewListener> viewListeners = new CopyOnWriteArrayList<IMapViewListener>();
	private CopyOnWriteArrayList<IMapSelectionListener> selectionListeners = new CopyOnWriteArrayList<IMapSelectionListener>();

	public MapViewChangeObserveable(Context androidContext) {
		handler = new Handler(androidContext.getMainLooper());
	}

	protected void fireMapViewChanged(final MapRectangle newView) {
		mapView = newView;
		handler.post(new FireMapViewChanged(newView));
	}

	protected void fireMapSelectionChanged(final ISelectionSet newSelection) {
		handler.post(new FireMapSelectionChanged(newSelection));
	}

	public void addMapViewListener(IMapViewListener l) {
		viewListeners.add(l);
	}

	public void removeMapViewListener(IMapViewListener l) {
		viewListeners.remove(l);
	}

	public void addMapSelectionListener(IMapSelectionListener l) {
		selectionListeners.add(l);
	}

	public void removeMapSelectionListener(IMapSelectionListener l) {
		selectionListeners.remove(l);
	}
}
