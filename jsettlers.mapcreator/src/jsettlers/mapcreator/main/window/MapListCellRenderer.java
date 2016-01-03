package jsettlers.mapcreator.main.window;

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

import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.mapcreator.localization.EditorLabels;

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
	private static final Color SELECTION_BACKGROUND = new Color(0x9EB1CD);

	/**
	 * Background even
	 */
	private static final Color BACKGROUND1 = Color.WHITE;

	/**
	 * Background odd
	 */
	private static final Color BACKGROUND2 = new Color(0xE0E0E0);

	/**
	 * Foreground
	 */
	private static final Color FOREGROUND = Color.BLACK;

	/**
	 * Right part of the panel with all texts
	 */
	private Box pRight = Box.createVerticalBox();

	/**
	 * Main Panel
	 */
	private JPanel pContents = new JPanel();

	/**
	 * Name of the Map
	 */
	private JLabel lbName = new JLabel();

	/**
	 * Count of players
	 */
	private JLabel lbPlayerCount = new JLabel();

	/**
	 * ID of the Map and the creation date
	 */
	private JLabel lbMapId = new JLabel();

	/**
	 * Description of the Map
	 */
	private JLabel lbDescription = new JLabel();

	/**
	 * Preview of the map
	 */
	private JLabel lbIcon = new JLabel();

	/**
	 * Empty icon, if there is noe
	 */
	private final Icon EMPTY_ICON = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
		}

		@Override
		public int getIconWidth() {
			return MapFileHeader.PREVIEW_IMAGE_SIZE / 2;
		}

		@Override
		public int getIconHeight() {
			return MapFileHeader.PREVIEW_IMAGE_SIZE / 2;
		}
	};

	/**
	 * Format for date display
	 */
	private SimpleDateFormat df = new SimpleDateFormat(EditorLabels.getLabel("date.date-only"));

	/**
	 * Cache for preview images
	 */
	private Map<MapLoader, Icon> previewImageCache = new HashMap<>();

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
		lbIcon.setOpaque(false);
		lbIcon.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
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
			short[] data = value.getImage();
			int xOffset = MapFileHeader.PREVIEW_IMAGE_SIZE;
			BufferedImage img = new BufferedImage(MapFileHeader.PREVIEW_IMAGE_SIZE + xOffset,
					MapFileHeader.PREVIEW_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);

			xOffset--;
			for (int y = 0; y < MapFileHeader.PREVIEW_IMAGE_SIZE; y++) {
				for (int x = 0; x < MapFileHeader.PREVIEW_IMAGE_SIZE; x++) {
					int index = y * MapFileHeader.PREVIEW_IMAGE_SIZE + x;
					int rgb565 = data[index];

					// blue:
					// 0b0000 0000 0001 1111 => 0x001f
					// green:
					// 0b0000 0111 1110 0000 => 0x07e0
					// red:
					// 0b1111 1000 0000 0000 => 0xF800

					// blue
					int rgb = (rgb565 & 0x001f) << 3;
					// green
					rgb |= (rgb565 & 0x07e0) << 5;
					// red
					rgb |= (rgb565 & 0xF800) << 8;

					img.setRGB(x + xOffset, y, rgb | 0xff000000);
				}
				if (xOffset > 1) {
					xOffset--;
				}
			}

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
