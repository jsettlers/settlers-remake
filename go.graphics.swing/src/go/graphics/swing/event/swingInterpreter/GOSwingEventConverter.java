/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package go.graphics.swing.event.swingInterpreter;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.interpreter.AbstractEventConverter;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;

/**
 * This class listens to swing events, converts them to a go events and sends them to handlers.
 * 
 * @author michael
 */
public class GOSwingEventConverter extends AbstractEventConverter implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

	private static final int MOUSE_MOVE_TRESHOLD = 10;

	private static final double MOUSE_TIME_TRSHOLD = 5;

	/**
	 * Are we currently panning with button 3?
	 */
	private boolean panWithButton3;

	private static Window referenceWindowToCalculateRetinaScale;

	/**
	 * Creates a new event converter, that converts swing events to go events.
	 * 
	 * @param component
	 *            The component.
	 * @param provider
	 *            THe provider to which to send the events.
	 */
	public GOSwingEventConverter(Component component, GOEventHandlerProvider provider) {
		super(provider);

		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);

		addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW, Replacement.COMMAND_SELECT, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
		addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN, Replacement.COMMAND_ACTION, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
	}

	public static void setReferenceWindowToCalculateRetinaScale(Window window) {

		referenceWindowToCalculateRetinaScale = window;
	}

	private UIPoint convertToLocal(MouseEvent e) {
		int scale = determineRetinaScaleFactor();
		return new UIPoint(e.getX() * scale, (e.getComponent().getHeight() - e.getY()) * scale);

	}

	private int determineRetinaScaleFactor() {
		int scale = 1;

		GraphicsConfiguration config = referenceWindowToCalculateRetinaScale.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();

		try {
			Field field = myScreen.getClass().getDeclaredField("scale");
			if (field != null) {
				field.setAccessible(true);
				Object scaleOfField = field.get(myScreen);
				if (scaleOfField instanceof Integer) {
					scale = ((Integer) scaleOfField).intValue();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return scale;
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
		String text = getKeyName(e);
		startKeyEvent(text);
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

	private String getKeyName(KeyEvent e) {
		String text = KeyEvent.getKeyText(e.getKeyCode());
		if (text == null || text.length() != 1) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				text = "LEFT";
				break;
			case KeyEvent.VK_RIGHT:
				text = "RIGHT";
				break;
			case KeyEvent.VK_DOWN:
				text = "DOWN";
				break;
			case KeyEvent.VK_UP:
				text = "UP";
				break;
			case KeyEvent.VK_PAUSE:
				text = "PAUSE";
				break;
			case KeyEvent.VK_F1:
				text = "F1";
				break;
			case KeyEvent.VK_F2:
				text = "F2";
				break;
			case KeyEvent.VK_F3:
				text = "F3";
				break;
			case KeyEvent.VK_F4:
				text = "F4";
				break;
			case KeyEvent.VK_F5:
				text = "F5";
				break;
			case KeyEvent.VK_F6:
				text = "F6";
				break;
			case KeyEvent.VK_F7:
				text = "F7";
				break;
			case KeyEvent.VK_F8:
				text = "F8";
				break;
			case KeyEvent.VK_F9:
				text = "F9";
				break;
			case KeyEvent.VK_F10:
				text = "F10";
				break;
			case KeyEvent.VK_F11:
				text = "F11";
				break;
			case KeyEvent.VK_F12:
				text = "F12";
				break;
			case KeyEvent.VK_PLUS:
				text = "+";
				break;
			case KeyEvent.VK_MINUS:
				text = "-";
				break;
			case KeyEvent.VK_DELETE:
				text = "DELETE";
				break;
			case KeyEvent.VK_SPACE:
				text = " ";
				break;
			case KeyEvent.VK_ESCAPE:
				text = "ESCAPE";
				break;
			case KeyEvent.VK_BACK_SPACE:
				text = "BACK_SPACE";
				break;
			default:
				text = "";
			}
		}
		return text;
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
		endKeyEvent(getKeyName(e));
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		float factor = (float) Math.exp(-e.getUnitsToScroll() / 20.0);
		startZoom();
		endZoomEvent(factor);
	}
}
