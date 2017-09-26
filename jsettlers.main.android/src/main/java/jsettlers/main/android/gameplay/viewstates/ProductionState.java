package jsettlers.main.android.gameplay.viewstates;

import jsettlers.common.material.EMaterialType;

/**
 * Created by Tom Pratt on 25/09/2017.
 */

public class ProductionState {
    private final EMaterialType materialType;
    private final int quantity;

    public ProductionState(EMaterialType materialType, int quantity) {
        this.materialType = materialType;
        this.quantity = quantity;
    }

    public EMaterialType getMaterialType() {
        return materialType;
    }

    public int getQuantity() {
        return quantity;
    }
}
