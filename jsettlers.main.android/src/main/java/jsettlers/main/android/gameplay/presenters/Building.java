package jsettlers.main.android.gameplay.presenters;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IBuildingCounts;

/**
 * Created by tompr on 29/05/2017.
 */

public class Building {

    private final EBuildingType buildingType;
    private final String name;
    private final String count;
    private final String constructionCount;

    public Building(EBuildingType buildingType, IBuildingCounts buildingCounts) {
        this.buildingType = buildingType;
        this.name = buildingType.name();

        if (buildingCounts != null) {
            this.count = Integer.toString(buildingCounts.buildingsInPartiton(buildingType));

            int constructionCount = buildingCounts.buildingsInPartitionUnderConstruction(buildingType);
            if (constructionCount > 0) {
                this.constructionCount = "+" + Integer.toString(constructionCount);
            } else {
                this.constructionCount = "";
            }
        } else {
            this.count = "";
            this.constructionCount = "";
        }
    }

    public EBuildingType getBuildingType() {
        return buildingType;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getConstructionCount() {
        return constructionCount;
    }
}
