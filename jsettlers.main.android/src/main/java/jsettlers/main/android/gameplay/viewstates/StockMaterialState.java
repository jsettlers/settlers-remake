package jsettlers.main.android.gameplay.viewstates;

/**
 * Created by Tom Pratt on 12/07/2017.
 */

import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;

/**
 * Model for stock item
 */
public class StockMaterialState {
    private final EMaterialType materialType;
    private final boolean stocked;

    public StockMaterialState(EMaterialType materialType, BuildingState state) {
        this.materialType = materialType;
        this.stocked = state.stockAcceptsMaterial(materialType);
    }

    public EMaterialType getMaterialType() {
        return materialType;
    }

    public boolean isStocked() {
        return stocked;
    }
}