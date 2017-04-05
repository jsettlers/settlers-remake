package jsettlers.logic.movable;

import jsettlers.common.selectable.ESelectionType;

/**
 * @author homoroselaps
 */

public class SelectableComponent extends Component {
    private static final long serialVersionUID = 665477836143096339L;
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
    public void onDisable() {
        setSelected(false);
    }
}
