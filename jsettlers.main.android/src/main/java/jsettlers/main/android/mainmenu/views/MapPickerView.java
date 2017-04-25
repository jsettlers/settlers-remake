package jsettlers.main.android.mainmenu.views;

import java.util.List;

import jsettlers.logic.map.loading.MapLoader;

/**
 * Created by tompr on 22/01/2017.
 */

public interface MapPickerView {
	void setItems(List<? extends MapLoader> items);
}
