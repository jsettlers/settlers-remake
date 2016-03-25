package jsettlers.algorithms.path.astar;

public final class AStarOptions {
	public short partition = -1;
	public boolean includeMovables = false;
	public AStarOptions setPartition(short value) {
		this.partition = value;
		return this;
	}
	public AStarOptions setIncludeMovables(boolean value) {
		this.includeMovables = value;
		return this;
	}
}
