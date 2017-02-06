package jsettlers.logic.movable;

import jsettlers.common.selectable.ESelectionType;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class SelectableComponent implements Component {
    public boolean isSelected() {
        //SectableComponent
        return false;
    }

    public void setSelected(boolean selected) {
        //SectableComponent
    }

    public ESelectionType getSelectionType() {
        //SectableComponent
        return null;
    }
}
