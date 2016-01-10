package jsettlers.lookandfeel;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.lookandfeel.ui.ScrollBarButton;
import jsettlers.lookandfeel.ui.ScrollBarButton.Orientation;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Stone looking scrollbar.
 * 
 * I couldn't create a transparent scrollbar UI, so I decided to create a custom scrollbar class. If you know how to create a Custom transparency
 * scrollbar UI which looks like this: Create a pull request
 * 
 * @author Andreas Butti
 */
public class StoneScrollbar extends JScrollBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Increment scroll (right, up)
	 */
	private ScrollBarButton btInc = new ScrollBarButton();

	/**
	 * Decrement scroll (left, down)
	 */
	private ScrollBarButton btDec = new ScrollBarButton();

	/**
	 * Background Image
	 */
	private BufferedImage backgroundTop;

	/**
	 * Background Image
	 */
	private BufferedImage backgroundBottom;

	/**
	 * Background Image
	 */
	private BufferedImage backgroundScrol;

	/**
	 * Constructor
	 * 
	 * @param orientation
	 *            Orientation
	 * @param value
	 *            Max value
	 * @param extent
	 *            Set Block increment (0/1)
	 * @param min
	 *            Min value
	 * @param max
	 *            Max value
	 */
	public StoneScrollbar(int orientation, int value, int extent, int min, int max) {
		super(orientation, value, extent, min, max);
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param orientation
	 *            Orientation
	 */
	public StoneScrollbar(int orientation) {
		super(orientation);
		init();
	}

	/**
	 * Constructor
	 */
	public StoneScrollbar() {
		super();
		init();
	}

	/**
	 * Initilaize
	 */
	private void init() {
		add(btInc);
		add(btDec);
		setLayout(null);

		ImageProvider prv = ImageProvider.getInstance();
		backgroundTop = prv.getGuiImage(2, 2).generateBufferedImage();
		backgroundBottom = prv.getGuiImage(2, 3).generateBufferedImage();
		backgroundScrol = prv.getGuiImage(2, 4).generateBufferedImage();

		btInc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	@Override
	public void doLayout() {
		if (orientation == VERTICAL) {
			btInc.setOrientation(Orientation.UP);
			btDec.setOrientation(Orientation.DOWN);

			int s = getWidth();
			btInc.setBounds(0, 0, s, s / 2);
			btDec.setBounds(0, getHeight() - s / 2, s, s / 2);
		} else {
			btInc.setOrientation(Orientation.LEFT);
			btDec.setOrientation(Orientation.RIGHT);

			int s = getHeight();
			btInc.setBounds(0, 0, s / 2, s);
			btDec.setBounds(getWidth() - s / 2, 0, s / 2, s);
		}
	}

	@Override
	public void setUI(ScrollBarUI ui) {
		super.setUI(null);
	}

	@Override
	public ScrollBarUI getUI() {
		return null;
	}

	@Override
	public void updateUI() {
		setUI(null);

		setPreferredSize(new Dimension(16, 100));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.RED);
		int min = getMinimum();
		int max = getMaximum() - min;
		int val = getValue() - min;

		/**
		 * Length of the slider
		 */
		int length = 40;

		int buttonHeigth = getWidth() + 2;

		float yPos = val / max * (getHeight() - buttonHeigth - length);

		g.fillRect(1, (int) (buttonHeigth / 2 + yPos), getWidth() - 2, length);
	}
}
