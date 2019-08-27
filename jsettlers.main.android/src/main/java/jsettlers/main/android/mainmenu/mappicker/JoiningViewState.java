package jsettlers.main.android.mainmenu.mappicker;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public class JoiningViewState {
	private final String state;
	private final int progress;

	public JoiningViewState(String state, int progress) {
		this.state = state;
		this.progress = progress;
	}

	public String getState() {
		return state;
	}

	public int getProgress() {
		return progress;
	}
}
