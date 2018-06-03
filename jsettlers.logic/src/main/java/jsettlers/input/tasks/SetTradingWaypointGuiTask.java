/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.action.SetTradingWaypointAction.EWaypointType;

public class SetTradingWaypointGuiTask extends SimpleBuildingGuiTask {

	private EWaypointType waypointType;
	private ShortPoint2D position;

	public SetTradingWaypointGuiTask() {
	}

	public SetTradingWaypointGuiTask(EGuiAction guiAction, byte playerId, ShortPoint2D buildingPos, EWaypointType waypointType, ShortPoint2D position) {
		super(guiAction, playerId, buildingPos);
		this.waypointType = waypointType;
		this.position = position;
	}

	public EWaypointType getWaypointType() {
		return waypointType;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		dos.writeByte(waypointType.ordinal());
		serializePosition(dos, position);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		waypointType = EWaypointType.VALUES[dis.readByte()];
		position = deserializePosition(dis);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((waypointType == null) ? 0 : waypointType.hashCode());
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
		SetTradingWaypointGuiTask other = (SetTradingWaypointGuiTask) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return waypointType == other.waypointType;
	}

	@Override
	public String toString() {
		return "SetTradingWaypointGuiTask [waypointType=" + waypointType + ", position=" + position + ", getBuildingPos()=" + getBuildingPos()
				+ ", getGuiAction()=" + getGuiAction() + ", getPlayer()=" + getPlayerId() + "]";
	}
}
