package jsettlers.logic.movable;

import jsettlers.logic.movable.components.Component;

/**
 * @author homoroselaps
 */

public @interface Requires {
    Class<? extends Component>[] value();
}
