package jsettlers.logic.movable;

/**
 * Created by jt-1 on 2/5/2017.
 */

public @interface Requires {
    Class<? extends Component>[] value();
}
