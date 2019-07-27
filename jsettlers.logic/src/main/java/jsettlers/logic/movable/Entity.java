package jsettlers.logic.movable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java8.util.Optional;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.Component;
import jsettlers.logic.movable.components.DonkeyComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.MultiMaterialComponent;
import jsettlers.logic.movable.components.SpecialistComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * @author homoroselaps
 */

public class Entity implements Serializable, IScheduledTimerable {
	private static final long serialVersionUID = -5615478576016074072L;

	private final int id;

	private boolean debug;

	private final Map<Class<? extends Component>, Component> components      = new IdentityHashMap<>();
	private final Map<Class<? extends Component>, Component> componentLookup = new IdentityHashMap<>();

	private Set<Notification> notificationsNext;
	private Set<Notification> notificationsCurrent;

	public Set<Notification> getAllNotifications() {
		return notificationsCurrent;
	}


	public enum State {
		ACTIVE,
		INACTIVE,
		UNINITALIZED
	}

	private State state;
	private int   invocationDelay;

	public Entity() {
		id = MovableDataManager.getNextID();
		state = State.UNINITALIZED;
		notificationsNext = new HashSet<>();
		notificationsCurrent = new HashSet<>();
		resetInvokationDelay();
	}

	public Entity(Component... cs) {
		this();
		for (Component c : cs) {
			add(c);
		}
	}

	/**
	 * Checks whether or not all Component dependencies are satisfied.
	 * @return {@code true} if all Component dependencies are satisfied, {@code false} otherwise.
	 */
	boolean checkComponentDependencies() {
		for (Class<? extends Component> cmp : this.componentLookup.keySet()) {
			Requires ann = cmp.getAnnotation(Requires.class);
			if (ann == null) { continue; }
			for (Class<? extends Component> dependency : ann.value()) {
				assert componentLookup.containsKey(dependency) : componentLookup.get(cmp).getClass().getName() + "[" + cmp.getName() + "]: " + dependency.getName() + " missing";
			}
		}
		return true;
	}

	void toggleDebug() {
		debug = !debug;
	}

	public boolean isInDebugMode() {
		return debug;
	}

	private int resetInvokationDelay() {
		int lastValue = invocationDelay;
		invocationDelay = -1;
		return lastValue;
	}

	public void setInvocationDelay(int delay) {
		invocationDelay = invocationDelay > 0 ? Math.min(invocationDelay, delay) : delay;
	}

	public boolean isActive() {
		return state == State.ACTIVE;
	}

	public void setActive(boolean active) {
		if (active == isActive()) {
			return;
		}

		if (state == State.UNINITALIZED) {
			initialize();
		}

		if (active) {
			state = State.ACTIVE;
			for (Component c : components.values()) {
				c.enable();
			}
		} else {
			state = State.INACTIVE;
			for (Component c : components.values()) {
				c.disable();
			}
		}
	}

	private void initialize() {
		for (Component component : components.values()) {
			component.wakeUp();
		}
		RescheduleTimer.add(this, Constants.MOVABLE_INTERRUPT_PERIOD);
	}

	private void invokeUpdate() {
		for (Component component : components.values()) {
			component.update();
		}
		for (Component component : components.values()) {
			component.lateUpdate();
		}
	}

	private void invokeDestroy() {
		for (Component component : components.values()) {
			component.destroy();
		}
	}

	public int getID() {
		return id;
	}

	public void add(Component c) {
		Class cls = c.getClass();
		assert !componentLookup.containsKey(cls) : "Component already registered" + cls.getName();
		componentLookup.put(cls, c);
		components.put(cls, c);
		c.entity = this;
		// Iterate over all super classes
		cls = cls.getSuperclass();
		while (cls != null && cls != Component.class) {
			assert !componentLookup.containsKey(cls) : "Component already registered" + cls.getName();
			componentLookup.put(cls, c);
			cls = cls.getSuperclass();
		}
	}

	public Component remove(Class<? extends Component> c) {
		Component result = componentLookup.remove(c);
		components.remove(c);

		Class cls = c.getSuperclass();
		while (cls != null && cls != Component.class) {
			componentLookup.remove(cls);
			cls = cls.getSuperclass();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public <C extends Component> C getComponent(Class<C> c) {
		return (C) componentLookup.get(c);
	}

	@SuppressWarnings("unchecked")
	public <C extends Component> Optional<C> getComponentOptional(Class<C> c) {
		return Optional.ofNullable((C) componentLookup.get(c));
	}

	public boolean containsComponent(Class<? extends Component> c) {
		return componentLookup.containsKey(c);
	}

	public void raiseNotification(Notification note) {
		notificationsNext.add(note);
	}

	public void convertTo(Entity blueprint) {
		// remove all unused components
		for (Class<? extends Component> cls : components.keySet()) {
			if (!blueprint.components.containsKey(cls)) {
				remove(cls);
			}
		}
		// add all new components
		List<Component> newComponents = new ArrayList<>();
		for (Class<? extends Component> cls : blueprint.components.keySet()) {
			// ignore components we already have
			if (components.containsKey(cls)) {
				continue;
			}
			Component c = blueprint.remove(cls);
			newComponents.add(c);
			add(c);
		}
		if (state != State.UNINITALIZED) {
			// initialize all new components
			for (Component c : newComponents) {
				c.wakeUp();
			}
			if (state == State.ACTIVE) {
				for (Component c : newComponents) {
					c.enable();
				}
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(30);
		sb.append("Entity");
		sb.append("(").append(id).append(")");
		sb.append("[");
		for (Component c : components.values()) {
			sb.append(c).append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public int timerEvent() {
		if (!isActive()) { return -1; }

		invokeUpdate();

		notificationsCurrent = notificationsNext;
		notificationsNext = new HashSet<>();

		return Math.max(resetInvokationDelay(), Constants.MOVABLE_INTERRUPT_PERIOD);
	}

	@Override
	public void kill() {
		setActive(false);
		invokeDestroy();
	}

	public final AnimationComponent getAnimationComponent() {
		return getComponent(AnimationComponent.class);
	}

	public final SteeringComponent steeringComponent() {
		return getComponent(SteeringComponent.class);
	}

	public final MovableComponent movableComponent() {
		return getComponent(MovableComponent.class);
	}

	public final BearerComponent bearerComponent() {
		return getComponent(BearerComponent.class);
	}

	public final SpecialistComponent specialistComponent() {
		return getComponent(SpecialistComponent.class);
	}

	public final GameFieldComponent gameFieldComponent() {
		return getComponent(GameFieldComponent.class);
	}

	public final MaterialComponent materialComponent() { return getComponent(MaterialComponent.class); }

	public final MultiMaterialComponent multiMaterialComponent() { return getComponent(MultiMaterialComponent.class); }

	public final DonkeyComponent donkeyComponent() { return getComponent(DonkeyComponent.class); }

	public final AttackableComponent attackableComponent() { return getComponent(AttackableComponent.class); }
}
