package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.utils.UIPanel;

/**
 * This are the main content types
 * 
 * @author michael
 */
public enum EContentType implements IContentProvider {
	EMPTY(ESecondaryTabType.NONE, null),

	BUILD_NORMAL(ESecondaryTabType.BUILD, BuildingBuildContent.getNormal()),

	BUILD_SOCIAL(ESecondaryTabType.BUILD, BuildingBuildContent.getSocial()),

	BUILD_MILITARY(ESecondaryTabType.BUILD, BuildingBuildContent.getMilitary()),

	BUILD_FOOD(ESecondaryTabType.BUILD, BuildingBuildContent.getFood()),

	STOCK(ESecondaryTabType.SETTLERS, null),
	TOOLS(ESecondaryTabType.SETTLERS, null),
	GOODS_SPREAD(ESecondaryTabType.SETTLERS, null),
	GOODS_TRANSPORT(ESecondaryTabType.SETTLERS, new MaterialPriorityPanel()),

	SETTLERSTATISTIC(ESecondaryTabType.GOODS, null),
	PROFESSION(ESecondaryTabType.GOODS, null),
	WARRIORS(ESecondaryTabType.GOODS, null),
	PRODUCTION(ESecondaryTabType.GOODS, null);

	private final ESecondaryTabType tabs;
	private final ContentFactory factory;

	private EContentType(ESecondaryTabType tabs, ContentFactory factory) {
		this.tabs = tabs;
		this.factory = factory;

	}

	@Override
	public UIPanel getPanel() {
		if (factory == null) {
			return new UIPanel();
		} else {
			return factory.getPanel();
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return tabs;
	}

	@Override
	public void displayBuildingBuild(EBuildingType type) {
		if (factory != null) {
			factory.displayBuildingBuild(type);
		}
	}

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		if (factory != null) {
			factory.showMapPosition(pos, grid);
		}
	}

}
