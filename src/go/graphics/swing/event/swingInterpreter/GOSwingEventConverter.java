package go.graphics.swing.event.swingInterpreter;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOEventHandlerProvoder;
import go.graphics.event.interpreter.AbstractEventConverter;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class listens to swing events, converts them to a go events and sends them to handlers.
 * 
 * @author michael
 */
public class GOSwingEventConverter extends AbstractEventConverter implements MouseListener, MouseMotionListener, KeyListener {

	private static final int MOUSE_MOVE_TRESHOLD = 10;

	private static final int KEYPAN = 20;

	private static final double MOUSE_TIME_TRSHOLD = 5;

	/**
	 * Are we currently panning with button 3?
	 */
	private boolean panWithButton3;

	/**
	 * Creates a new event converter, that converts swing events to go events.
	 * 
	 * @param component
	 *            The component.
	 * @param provider
	 *            THe provider to which to send the events.
	 */
	public GOSwingEventConverter(Component component, GOEventHandlerProvoder provider) {
		super(provider);

		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW, Replacement.COMMAND_SELECT, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
		addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN, Replacement.COMMAND_ACTION, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
	}

	private UIPoint convertToLocal(MouseEvent e) {
		return new UIPoint(e.getX(), e.getComponent().getHeight() - e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		startHover(convertToLocal(e));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateHoverPosition(convertToLocal(e));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		endHover(convertToLocal(e));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mouseButton = e.getButton();
		UIPoint local = convertToLocal(e);
		if (mouseButton == MouseEvent.BUTTON1) {
			startDraw(local);
		} else {
			boolean isPanClick = mouseButton == MouseEvent.BUTTON2 || mouseButton == MouseEvent.BUTTON3;
			if (isPanClick) {
				startPan(local);
				panWithButton3 = mouseButton == MouseEvent.BUTTON3;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		UIPoint local = convertToLocal(e);
		updateDrawPosition(local);
		updatePanPosition(local);
		updateHoverPosition(local);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		UIPoint local = convertToLocal(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			endDraw(local);
		} else if (panWithButton3 && e.getButton() == MouseEvent.BUTTON3) {
			endPan(local);

		} else if (!panWithButton3 && e.getButton() == MouseEvent.BUTTON2) {
			endPan(local);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		startKeyEvent(KeyEvent.getKeyText(e.getKeyCode()));
		/*
		 * if (ongoingKeyEvent == null) { if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { ongoingKeyEvent.setHandler(getCancelHandler()); } else if
		 * (e.getKeyCode() == KeyEvent.VK_UP) { ongoingKeyEvent.setHandler(getPanHandler(0, -KEYPAN)); } else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		 * { ongoingKeyEvent.setHandler(getPanHandler(0, KEYPAN)); } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
		 * ongoingKeyEvent.setHandler(getPanHandler(KEYPAN, 0)); } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
		 * ongoingKeyEvent.setHandler(getPanHandler(-KEYPAN, 0)); }
		 * 
		 * provider.handleEvent(ongoingKeyEvent);
		 * 
		 * ongoingKeyEvent.started(); }
		 */
	}

	private GOEventHandler getPanHandler(final int x, final int y) {
		return new GOEventHandler() {
			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				panBy(x, y);
			}

			@Override
			public void aborted(GOEvent event) {
			}
		};
	}

	protected void panBy(int x, int y) {
		PseudoPanEvent event = new PseudoPanEvent(x, y);
		handleEvent(event);
		event.pan();
	}

	private GOEventHandler getCancelHandler() {
		return new GOEventHandler() {
			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				tryCancelCurrentEvent();
			}

			@Override
			public void aborted(GOEvent event) {
			}
		};
	}

	@Override
	public void keyReleased(KeyEvent e) {
		endKeyEvent();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
