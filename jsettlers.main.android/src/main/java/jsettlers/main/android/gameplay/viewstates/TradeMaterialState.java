package jsettlers.main.android.gameplay.viewstates;

import jsettlers.common.material.EMaterialType;

/**
 * Created by Tom Pratt on 05/08/2017.
 */

public class TradeMaterialState {
    private final EMaterialType materialType;

    public TradeMaterialState(EMaterialType materialType) {
        this.materialType = materialType;
    }

    public EMaterialType getMaterialType() {
        return materialType;
    }
}