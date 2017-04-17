package jsettlers.logic.movable.simplebehaviortree;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface INodeStatusActionConsumer<T> extends Consumer<T>, Serializable { }
