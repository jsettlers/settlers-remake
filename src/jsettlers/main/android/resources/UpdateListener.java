package jsettlers.main.android.resources;

import jsettlers.graphics.progress.EProgressState;

/**
 * This is a listener that gets called when a resource update was finished.
 * @author michaelz
 *
 */
public interface UpdateListener {
	public void resourceUpdateFinished();

	public void setProgressState(EProgressState state, float progress);
}
