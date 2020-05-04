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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.main.swing.JSettlersSwingUtil;
import jsettlers.main.swing.lookandfeel.ELFStyle;
import jsettlers.main.swing.lookandfeel.ui.UIDefaults;

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
	private final Box rightPanelPart = Box.createVerticalBox();

	/**
	 * Main Panel
	 */
	private final JPanel contentsPanel = new JPanel();

	/**
	 * Name of the Map
	 */
	private final JLabel mapNameLabel = new JLabel();

	/**
	 * Count of players
	 */
	private final JLabel playerCountLabel = new JLabel();

	/**
	 * ID of the Map and the creation date
	 */
	private final JLabel mapIdLabel = new JLabel();

	/**
	 * Description of the Map
	 */
	private final JLabel descriptionLabel = new JLabel();

	/**
	 * Preview of the map
	 */
	private final JLabel iconLabel = new JLabel();

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
	private final Map<MapLoader, Icon> previewImageCache = new ConcurrentHashMap<>();

	/**
	 * Constructor
	 */
	public MapListCellRenderer() {
		JPanel pFirst = new JPanel();
		pFirst.setOpaque(false);
		pFirst.setLayout(new BorderLayout(5, 0));
		pFirst.add(mapNameLabel, BorderLayout.CENTER);
		pFirst.add(playerCountLabel, BorderLayout.EAST);
		pFirst.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanelPart.add(pFirst);
		rightPanelPart.add(mapIdLabel);
		mapIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanelPart.add(descriptionLabel);
		descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		rightPanelPart.setOpaque(false);
		rightPanelPart.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mapNameLabel.setFont(mapNameLabel.getFont().deriveFont(Font.BOLD));
		mapNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		mapNameLabel.setForeground(FOREGROUND);
		mapIdLabel.setForeground(FOREGROUND);
		descriptionLabel.setForeground(FOREGROUND);
		playerCountLabel.setForeground(Color.BLUE);

		mapNameLabel.setFont(UIDefaults.FONT_SMALL);
		mapIdLabel.setFont(UIDefaults.FONT_SMALL);
		descriptionLabel.setFont(UIDefaults.FONT_SMALL);
		playerCountLabel.setFont(UIDefaults.FONT_SMALL);

		contentsPanel.setLayout(new BorderLayout());
		contentsPanel.add(rightPanelPart, BorderLayout.CENTER);
		contentsPanel.add(iconLabel, BorderLayout.WEST);
		contentsPanel.putClientProperty(ELFStyle.KEY, ELFStyle.PANEL_DRAW_BG_CUSTOM);

		iconLabel.setOpaque(false);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

		// Update UI
		SwingUtilities.updateComponentTreeUI(contentsPanel);
	}

	/**
	 * Gets the preview icon for a map
	 *
	 * @param value
	 *            Map
	 * @return Image, <code>null</code> if none or error
	 */
	private Icon getPreviewIcon(MapLoader value) {
		Icon mapPreviewIcon = previewImageCache.get(value);
		if (mapPreviewIcon != null) {
			return mapPreviewIcon;
		}

		try {
			BufferedImage previewImage = JSettlersSwingUtil.createBufferedImageFrom(value);

			Image resizedPreviewImage = previewImage.getScaledInstance(MapFileHeader.PREVIEW_IMAGE_SIZE,
					MapFileHeader.PREVIEW_IMAGE_SIZE / 2, Image.SCALE_SMOOTH);
			mapPreviewIcon = new ImageIcon(resizedPreviewImage);
			previewImageCache.put(value, mapPreviewIcon);
			return mapPreviewIcon;
		} catch (Exception e) {
			System.err.println("Error converting preview image");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MapLoader> list, MapLoader value, int index, boolean isSelected,
			boolean cellHasFocus) {
		mapNameLabel.setText(value.getMapName());
		String date = "???";
		if (value.getCreationDate() != null) {
			date = df.format(value.getCreationDate());
		}

		mapIdLabel.setText(date + " / " + value.getMapId());
		playerCountLabel.setText("Player: " + value.getMinPlayers() + " - " + value.getMaxPlayers());

		if (value.getDescription() != null && !value.getDescription().isEmpty()) {
			descriptionLabel.setText(value.getDescription());
		} else {
			descriptionLabel.setText("<no description>");
		}

		Icon previewIcon = getPreviewIcon(value);
		if (previewIcon == null) {
			previewIcon = EMPTY_ICON;
		}
		iconLabel.setIcon(previewIcon);

		if (isSelected) {
			contentsPanel.setBackground(SELECTION_BACKGROUND);
		} else {
			if (index % 2 == 0) {
				contentsPanel.setBackground(BACKGROUND1);
			} else {
				contentsPanel.setBackground(BACKGROUND2);
			}
		}

		return contentsPanel;
	}
}