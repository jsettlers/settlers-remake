package jsettlers.logic.movable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jsettlers.logic.movable.components.Component;

/**
 * @author homoroselaps
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Requires {
    Class<? extends Component>[] value();
}
