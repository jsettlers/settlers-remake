package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;

/**
 * @author homoroselaps
 */

public class MaterialComponent extends Component {
    private static final long serialVersionUID = -3337241844215162194L;
    private EMaterialType material = EMaterialType.NO_MATERIAL;

    public EMaterialType getMaterial() {
        return material;
    }

    public void setMaterialType(EMaterialType material) {
        this.material = material;
    }
}
