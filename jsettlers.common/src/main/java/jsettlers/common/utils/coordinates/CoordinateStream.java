package jsettlers.common.utils.coordinates;

import java8.util.Optional;

import jsettlers.common.utils.interfaces.ICoordinateConsumer;
import jsettlers.common.utils.interfaces.ICoordinateFunction;
import jsettlers.common.utils.interfaces.ICoordinatePredicate;

/**
 * Created by Andreas Eberle on 12.01.2017.
 */

public abstract class CoordinateStream {

	public CoordinateStream filter(ICoordinatePredicate predicate) {
		return new CoordinateStream() {
			@Override
			public <T> Optional<T> iterate(ICoordinateFunction<Optional<T>> function) {
				return CoordinateStream.this.iterate((x, y) -> predicate.test(x, y) ? function.apply(x, y) : Optional.empty());
			}
		};
	}

	public CoordinateStream filterBounds(int width, int height) {
		return filterBounds(0, 0, width, height);
	}

	/**
	 * 
	 * @param xStart
	 *            inclusive
	 * @param yStart
	 *            inclusive
	 * @param xEnd
	 *            exclusive
	 * @param yEnd
	 *            exclusive
	 * @return
	 */
	public CoordinateStream filterBounds(int xStart, int yStart, int xEnd, int yEnd) {
		return filter((x, y) -> xStart <= x && x < xEnd && yStart <= y && y < yEnd);
	}

	public abstract <T> Optional<T> iterate(ICoordinateFunction<Optional<T>> function);

	public void forEach(ICoordinateConsumer consumer) {
		iterate((x, y) -> {
			consumer.accept(x, y);
			return Optional.empty();
		});
	}
}
