package jsettlers.main.android.gameplay.viewstates;

import jsettlers.common.material.EMaterialType;

/**
 * Created by Tom Pratt on 05/08/2017.
 */

public class TradeMaterialState {
    private final EMaterialType materialType;
    private int count;

    public TradeMaterialState(EMaterialType materialType, int count) {
        this.materialType = materialType;
        this.count = count;
    }

    public EMaterialType getMaterialType() {
        return materialType;
    }

    public int getCount() {
        return count;
    }
}