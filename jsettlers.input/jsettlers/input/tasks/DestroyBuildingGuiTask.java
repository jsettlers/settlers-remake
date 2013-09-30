package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.position.ShortPoint2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class DestroyBuildingGuiTask extends SimpleGuiTask {
	private ShortPoint2D position;

	public DestroyBuildingGuiTask() {
	}

	/**
	 * 
	 * @param playerId
	 * @param position
	 */
	public DestroyBuildingGuiTask(byte playerId, ShortPoint2D position) {
		super(EGuiAction.DESTROY_BUILDING, playerId);
		this.position = position;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, position);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		position = SimpleGuiTask.deserializePosition(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DestroyBuildingGuiTask other = (DestroyBuildingGuiTask) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
}
