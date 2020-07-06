package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;

import java8.util.function.Function;

@FunctionalInterface
public interface IShortSupplier<T> extends Function<T, Short>, Serializable {}
