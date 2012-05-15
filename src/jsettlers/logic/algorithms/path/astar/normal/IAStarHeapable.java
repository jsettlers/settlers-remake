package jsettlers.logic.algorithms.path.astar.normal;

public interface IAStarHeapable {
	int getHeapIdx(int elementID);

	void setHeapIdx(int elementID, int idx);

	float getHeapRank(int parentElementID);
}
