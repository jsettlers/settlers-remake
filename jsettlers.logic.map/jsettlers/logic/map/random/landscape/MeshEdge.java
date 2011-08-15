package jsettlers.logic.map.random.landscape;

public class MeshEdge {
	private static final float GLOBAL_MAX_INCLINE = 1;
	private final MeshSite left;
	private final MeshSite right;
	private final Vertex start;
	private final Vertex end;
	private float maxIncline = GLOBAL_MAX_INCLINE;

	public MeshEdge(MeshSite left, MeshSite right, Vertex start, Vertex end) {
		this.left = left;
		this.right = right;
		this.start = start;
		this.end = end;
	}

	public MeshSite getOppositeSite(MeshSite site) {
		if (site == left) {
			return getRight();
		} else if (site == right) {
			return getLeft();
		} else {
			return null;
		}
	}

	public Vertex getOppositePoint(Vertex point) {
		if (point.equals(start)) {
			return end;
		} else if (point.equals(end)) {
			return start;
		} else {
			return null;
		}
	}

	public MeshSite getLeft() {
		return left;
	}

	public MeshSite getRight() {
		return right;
	}

	public Vertex getStart() {
		return start;
	}

	public Vertex getEnd() {
		return end;
	}

	public boolean isBorderOf(MeshSite site) {
		return site == left || site == right;
	}

	public Vertex getCounterclockPoint(MeshSite site) {
		if (site == left) {
			return end;
		} else if (site == right) {
			return start;
		} else {
			throw new IllegalArgumentException(
			        "The site does not belong to this edge");
		}
	}

	public Vertex getClockPoint(MeshSite site) {
		if (site == left) {
			return start;
		} else if (site == right) {
			return end;
		} else {
			throw new IllegalArgumentException(
			        "The site does not belong to this edge");
		}
	}

	public boolean hasPoint(Vertex point) {
		return point.equals(start) || point.equals(end);
	}

	@Override
	public String toString() {
		return "MeshEdge[" + start.toString() + "-" + end.toString() + "]";
	}

	public MeshSite getClockSite(Vertex vertex) {
		if (vertex == start) {
			return left;
		} else if (vertex == end) {
			return right;
		} else {
			throw new IllegalArgumentException(
			        "The vertex does not belong to this edge");
		}
	}

	public float getMaxIncline() {
		return maxIncline;
	}

	public void constraintMaxIncline(float maxIncline) {
		if (maxIncline < this.maxIncline && maxIncline >= 0) {
			this.maxIncline = maxIncline;
		}
	}
}
