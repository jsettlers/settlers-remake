package jsettlers.main.android.views;

import java.util.List;

import jsettlers.common.menu.IMapDefinition;

/**
 * Created by tompr on 22/01/2017.
 */

public interface MapPickerView {
    void setItems(List<? extends IMapDefinition> items);
}
