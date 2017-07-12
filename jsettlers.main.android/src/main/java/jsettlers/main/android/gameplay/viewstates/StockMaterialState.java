package jsettlers.main.android.gameplay.viewstates;

/**
 * Created by Tom Pratt on 12/07/2017.
 */

import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;

/**
 * Model for stock item
 */
public class StockMaterialState {
    private final EMaterialType materialType;
    private final boolean stocked;

    public StockMaterialState(EMaterialType materialType, IStockSettings stockSettings) {
        this.materialType = materialType;
        this.stocked = stockSettings.isAccepted(materialType);
    }

    public EMaterialType getMaterialType() {
        return materialType;
    }

    public boolean isStocked() {
        return stocked;
    }
}