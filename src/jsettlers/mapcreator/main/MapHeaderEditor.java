package jsettlers.mapcreator.main;

import java.awt.Dimension;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import jsettlers.common.CommonConstants;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;

/**
 * This dialog edits the map header.
 * 
 * @author michael
 */
public class MapHeaderEditor extends JPanel {
	private static final int DEFAULT_MAPSIZE = 300;

	/**
     * 
     */
	private static final long serialVersionUID = 4452683956025604099L;

	private static final int MIN_MAPSIZE = 100;
	private static final int MAX_MAPSIZE = 2000;
	private SpinnerNumberModel width;
	private SpinnerNumberModel height;
	private SpinnerNumberModel minPlayer;
	private SpinnerNumberModel maxPlayer;
	private JTextField nameField;
	private JTextArea descriptionField;

	public MapHeaderEditor(MapFileHeader header, boolean sizeChangable) {
		generate(sizeChangable);
		setHeader(header);
	}

	private void generate(boolean sizeChangable) {
		nameField = new JTextField();
		descriptionField = new JTextArea();
		descriptionField.setMinimumSize(new Dimension(200, 50));

		width =
		        new SpinnerNumberModel(DEFAULT_MAPSIZE, MIN_MAPSIZE,
		                MAX_MAPSIZE, 1);
		height =
		        new SpinnerNumberModel(DEFAULT_MAPSIZE, MIN_MAPSIZE,
		                MAX_MAPSIZE, 1);
		minPlayer =
		        new SpinnerNumberModel(1, 1, CommonConstants.MAX_PLAYERS, 1);
		maxPlayer =
		        new SpinnerNumberModel(1, 1, CommonConstants.MAX_PLAYERS, 1);

		JSpinner widthField = new JSpinner(width);
		JSpinner heightField = new JSpinner(height);
		JSpinner minPlayerField = new JSpinner(minPlayer);
		JSpinner maxPlayerField = new JSpinner(maxPlayer);

		JLabel nameLabel = new JLabel("Name");
		JLabel descriptionLabel = new JLabel("Description");
		JLabel widthLabel = new JLabel("Width");
		JLabel heightLabel = new JLabel("height");
		JLabel minPlayerLabel = new JLabel("minimum player number");
		JLabel maxPlayerLabel = new JLabel("maximum player number");

		add(nameField);
		add(descriptionField);
		add(heightField);
		add(widthField);
		add(minPlayerField);
		add(maxPlayerField);

		add(nameLabel);
		add(descriptionLabel);
		add(widthLabel);
		add(heightLabel);
		add(maxPlayerLabel);
		add(minPlayerLabel);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		//@formatter:off
		layout.setHorizontalGroup(layout.createSequentialGroup()
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(nameLabel)
		        		.addComponent(descriptionLabel)
		        		.addComponent(heightLabel)
		        		.addComponent(widthLabel)
		        		.addComponent(maxPlayerLabel)
		        		.addComponent(minPlayerLabel))
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(nameField)
		        		.addComponent(descriptionField)
		        		.addComponent(heightField)
		        		.addComponent(widthField)
		        		.addComponent(maxPlayerField)
		        		.addComponent(minPlayerField)));
		layout.setVerticalGroup(layout.createSequentialGroup()
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(nameLabel)
		        		.addComponent(nameField))
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(descriptionLabel)
		        		.addComponent(descriptionField))
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(widthLabel)
		        		.addComponent(widthField))
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(heightLabel)
		        		.addComponent(heightField))
		        .addGroup(layout.createParallelGroup()
		        		.addComponent(minPlayerLabel)
		        		.addComponent(minPlayerField))
				.addGroup(layout.createParallelGroup()
				        .addComponent(maxPlayerLabel)
				        .addComponent(maxPlayerField)));
		//@formatter:on

		if (!sizeChangable) {
			widthField.setEnabled(false);
			heightField.setEnabled(false);
		}
	}

	public void setHeader(MapFileHeader header) {
		nameField.setText(header.getName());
		descriptionField.setText(header.getDescription());
		width.setValue(new Integer(header.getWidth()));
		height.setValue(new Integer(header.getHeight()));
		minPlayer.setValue(new Integer(header.getMinPlayer()));
		maxPlayer.setValue(new Integer(header.getMaxPlayer()));
	}

	public MapFileHeader getHeader() {
		String name = nameField.getText();
		String description = descriptionField.getText();
		short width = this.width.getNumber().shortValue();
		short height = this.height.getNumber().shortValue();
		short minPlayer = this.minPlayer.getNumber().shortValue();
		short maxPlayer = this.maxPlayer.getNumber().shortValue();
		return new MapFileHeader(MapType.NORMAL, name, description, width,
		        height, minPlayer, maxPlayer, new Date(),
		        new short[MapFileHeader.PREVIEW_IMAGE_SIZE
		                * MapFileHeader.PREVIEW_IMAGE_SIZE]);
	}
}
