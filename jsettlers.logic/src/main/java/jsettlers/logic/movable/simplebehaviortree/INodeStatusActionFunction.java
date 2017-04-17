package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface INodeStatusActionFunction<T> extends Function<T, NodeStatus>, Serializable { }
