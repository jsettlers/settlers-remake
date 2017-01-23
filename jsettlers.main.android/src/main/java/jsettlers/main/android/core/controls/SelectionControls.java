package jsettlers.main.android.core.controls;

import jsettlers.common.selectable.ISelectionSet;

/**
 * Created by tompr on 10/01/2017.
 */

public interface SelectionControls {
    ISelectionSet getCurrentSelection();
    void deselect();
    void addSelectionListener(SelectionListener selectionListener);
    void removeSelectionListener(SelectionListener selectionListener);
}
