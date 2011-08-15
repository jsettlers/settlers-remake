package go.event.swingInterpreter;

import go.event.GOEvent;
import go.event.GOEventHandler;
import go.event.GOEventHandlerProvoder;
import go.event.GOKeyEvent;
import go.event.SingleHandlerGoEvent;
import go.event.command.GOCommandEvent;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class listens to swing events, converts them to a go events and sends
 * them to handlers.
 * 
 * @author michael
 */
public class GOSwingEventConverter implements MouseListener,
        MouseMotionListener, KeyListener {

	private static final int MOUSE_MOVE_TRESHOLD = 10;

	private static final int KEYPAN = 20;

	private final GOEventHandlerProvoder provider;

	GOKeyEvent ongoingKeyEvent = null;

	ConvertedDrawEvent ongoingMouseEvent;
	
	ConvertedHoverEvent ongoingHoverEvent;

	ConvertedCommandEvent ongoingCommandEvent = null;

	private ConvertedPanEvent ongoingPanEvent;

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
	public GOSwingEventConverter(Component component,
	        GOEventHandlerProvoder provider) {
		this.provider = provider;

		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
	}

	private Point convertToLocal(MouseEvent e) {
		return new Point(e.getX(), e.getComponent().getHeight() - e.getY());
	}

	/**
	 * trys to cancel the first found ongoing event.
	 */
	protected void tryCancelCurrentEvent() {
		if (this.ongoingMouseEvent != null) {
			this.ongoingMouseEvent.aborted();
			this.ongoingMouseEvent = null;
		} /*
		 * else if (ongoingCommandEvent != null) {
		 * ongoingCommandEvent.aborted(); ongoingCommandEvent = null; }
		 */
		else if (this.ongoingPanEvent != null) {
			this.ongoingPanEvent.aborted();
			this.ongoingPanEvent = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (ongoingHoverEvent == null) {
			ongoingHoverEvent = new ConvertedHoverEvent(convertToLocal(e));

			this.provider.handleEvent(this.ongoingHoverEvent);

			this.ongoingHoverEvent.initialized();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (ongoingHoverEvent == null) {
			mouseEntered(e);
		}
		this.ongoingHoverEvent.setMousePosition(convertToLocal(e));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (ongoingHoverEvent != null) {
			ongoingHoverEvent.released();
			ongoingHoverEvent = null;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	private class ConvertedCommandEvent extends SingleHandlerGoEvent implements
	        GOCommandEvent {

		private final Point position;
		private final boolean selecting;

		public ConvertedCommandEvent(Point position, boolean selecting) {
			this.position = position;
			this.selecting = selecting;
		}

		public void initialized() {
			setPhase(PHASE_STARTED);
			setPhase(PHASE_MODAL);
			setPhase(PHASE_FINISHED);
		}

		@Override
		public Point getCommandPosition() {
			return this.position;
		}

		@Override
		public boolean isSelecting() {
			return this.selecting;
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int mouseButton = e.getButton();
		if (this.ongoingMouseEvent == null && mouseButton == MouseEvent.BUTTON1) {
			this.ongoingMouseEvent = new ConvertedDrawEvent(convertToLocal(e));

			this.provider.handleEvent(this.ongoingMouseEvent);

			this.ongoingMouseEvent.initialized();
		} else {
			boolean isPanClick =
			        mouseButton == MouseEvent.BUTTON2
			                || mouseButton == MouseEvent.BUTTON3;
			if (this.ongoingPanEvent == null && isPanClick) {
				this.ongoingPanEvent = new ConvertedPanEvent(convertToLocal(e));

				this.provider.handleEvent(this.ongoingPanEvent);
				this.panWithButton3 = mouseButton == MouseEvent.BUTTON3;

				this.ongoingPanEvent.initialized();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.ongoingHoverEvent != null) {
			this.ongoingHoverEvent.setMousePosition(convertToLocal(e));
		}
		if (this.ongoingMouseEvent != null) {
			this.ongoingMouseEvent.setMousePosition(convertToLocal(e));
		}
		if (this.ongoingPanEvent != null) {
			this.ongoingPanEvent.setMousePosition(convertToLocal(e));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.ongoingMouseEvent != null && e.getButton() == MouseEvent.BUTTON1) {
			button1Released(e);
		} else if (this.ongoingPanEvent != null && this.panWithButton3
		        && e.getButton() == MouseEvent.BUTTON3) {
			button3Released(e);

		} else if (this.ongoingPanEvent != null && !this.panWithButton3
		        && e.getButton() == MouseEvent.BUTTON2) {
			button2Released(e);
		}
	}

	private void button2Released(MouseEvent e) {
	    this.ongoingPanEvent.setMousePosition(convertToLocal(e));
	    this.ongoingPanEvent.released();
	    this.ongoingPanEvent = null;
    }

	private void button3Released(MouseEvent e) {
	    this.ongoingPanEvent.setMousePosition(convertToLocal(e));
	    boolean justClick =
	            this.ongoingPanEvent.getMouseMoved() < MOUSE_MOVE_TRESHOLD;
	    if (justClick) {
	    	sendCommandEvent(e, this.ongoingPanEvent);
	    } else {
	    	this.ongoingPanEvent.released();
	    }
	    this.ongoingPanEvent = null;
    }

	private void button1Released(MouseEvent e) {
	    this.ongoingMouseEvent.setMousePosition(convertToLocal(e));
	    boolean justClick =
	            this.ongoingMouseEvent.getMouseMoved() < MOUSE_MOVE_TRESHOLD;
	    if (justClick) {
	    	sendCommandEvent(e, this.ongoingMouseEvent);
	    } else {
	    	this.ongoingMouseEvent.released();
	    }
	    this.ongoingMouseEvent = null;
    }

	private void sendCommandEvent(MouseEvent e, AbstractMouseEvent replacedEvent) {
		// send a command event.
		ConvertedCommandEvent commandEvent =
		        new ConvertedCommandEvent(convertToLocal(e),
		                e.getButton() == MouseEvent.BUTTON1);

		this.provider.handleEvent(commandEvent);

		commandEvent.initialized();

		if (commandEvent.getHandler() != null) {
			// only abort if command was handeled.
			replacedEvent.aborted();
		} else {
			replacedEvent.released();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (this.ongoingKeyEvent == null) {
			this.ongoingKeyEvent = new GOKeyEvent(e.getKeyCode());

			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.ongoingKeyEvent.setHandler(getCancelHandler());
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				this.ongoingKeyEvent.setHandler(getPanHandler(0, -KEYPAN));
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.ongoingKeyEvent.setHandler(getPanHandler(0, KEYPAN));
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				this.ongoingKeyEvent.setHandler(getPanHandler(KEYPAN, 0));
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				this.ongoingKeyEvent.setHandler(getPanHandler(-KEYPAN, 0));
			}

			this.provider.handleEvent(this.ongoingKeyEvent);

			this.ongoingKeyEvent.started();
		}
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
	    this.provider.handleEvent(event);
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
		if (this.ongoingKeyEvent != null) {
			this.ongoingKeyEvent.released();
			this.ongoingKeyEvent = null;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
