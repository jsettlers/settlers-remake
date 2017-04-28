package jsettlers.logic.movable.components;

import java.util.HashMap;
import java.util.Map;

import jsettlers.common.material.EMaterialType;

/**
 * @author homoroselaps
 */

public class MultiMaterialComponent extends MaterialComponent {
    private static final long serialVersionUID = -2141241181575955088L;
    private final Map<EMaterialType,Integer> materials = new HashMap<>();
    private int sum = 0;

    public void addMaterial(EMaterialType material) {
        if (material == null || material == EMaterialType.NO_MATERIAL) return;
        materials.put(material,materials.getOrDefault(material,0)+1);
        sum++;
        super.setMaterial(EMaterialType.BASKET);
    }

    @Override
    public void setMaterial(EMaterialType material) {
        addMaterial(material);
    }

    public EMaterialType removeMaterial(EMaterialType material) {
        int amount = materials.getOrDefault(material,0);
        EMaterialType result = EMaterialType.NO_MATERIAL;
        if (amount > 0) {
            materials.put(material, amount-1);
            sum--;
            result = material;
        }
        if (isEmpty()) super.setMaterial(EMaterialType.NO_MATERIAL);
        return result;
    }

    public EMaterialType removeMaterial() {
        EMaterialType result = EMaterialType.NO_MATERIAL;
        for (EMaterialType material : materials.keySet()) {
            int amount = materials.getOrDefault(material,0);
            if (amount > 0) {
                materials.put(material, amount-1);
                sum--;
                result = material;
            }
        }
        if (isEmpty()) super.setMaterial(EMaterialType.NO_MATERIAL);
        return result;
    }

    public boolean isEmpty() {
        return sum <= 0;
    }
}
