/*******************************************************************************
 * Copyright (c) 2015
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

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;

/**
 * This sets the partition wise setting if this material should be brought to stock buildings.
 * 
 * @author Michael Zangl
 *
 */
public class SetStockAcceptsMaterialGuiTask extends SimpleGuiTask {
	private ShortPoint2D managerPosition;
	private EMaterialType materialType;
	private boolean acceptedInStock;

	public SetStockAcceptsMaterialGuiTask() {
	}

	public SetStockAcceptsMaterialGuiTask(byte playerId, ShortPoint2D managerPosition, EMaterialType materialType,
			boolean acceptedInStock) {
		super(EGuiAction.SET_STOCK_ACEPTS_MATERIAL, playerId);
		this.managerPosition = managerPosition;
		this.materialType = materialType;
		this.acceptedInStock = acceptedInStock;
	}

	@Override
	protected void serializeTask(DataOutputStream dos) throws IOException {
		super.serializeTask(dos);
		serializePosition(dos, managerPosition);
		dos.writeByte(materialType.ordinal);
		dos.writeBoolean(acceptedInStock);
	}

	@Override
	protected void deserializeTask(DataInputStream dis) throws IOException {
		super.deserializeTask(dis);
		managerPosition = deserializePosition(dis);
		materialType = EMaterialType.values[dis.readByte()];
		acceptedInStock = dis.readBoolean();
	}

	/**
	 * @return Returns a position occupied by the manager this settings shall be used for.
	 */
	public ShortPoint2D getManagerPosition() {
		return managerPosition;
	}

	/**
	 * @return Returns the {@link EMaterialType} this settings shall be used for.
	 */
	public EMaterialType getMaterialType() {
		return materialType;
	}

	/**
	 * @return <code>true</code> if this material should be brought to (default) stock buildings.
	 */
	public boolean isAcceptedInStock() {
		return acceptedInStock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (acceptedInStock ? 1231 : 1237);
		result = prime * result + ((managerPosition == null) ? 0 : managerPosition.hashCode());
		result = prime * result + ((materialType == null) ? 0 : materialType.hashCode());
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
		SetStockAcceptsMaterialGuiTask other = (SetStockAcceptsMaterialGuiTask) obj;
		if (acceptedInStock != other.acceptedInStock)
			return false;
		if (managerPosition == null) {
			if (other.managerPosition != null)
				return false;
		} else if (!managerPosition.equals(other.managerPosition))
			return false;
		if (materialType != other.materialType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SetStockAcceptsMaterialGuiTask [managerPosition=" + managerPosition + ", materialType=" + materialType + ", acceptedInStock="
				+ acceptedInStock + ", getGuiAction()=" + getGuiAction() + ", getPlayerId()=" + getPlayerId() + "]";
	}
}
