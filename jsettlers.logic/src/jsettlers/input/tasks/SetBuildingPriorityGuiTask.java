package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;

/**
 * This task is used to set the priority of a building.
 * 
 * @author Andreas Eberle
 * 
 */
public class SetBuildingPriorityGuiTask extends SimpleGuiTask {
	private ShortPoint2D buildingPosition;
	private EPriority newPriority;

	public SetBuildingPriorityGuiTask() {
	}

	public SetBuildingPriorityGuiTask(byte playerId, ShortPoint2D buildingPosition, EPriority newPriority) {
		super(EGuiAction.SET_BUILDING_PRIORITY, playerId);
		this.buildingPosition = buildingPosition;
		this.newPriority = newPriority;
	}

	/**
	 * 
	 * @return Returns the position of the building that shall get the new priority.
	 */
	public ShortPoint2D getBuildingPosition() {
		return buildingPosition;
	}

	/**
	 * 
	 * @return Returns the new priority that shall be set to the building.
	 */
	public EPriority getNewPriority() {
		return newPriority;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, buildingPosition);
		dos.writeByte(newPriority.ordinal);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		buildingPosition = SimpleGuiTask.deserializePosition(dis);
		newPriority = EPriority.values[dis.readByte()];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((buildingPosition == null) ? 0 : buildingPosition.hashCode());
		result = prime * result + ((newPriority == null) ? 0 : newPriority.hashCode());
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
		SetBuildingPriorityGuiTask other = (SetBuildingPriorityGuiTask) obj;
		if (buildingPosition == null) {
			if (other.buildingPosition != null)
				return false;
		} else if (!buildingPosition.equals(other.buildingPosition))
			return false;
		if (newPriority != other.newPriority)
			return false;
		return true;
	}
}
