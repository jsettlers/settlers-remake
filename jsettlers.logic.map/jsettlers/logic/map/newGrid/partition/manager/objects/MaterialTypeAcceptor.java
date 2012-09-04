package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.algorithms.queue.ITypeAcceptor;

public final class MaterialTypeAcceptor implements ITypeAcceptor<MaterialOffer>, Serializable {
	private static final long serialVersionUID = 635444536013281565L;

	public EMaterialType materialType = null;

	@Override
	public final boolean accepts(MaterialOffer offer) {
		return this.materialType == offer.materialType;
	}
}