/*******************************************************************************
 * Copyright (c) 2016 -2017
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

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This task is used to set the stock configuration either globally or locally.
 * 
 * @author Andreas Eberle
 * 
 */
public class SetAcceptedStockMaterialGuiTask extends SimpleGuiTask {
	private ShortPoint2D position;
	private EMaterialType materialType;
	private boolean accepted;
	private boolean local;

	public SetAcceptedStockMaterialGuiTask() {
	}

	public SetAcceptedStockMaterialGuiTask(byte playerId, ShortPoint2D position, EMaterialType materialType, boolean accepted, boolean local) {
		super(EGuiAction.SET_ACCEPTED_STOCK_MATERIAL, playerId);
		this.position = position;
		this.materialType = materialType;
		this.accepted = accepted;
		this.local = local;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public boolean isLocal() {
		return local;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		SimpleGuiTask.serializePosition(dos, position);
		dos.writeByte(materialType.ordinal);
		dos.writeBoolean(accepted);
		dos.writeBoolean(local);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		position = SimpleGuiTask.deserializePosition(dis);
		materialType = EMaterialType.VALUES[dis.readByte()];
		accepted = dis.readBoolean();
		local = dis.readBoolean();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SetAcceptedStockMaterialGuiTask))
			return false;
		if (!super.equals(o))
			return false;

		SetAcceptedStockMaterialGuiTask that = (SetAcceptedStockMaterialGuiTask) o;

		if (accepted != that.accepted)
			return false;
		if (local != that.local)
			return false;
		if (position != null ? !position.equals(that.position) : that.position != null)
			return false;
		return materialType == that.materialType;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (position != null ? position.hashCode() : 0);
		result = 31 * result + (materialType != null ? materialType.hashCode() : 0);
		result = 31 * result + (accepted ? 1 : 0);
		result = 31 * result + (local ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SetAcceptedStockMaterialGuiTask{" +
				"position=" + position +
				", materialType=" + materialType +
				", accepted=" + accepted +
				", local=" + local +
				'}';
	}
}
