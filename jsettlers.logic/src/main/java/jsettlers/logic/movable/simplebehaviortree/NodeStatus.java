package jsettlers.logic.movable.simplebehaviortree;

public class NodeStatus {
	private enum Status {
		Success,
		Failure,
		Running
	}
	public static NodeStatus Success = new NodeStatus(Status.Success);
	public static NodeStatus Failure = new NodeStatus(Status.Failure);
	public static NodeStatus Running = new NodeStatus(Status.Running);
	public static NodeStatus Running(Object value) {
		return new NodeStatus(Status.Running, value);
	}
	public static NodeStatus Running() {
		return Running(null);
	}
	
	private Object value;
	private Status status;
	
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
