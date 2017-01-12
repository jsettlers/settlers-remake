package jsettlers.common.utils.coordinates;

import java8.util.Optional;
import jsettlers.common.utils.interfaces.ICoordinateFunction;

/**
 * Created by Andreas Eberle on 12.01.2017.
 */

public interface ICoordinateProducer{
	<T> Optional<T> iterate(ICoordinateFunction<Optional<T>> function);
}
