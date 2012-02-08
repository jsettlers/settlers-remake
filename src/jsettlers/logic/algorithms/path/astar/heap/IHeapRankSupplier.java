package jsettlers.logic.algorithms.path.astar.heap;

/**
 * Gives the rank of an element by its ID
 * 
 * @author Andreas Eberle
 * 
 */
public interface IHeapRankSupplier {

	/**
	 * 
	 * @param identifier
	 *            element to get the rank for
	 * @return rank of the element identified by the given identifier.
	 */
	float getHeapRank(int identifier);

	int getHeapIdx(int identifier);

	void setHeapIdx(int identifier, int idx);
}
