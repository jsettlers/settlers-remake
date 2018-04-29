package jsettlers.logic.map.grid.partition.manager.settings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MaterialDistributionSettingsTest {

    private static final EMaterialType TEST_MATERIAL = EMaterialType.IRON;
    private static final EBuildingType TEST_BUILDING_TYPE = EBuildingType.WEAPONSMITH;

    @Test
    public void testGetUserConfiguredDistributionValue() {
        MaterialDistributionSettings settings = new MaterialDistributionSettings(TEST_MATERIAL);
        float testValue = 0.5f;
        settings.setUserConfiguredDistributionValue(TEST_BUILDING_TYPE, testValue);

        float result = settings.getUserConfiguredDistributionValue(TEST_BUILDING_TYPE);

        assertEquals("the inserted value should be returned", testValue, result, 0f);
    }


    @Test
    public void testGetDistributionProbability() {
        MaterialDistributionSettings settings = new MaterialDistributionSettings(TEST_MATERIAL);
        settings.setUserConfiguredDistributionValue(TEST_BUILDING_TYPE, 1f);
        settings.setUserConfiguredDistributionValue(EBuildingType.TOOLSMITH, 0.7f);
        settings.setUserConfiguredDistributionValue(EBuildingType.DOCKYARD, 0.3f);

        float result = settings.getDistributionProbability(TEST_BUILDING_TYPE);

        assertEquals("the distribution probability should take every value into account", 0.5f, result, 0f);
    }

    @Test
    public void testGetMaterialType() {
        MaterialDistributionSettings settings = new MaterialDistributionSettings(TEST_MATERIAL);

        EMaterialType result = settings.getMaterialType();

        assertEquals("the type should by the same as used in theconstructor", TEST_MATERIAL, result);
    }

}
