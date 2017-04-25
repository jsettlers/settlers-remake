package jsettlers.main.android.mainmenu.views;

/**
 * Created by tompr on 22/01/2017.
 */

public interface NewMultiPlayerPickerView extends MapPickerView {
	void setJoiningProgress(String stateString, int progressPercentage);

	void dismissJoiningProgress();
}
