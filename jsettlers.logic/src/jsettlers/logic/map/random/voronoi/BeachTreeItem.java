package jsettlers.logic.map.random.voronoi;

public interface BeachTreeItem {
	BeachTreeItem getTopChild();

	BeachTreeItem getBottomChild();

	BeachSeparator getParent();
}
