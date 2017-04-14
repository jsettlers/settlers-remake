package jsettlers.logic.movable.components;

import java.util.HashMap;
import java.util.Map;

import jsettlers.common.material.EMaterialType;

/**
 * @author homoroselaps
 */

public class MultiMaterialComponent extends Component {
    private static final long serialVersionUID = -2141241181575955088L;
    private Map<EMaterialType,Integer> materials = new HashMap<>();
    private int sum = 0;

    public void addMaterial(EMaterialType material) {
        materials.put(material,materials.getOrDefault(material,0)+1);
        sum++;
    }

    public EMaterialType removeMaterial(EMaterialType material) {
        int amount = materials.getOrDefault(material,0);
        if (amount > 0) {
            materials.put(material, amount-1);
            sum--;
            return material;
        }
        return EMaterialType.NO_MATERIAL;
    }

    public EMaterialType removeMaterial() {
        if (sum <= 0) return EMaterialType.NO_MATERIAL;
        for (EMaterialType material : materials.keySet()) {
            int amount = materials.getOrDefault(material,0);
            if (amount > 0) {
                materials.put(material, amount-1);
                sum--;
                return material;
            }
        }
        return EMaterialType.NO_MATERIAL;
    }

    public boolean isEmpty() {
        return sum <= 0;
    }
}
