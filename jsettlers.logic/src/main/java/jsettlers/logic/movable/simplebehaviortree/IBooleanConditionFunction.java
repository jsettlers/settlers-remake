package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface IBooleanConditionFunction<T> extends Function<T,Boolean>, Serializable { }
