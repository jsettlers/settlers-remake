package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;

public class NodeStatus implements Serializable {
	private static final long serialVersionUID = -705132187133760858L;

	private enum Status {
		Success,
		Failure,
		Running
	}
	public static final NodeStatus Success = new NodeStatus(Status.Success);
	public static final NodeStatus Failure = new NodeStatus(Status.Failure);
	public static final NodeStatus Running = new NodeStatus(Status.Running);
	public static NodeStatus Running(Object value) {
		return new NodeStatus(Status.Running, value);
	}
	public static NodeStatus Running() {
		return Running(null);
	}
	
	private Object value;
	private final Status status;
	
	private NodeStatus(Status s) { 
		status = s;
	}
	
	private NodeStatus(Status s, Object value) {
		status = s;
		this.value = value;
	}
	
	public boolean equals(NodeStatus other) {
		return this.status == other.status;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return status.toString();
	}
}
