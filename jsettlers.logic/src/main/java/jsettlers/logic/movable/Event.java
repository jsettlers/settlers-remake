package jsettlers.logic.movable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Event<T> {

    private Set<Consumer<T>> listeners = new HashSet<Consumer<T>>();

    public void subscribe(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void trigger(T args) {
        listeners.forEach(x -> x.accept(args));
    }
}
