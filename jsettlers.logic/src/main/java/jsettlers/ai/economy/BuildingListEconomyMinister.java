package jsettlers.ai.economy;

import jsettlers.ai.construction.BuildingCount;
import jsettlers.common.buildings.EBuildingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author codingberlin
 */
public class BuildingListEconomyMinister {

	protected final List<EBuildingType> buildingsToBuild;

	public BuildingListEconomyMinister() {
		buildingsToBuild = new ArrayList<>();
	}

	protected int plannedCountOf(EBuildingType buildingType, List<BuildingCount> buildingCounts) {
		for (BuildingCount count : buildingCounts) {
			if (count.buildingType == buildingType) {
				return (int) count.count;
			}
		}
		return 0;
	}

	protected int currentCountOf(EBuildingType targetBuildingType) {
		int result = 0;
		for (EBuildingType buildingType : buildingsToBuild) {
			if (buildingType == targetBuildingType) {
				result++;
			}
		}
		return result;
	}

	protected void addIfPossible(EBuildingType buildingType, List<BuildingCount> buildingCounts, List<EBuildingType> buildingList) {
		if (currentCountOf(buildingType) < plannedCountOf(buildingType, buildingCounts)) {
			buildingList.add(buildingType);
		}
	}
}
