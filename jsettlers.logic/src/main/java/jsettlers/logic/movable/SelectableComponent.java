package jsettlers.logic.movable;

import jsettlers.common.selectable.ESelectionType;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class SelectableComponent extends Component {

    private final ESelectionType _selectionType;
    public ESelectionType getSelectionType() {
        return _selectionType;
    }

    private boolean _isSelected;
    public boolean isSelected() {
        return _isSelected;
    }
    public void setSelected(boolean selected) {
        _isSelected = selected;
    }

    SelectableComponent(ESelectionType selectionType) {
        _selectionType = selectionType;
    }

    @Override
    public void OnDisable() {
        setSelected(false);
    }
}
