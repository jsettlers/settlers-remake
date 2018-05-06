package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;

import java8.util.function.Function;

@FunctionalInterface
public interface IBooleanConditionFunction<T> extends Function<T, Boolean>, Serializable {}
