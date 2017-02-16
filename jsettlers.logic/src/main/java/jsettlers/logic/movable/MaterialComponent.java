package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class MaterialComponent extends Component {
    private EMaterialType material = EMaterialType.NO_MATERIAL;

    public EMaterialType getMaterial() {
        return material;
    }

    public void setMaterialType(EMaterialType material) {
        this.material = material;
    }
}
