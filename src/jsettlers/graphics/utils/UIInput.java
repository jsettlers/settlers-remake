package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandler;
import go.graphics.event.GOKeyEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.graphics.action.Action;

public class UIInput extends UIPanel implements GOEventHandler {

	private StringBuffer inputString = new StringBuffer();
	private int carret = 0;

	public boolean handleEvent(GOKeyEvent event) {
		String code = event.getKeyCode();
		if (code.length() == 1 || code.equals("BACK_SPACE")
		        || code.equals("LEFT") || code.equals("RIGHT")) {
			event.setHandler(this);
			return true;
		} else {
			System.out.println("Input cannot handle: " + code);
			return false;
		}
	}

	@Override
	public void phaseChanged(GOEvent event) {
	}

	@Override
	public void finished(GOEvent event) {
		String code = ((GOKeyEvent) event).getKeyCode();
		if (code.length() == 1) {
			inputString.insert(carret, code);
			carret++;
		} else if (code.equals("BACK_SPACE") && carret > 0) {
			// inputString = inputString.substring(0, inputString.length() - 1);
			inputString.replace(carret - 1, carret, "");
			carret--;
		} else if (code.equals("LEFT") && carret > 0) {
			carret--;
		} else if (code.equals("RIGHT") && carret < inputString.length()) {
			carret++;
		}
	}

	@Override
	public void aborted(GOEvent event) {
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		super.drawAt(gl);
		TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);

		float textHeight =
		        (float) drawer.getHeight(inputString.toString() + "X");
		float y = getPosition().getCenterY() - textHeight / 2;
		float x = getPosition().getMinX() + 2;
		drawer.drawString(x, y, inputString.toString());

		float carretX =
		        (float) (x
		                + drawer.getWidth(inputString.substring(0, carret)
		                        + "X") - drawer.getWidth("X"));
		gl.drawLine(new float[] {
		        carretX, y, 0, carretX, y + textHeight, 0
		}, false);
	}

	public String getInputString() {
		return inputString.toString();
	}

	public void setInputString(String inputString) {
		this.inputString = new StringBuffer(inputString);
		carret = inputString.length();
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return new FocusAction(this);
	}
}
