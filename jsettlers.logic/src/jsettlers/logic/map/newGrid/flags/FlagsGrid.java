package jsettlers.logic.map.newGrid.flags;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.map.newGrid.partition.IPartitionsGridBlockingProvider;

/**
 * Grid that's storing the blocked information for fast access.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FlagsGrid implements Serializable, IBlockingProvider, IPartitionsGridBlockingProvider, IProtectedProvider {
	private static final long serialVersionUID = -413005884613149208L;

	private final short width;

	private final BitSet blockedGrid;
	private final BitSet markedGrid;
	private final BitSet protectedGrid;
	private final BitSet bordersGrid;

	private IBlockingChangedListener blockingChangedListener = null;
	private IProtectedChangedListener protectedChangedListener = null;

	private transient IContainingProvider blockedContainingProvider;

	public FlagsGrid(final short width, final short height) {
		this.width = width;

		this.blockedGrid = new BitSet(width * height);
		this.protectedGrid = new BitSet(width * height);
		this.markedGrid = new BitSet(width * height);
		this.bordersGrid = new BitSet(width * height);

		initAdditional();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		initAdditional();
	}

	private void initAdditional() {
		this.blockedContainingProvider = new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return blockedGrid.get(x + y * width);
			}
		};
	}

	@Override
	public boolean isBlocked(int x, int y) {
		return blockedGrid.get(x + y * width);
	}

	/**
	 * Sets this position's blocked and protected.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param blocked
	 *            the position will be set to blocked and protected if blocked == true<br>
	 *            otherwise it will be set to unblocked and unprotected.
	 */
	public void setBlockedAndProtected(int x, int y, boolean blockedAndProtected) {
		setBlockedAndProtected(x, y, blockedAndProtected, blockedAndProtected);
	}

	/**
	 * Sets this position's blocked and protected.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param blocked
	 *            new blocked value of this position
	 * @param newProtected
	 *            new protected value of this position
	 */
	public void setBlockedAndProtected(int x, int y, boolean blocked, boolean newProtected) {
		final int idx = x + y * width;
		this.blockedGrid.set(idx, blocked);
		this.protectedGrid.set(idx, newProtected);

		if (blockingChangedListener != null) {
			this.blockingChangedListener.blockingChanged(x, y, blocked);
		}
		if (protectedChangedListener != null) {
			this.protectedChangedListener.protectedChanged(x, y, newProtected);
		}
	}

	public boolean isMarked(int x, int y) {
		return this.markedGrid.get(x + y * width);
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid.set(x + y * width, marked);
	}

	@Override
	public boolean isProtected(int x, int y) {
		return this.protectedGrid.get(x + y * width);
	}

	public void setProtected(int x, int y, boolean newProtected) {
		this.protectedGrid.set(x + y * width, newProtected);

		if (protectedChangedListener != null) {
			this.protectedChangedListener.protectedChanged(x, y, newProtected);
		}
	}

	public boolean isBorderAt(int x, int y) {
		return this.bordersGrid.get(x + y * width);
	}

	public void setBorderAt(short x, short y, boolean setProtected) {
		this.bordersGrid.set(x + y * width, setProtected);
	}

	@Override
	public void registerBlockingChangedListener(IBlockingChangedListener listener) {
		this.blockingChangedListener = listener;
	}

	@Override
	public void setProtectedChangedListener(IProtectedChangedListener protectedChangedListener) {
		this.protectedChangedListener = protectedChangedListener;
	}

	/**
	 * 
	 * @return Returns an {@link IContainingProvider} that returns true for every blocked position.
	 */
	public IContainingProvider getBlockedContainingProvider() {
		return blockedContainingProvider;
	}

}
