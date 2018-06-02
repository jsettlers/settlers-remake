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

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Set;

import go.graphics.UIPoint;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.command.EModifier;
import go.graphics.event.interpreter.AbstractEventConverter;

/**
 * This class listens to swing events, converts them to a go events and sends them to handlers.
 * 
 * @author michael
 */
public class GOSwingEventConverter extends AbstractEventConverter
		implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener, ComponentListener, HierarchyListener {

	private static final int MOUSE_MOVE_TRESHOLD = 10;

	private static final double MOUSE_TIME_TRSHOLD = 5;

	/**
	 * Are we currently panning with button 3?
	 */
	private boolean panWithButton3;

	private int scaleFactor = 1;

	private int modifiers;

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
		component.addHierarchyListener(this);

		addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW, Replacement.COMMAND_SELECT, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
		addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN, Replacement.COMMAND_ACTION, MOUSE_TIME_TRSHOLD, MOUSE_MOVE_TRESHOLD));
	}

	private UIPoint convertToLocal(MouseEvent e) {
		return new UIPoint(e.getX() * scaleFactor, (e.getComponent().getHeight() - e.getY()) * scaleFactor);

	}

	private void updateScaleFactor(Component component) {
		GraphicsConfiguration config = component.getGraphicsConfiguration();
		if (config == null) {
			return;
		}

		GraphicsDevice myScreen = config.getDevice();

		try {
			Field field = myScreen.getClass().getDeclaredField("scale");
			if (field == null) {
				return;
			}
			field.setAccessible(true);
			Object scaleOfField = field.get(myScreen);
			if (scaleOfField instanceof Integer) {
				scaleFactor = ((Integer) scaleOfField).intValue();
			}
		} catch (NoSuchFieldException exception) {
			// if there is no Field scale then we have a scale factor of 1
			// this is expected for Oracle JRE < 1.7.0_u40
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		updateModifiers(e);
		startHover(convertToLocal(e));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateModifiers(e);
		updateHoverPosition(convertToLocal(e));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		updateModifiers(e);
		endHover(convertToLocal(e));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		updateModifiers(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		updateModifiers(e);
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
		updateModifiers(e);
		UIPoint local = convertToLocal(e);
		updateDrawPosition(local);
		updatePanPosition(local);
		updateHoverPosition(local);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateModifiers(e);
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
		updateModifiers(e);
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
				text = "" + e.getKeyChar();
			}
		}
		return text;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateModifiers(e);
		endKeyEvent(getKeyName(e));
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		updateModifiers(e);
		float factor = (float) Math.exp(-e.getUnitsToScroll() / 20.0);
		startZoom();
		endZoomEvent(factor, convertToLocal(e));
	}

	@Override
	public void componentResized(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent componentEvent) {
		updateScaleFactor(componentEvent.getComponent());
	}

	@Override
	public void componentShown(ComponentEvent componentEvent) {
		updateScaleFactor(componentEvent.getComponent());
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void hierarchyChanged(HierarchyEvent hierarchyEvent) {
		Component component = hierarchyEvent.getComponent();
		privateRegisterComponentListenerToParentWindowOf(component, component);
	}

	void privateRegisterComponentListenerToParentWindowOf(Component component, Component childComponent) {
		if (component == null) {
			return;
		} else if (component instanceof Window) {
			updateScaleFactor(component);
			component.addComponentListener(this);
			childComponent.removeComponentListener(this);
		} else {
			privateRegisterComponentListenerToParentWindowOf(component.getParent(), childComponent);
		}
	}

	private void updateModifiers(InputEvent e) {
		modifiers = e.getModifiers();
	}
	
	@Override
	protected Set<EModifier> getCurrentModifiers() {
		EnumSet<EModifier> set = EnumSet.noneOf(EModifier.class);
		if ((modifiers & (InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK)) != 0) {
			set.add(EModifier.CTRL);
		}
		if ((modifiers & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK)) != 0) {
			set.add(EModifier.ALT);
		}
		if ((modifiers & (InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK)) != 0) {
			set.add(EModifier.SHIFT);
		}
		return set;
	}
}
