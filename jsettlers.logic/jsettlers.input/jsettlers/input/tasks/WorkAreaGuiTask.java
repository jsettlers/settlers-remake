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
public class WorkAreaGuiTask extends SimpleGuiTask {
	private ShortPoint2D workAreaPosition;
	private ShortPoint2D buildingPos;

	public WorkAreaGuiTask() {
	}

	/**
	 * 
	 * @param guiAction
	 * @param playerId
	 * @param workAreaPosition
	 * @param buildingPos
	 */
	public WorkAreaGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D workAreaPosition, ShortPoint2D buildingPos) {
		super(guiAction, playerId);
		this.workAreaPosition = workAreaPosition;
		this.buildingPos = buildingPos;
	}

	public ShortPoint2D getPosition() {
		return workAreaPosition;
	}

	public ShortPoint2D getBuildingPos() {
		return buildingPos;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, workAreaPosition);
		SimpleGuiTask.serializePosition(dos, buildingPos);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		workAreaPosition = SimpleGuiTask.deserializePosition(dis);
		buildingPos = SimpleGuiTask.deserializePosition(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((buildingPos == null) ? 0 : buildingPos.hashCode());
		result = prime * result + ((workAreaPosition == null) ? 0 : workAreaPosition.hashCode());
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
		WorkAreaGuiTask other = (WorkAreaGuiTask) obj;
		if (buildingPos == null) {
			if (other.buildingPos != null)
				return false;
		} else if (!buildingPos.equals(other.buildingPos))
			return false;
		if (workAreaPosition == null) {
			if (other.workAreaPosition != null)
				return false;
		} else if (!workAreaPosition.equals(other.workAreaPosition))
			return false;
		return true;
	}
}
