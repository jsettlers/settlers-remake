package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.ISerializablePredicate;
import jsettlers.common.utils.collections.IteratorFilter;
import jsettlers.common.utils.collections.IteratorFilter.FilteredIterator;

/**
 * This extension of {@link IteratorFilter} is specialized for the usage with {@link IMapArea}s. It allows to use the contains method on the filtered
 * area.
 * 
 * @author Andreas Eberle
 * 
 */
public class FilteredMapArea implements IMapArea {
	private static final long serialVersionUID = -5136044315417473251L;
	private final IMapArea iterable;
	private final ISerializablePredicate<ShortPoint2D> predicate;

	public FilteredMapArea(IMapArea iterable, ISerializablePredicate<ShortPoint2D> predicate) {
		this.iterable = iterable;
		this.predicate = predicate;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new FilteredIterator<ShortPoint2D>(iterable.iterator(), predicate);
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return predicate.evaluate(position) && iterable.contains(position);
	}
}
