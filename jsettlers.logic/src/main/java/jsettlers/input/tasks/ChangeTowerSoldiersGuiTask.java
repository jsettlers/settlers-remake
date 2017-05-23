/*******************************************************************************
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.input.tasks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.common.movable.ESoldierType;
import jsettlers.common.position.ShortPoint2D;

/**
 *
 * @author Andreas Eberle
 *
 */
public class ChangeTowerSoldiersGuiTask extends SimpleBuildingGuiTask {

	private EChangeTowerSoldierTaskType taskType;
	private ESoldierType soldierType;

	public ChangeTowerSoldiersGuiTask() {
	}

	/**
	 * 
	 * @param playerId
	 * @param buildingPosition
	 * @param taskType
	 * @param soldierType
	 */
	public ChangeTowerSoldiersGuiTask(byte playerId, ShortPoint2D buildingPosition, EChangeTowerSoldierTaskType taskType, ESoldierType soldierType) {
		super(EGuiAction.CHANGE_TOWER_SOLDIERS, playerId, buildingPosition);
		this.taskType = taskType;
		this.soldierType = soldierType;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeByte(taskType.ordinal());
		dos.writeByte(soldierType != null ? soldierType.ordinal() : -1);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		taskType = EChangeTowerSoldierTaskType.values()[dis.readByte()];
		byte soldierTypeValue = dis.readByte();
		soldierType = soldierTypeValue >= 0 ? ESoldierType.values()[soldierTypeValue] : null;
	}

	public EChangeTowerSoldierTaskType getTaskType() {
		return taskType;
	}

	public ESoldierType getSoldierType() {
		return soldierType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		ChangeTowerSoldiersGuiTask that = (ChangeTowerSoldiersGuiTask) o;

		if (taskType != that.taskType)
			return false;
		return soldierType == that.soldierType;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (taskType != null ? taskType.hashCode() : 0);
		result = 31 * result + (soldierType != null ? soldierType.hashCode() : 0);
		return result;
	}

	public enum EChangeTowerSoldierTaskType {
		MORE,
		LESS,
		FULL,
		ONE
	}

}
