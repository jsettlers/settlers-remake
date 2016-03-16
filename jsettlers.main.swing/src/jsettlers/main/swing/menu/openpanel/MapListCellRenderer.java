/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.main.swing.menu.openpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.main.swing.JSettlersSwingUtil;
import jsettlers.main.swing.lookandfeel.LFStyle;

/**
 * Render to open an existing map
 *
 * @author Andreas Butti
 *
 */
public class MapListCellRenderer implements ListCellRenderer<MapLoader> {

	/**
	 * Selected background color
	 */
	private final Color SELECTION_BACKGROUND = UIManager.getColor("MapListCellRenderer.backgroundSelected");

	/**
	 * Background even
	 */
	private final Color BACKGROUND1 = UIManager.getColor("MapListCellRenderer.backgroundColor1");

	/**
	 * Background odd
	 */
	private final Color BACKGROUND2 = UIManager.getColor("MapListCellRenderer.backgroundColor2");

	/**
	 * Font color
	 */
	private final Color FOREGROUND = UIManager.getColor("MapListCellRenderer.foregroundColor");

	/**
	 * Right part of the panel with all texts
	 */
	private final Box pRight = Box.createVerticalBox();

	/**
	 * Main Panel
	 */
	private final JPanel pContents = new JPanel();

	/**
	 * Name of the Map
	 */
	private final JLabel lbName = new JLabel();

	/**
	 * Count of players
	 */
	private final JLabel lbPlayerCount = new JLabel();

	/**
	 * ID of the Map and the creation date
	 */
	private final JLabel lbMapId = new JLabel();

	/**
	 * Description of the Map
	 */
	private final JLabel lbDescription = new JLabel();

	/**
	 * Preview of the map
	 */
	private final JLabel lbIcon = new JLabel();

	/**
	 * Empty icon, if there is no image
	 */
	private final Icon EMPTY_ICON = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
		}

		@Override
		public int getIconWidth() {
			return 1;
		}

		@Override
		public int getIconHeight() {
			return 1;
		}
	};

	/**
	 * Format for date display
	 */
	private final SimpleDateFormat df = new SimpleDateFormat(Labels.getString("date.date-only"));

	/**
	 * Cache for preview images
	 */
	private final Map<MapLoader, Icon> previewImageCache = new HashMap<>();

	/**
	 * Constructor
	 */
	public MapListCellRenderer() {
		JPanel pFirst = new JPanel();
		pFirst.setOpaque(false);
		pFirst.setLayout(new BorderLayout(5, 0));
		pFirst.add(lbName, BorderLayout.CENTER);
		pFirst.add(lbPlayerCount, BorderLayout.EAST);
		pFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
		pRight.add(pFirst);
		pRight.add(lbMapId);
		lbMapId.setAlignmentX(Component.LEFT_ALIGNMENT);
		pRight.add(lbDescription);
		lbDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

		pRight.setOpaque(false);
		pRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		lbName.setFont(lbName.getFont().deriveFont(Font.BOLD));
		lbName.setAlignmentX(Component.LEFT_ALIGNMENT);

		lbName.setForeground(FOREGROUND);
		lbMapId.setForeground(FOREGROUND);
		lbDescription.setForeground(FOREGROUND);
		lbPlayerCount.setForeground(Color.BLUE);

		pContents.setLayout(new BorderLayout());
		pContents.add(pRight, BorderLayout.CENTER);
		pContents.add(lbIcon, BorderLayout.WEST);
		pContents.putClientProperty(LFStyle.KEY, LFStyle.PANEL_DRAW_BG_CUSTOM);

		lbIcon.setOpaque(false);
		lbIcon.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

		// Update UI
		SwingUtilities.updateComponentTreeUI(pContents);
	}

	/**
	 * Resize image
	 *
	 * @param img
	 *            Source image
	 * @param newW
	 *            Width
	 * @param newH
	 *            Height
	 * @return Rescaled image
	 */
	private static Image resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	/**
	 * Gets the preview image for a map
	 *
	 * @param value
	 *            Map
	 * @return Image, <code>null</code> if none or error
	 */
	private Icon getPreviewImage(MapLoader value) {
		Icon icon = previewImageCache.get(value);
		if (icon != null) {
			return icon;
		}

		try {
			BufferedImage img = JSettlersSwingUtil.createBufferedImageFrom(value);

			Image resized = resize(img, MapFileHeader.PREVIEW_IMAGE_SIZE, MapFileHeader.PREVIEW_IMAGE_SIZE / 2);
			icon = new ImageIcon(resized);
			previewImageCache.put(value, icon);
			return icon;
		} catch (Exception e) {
			System.err.println("Error converting preview image");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MapLoader> list, MapLoader value, int index, boolean isSelected,
			boolean cellHasFocus) {
		lbName.setText(value.getMapName());
		String date = "???";
		if (value.getCreationDate() != null) {
			date = df.format(value.getCreationDate());
		}

		lbMapId.setText(date + " / " + value.getMapId());
		lbPlayerCount.setText("Player: " + value.getMinPlayers() + " - " + value.getMaxPlayers());

		if (value.getDescription() != null && !value.getDescription().isEmpty()) {
			lbDescription.setText(value.getDescription());
		} else {
			lbDescription.setText("<no description>");
		}

		Icon img = getPreviewImage(value);
		if (img == null) {
			img = EMPTY_ICON;
		}
		lbIcon.setIcon(img);

		if (isSelected) {
			pContents.setBackground(SELECTION_BACKGROUND);
		} else {
			if (index % 2 == 0) {
				pContents.setBackground(BACKGROUND1);
			} else {
				pContents.setBackground(BACKGROUND2);
			}
		}

		return pContents;
	}
}