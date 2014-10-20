package jsettlers.main.android.resources;


/**
 * This is a listener that gets called when a resource update was finished.
 * @author michaelz
 *
 */
public interface UpdateListener {
	public void resourceUpdateFinished();

	public void setProgressState(String state, float progress);
}
