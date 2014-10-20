package jsettlers.graphics.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;

public class StartMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public StartMenuPanel() {
		this(IStartScreen.DEFAULT_IMPLEMENTATION);
	}

	/**
	 * Create the panel.
	 */
	public StartMenuPanel(final IStartScreen dataSupplier) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);

		Component rigidAreaUpperLeft = Box.createRigidArea(new Dimension(40, 40));
		GridBagConstraints gbc_rigidAreaUpperLeft = new GridBagConstraints();
		gbc_rigidAreaUpperLeft.insets = new Insets(0, 0, 5, 5);
		gbc_rigidAreaUpperLeft.gridx = 1;
		gbc_rigidAreaUpperLeft.gridy = 0;
		add(rigidAreaUpperLeft, gbc_rigidAreaUpperLeft);

		JButton btnLoadSingleplayer = new JButton("Load Savegame");
		btnLoadSingleplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		Component strutListWidth = Box.createHorizontalStrut(450);
		GridBagConstraints gbc_strutListWidth = new GridBagConstraints();
		gbc_strutListWidth.insets = new Insets(0, 0, 5, 5);
		gbc_strutListWidth.gridx = 5;
		gbc_strutListWidth.gridy = 0;
		add(strutListWidth, gbc_strutListWidth);

		Component strutPreviewWidth = Box.createHorizontalStrut(250);
		GridBagConstraints gbc_strutPreviewWidth = new GridBagConstraints();
		gbc_strutPreviewWidth.insets = new Insets(0, 0, 5, 0);
		gbc_strutPreviewWidth.gridx = 7;
		gbc_strutPreviewWidth.gridy = 0;
		add(strutPreviewWidth, gbc_strutPreviewWidth);

		final JPanel panelList = new JPanel();
		GridBagConstraints gbc_panelList = new GridBagConstraints();
		gbc_panelList.fill = GridBagConstraints.BOTH;
		gbc_panelList.gridheight = 16;
		gbc_panelList.insets = new Insets(0, 0, 0, 5);
		gbc_panelList.gridx = 5;
		gbc_panelList.gridy = 1;
		add(panelList, gbc_panelList);
		panelList.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panelList.add(scrollPane);

		final JList<IMapDefinition> list = new JList<IMapDefinition>();
		list.setCellRenderer(new MapDefinitionListCellRenderer());
		scrollPane.setViewportView(list);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 6;
		gbc_horizontalStrut.gridy = 1;
		add(horizontalStrut, gbc_horizontalStrut);

		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(null);
		GridBagConstraints gbc_panelInfo = new GridBagConstraints();
		gbc_panelInfo.gridheight = 16;
		gbc_panelInfo.fill = GridBagConstraints.BOTH;
		gbc_panelInfo.gridx = 7;
		gbc_panelInfo.gridy = 1;
		add(panelInfo, gbc_panelInfo);

		JPanel panelMapPreview = new JPanel();
		panelMapPreview.setBounds(0, 0, 250, 250);
		panelInfo.add(panelMapPreview);

		JPanel panelMapInfos = new JPanel();
		panelMapInfos.setBounds(0, 261, 250, 149);
		panelInfo.add(panelMapInfos);

		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 2;
		gbc_verticalStrut.gridy = 2;
		add(verticalStrut, gbc_verticalStrut);
		GridBagConstraints gbc_btnLoadSingleplayer = new GridBagConstraints();
		gbc_btnLoadSingleplayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLoadSingleplayer.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadSingleplayer.gridx = 2;
		gbc_btnLoadSingleplayer.gridy = 3;
		add(btnLoadSingleplayer, gbc_btnLoadSingleplayer);

		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_1.gridx = 2;
		gbc_verticalStrut_1.gridy = 4;
		add(verticalStrut_1, gbc_verticalStrut_1);

		JButton btnStartMultiplayer = new JButton("Create Multiplayer Match");
		GridBagConstraints gbc_btnStartMultiplayer = new GridBagConstraints();
		gbc_btnStartMultiplayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStartMultiplayer.insets = new Insets(0, 0, 5, 5);
		gbc_btnStartMultiplayer.gridx = 2;
		gbc_btnStartMultiplayer.gridy = 5;
		add(btnStartMultiplayer, gbc_btnStartMultiplayer);

		Component verticalStrut_2 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_2 = new GridBagConstraints();
		gbc_verticalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_2.gridx = 2;
		gbc_verticalStrut_2.gridy = 6;
		add(verticalStrut_2, gbc_verticalStrut_2);

		JButton btnLoadMultiplayer = new JButton("Join Multiplayer Match");
		GridBagConstraints gbc_btnLoadMultiplayer = new GridBagConstraints();
		gbc_btnLoadMultiplayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLoadMultiplayer.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadMultiplayer.gridx = 2;
		gbc_btnLoadMultiplayer.gridy = 7;
		add(btnLoadMultiplayer, gbc_btnLoadMultiplayer);

		Component verticalStrut_3 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_3 = new GridBagConstraints();
		gbc_verticalStrut_3.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_3.gridx = 2;
		gbc_verticalStrut_3.gridy = 8;
		add(verticalStrut_3, gbc_verticalStrut_3);

		Component verticalStrut_4 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_4 = new GridBagConstraints();
		gbc_verticalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_4.gridx = 2;
		gbc_verticalStrut_4.gridy = 9;
		add(verticalStrut_4, gbc_verticalStrut_4);

		Component verticalStrut_5 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_5 = new GridBagConstraints();
		gbc_verticalStrut_5.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_5.gridx = 2;
		gbc_verticalStrut_5.gridy = 10;
		add(verticalStrut_5, gbc_verticalStrut_5);

		Component verticalStrut_6 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_6 = new GridBagConstraints();
		gbc_verticalStrut_6.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_6.gridx = 2;
		gbc_verticalStrut_6.gridy = 11;
		add(verticalStrut_6, gbc_verticalStrut_6);

		Component verticalStrut_7 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_7 = new GridBagConstraints();
		gbc_verticalStrut_7.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_7.gridx = 2;
		gbc_verticalStrut_7.gridy = 12;
		add(verticalStrut_7, gbc_verticalStrut_7);

		Component verticalStrut_8 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_8 = new GridBagConstraints();
		gbc_verticalStrut_8.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_8.gridx = 2;
		gbc_verticalStrut_8.gridy = 13;
		add(verticalStrut_8, gbc_verticalStrut_8);

		Component verticalStrut_9 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_9 = new GridBagConstraints();
		gbc_verticalStrut_9.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_9.gridx = 2;
		gbc_verticalStrut_9.gridy = 14;
		add(verticalStrut_9, gbc_verticalStrut_9);

		Component verticalStrut_10 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_10 = new GridBagConstraints();
		gbc_verticalStrut_10.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_10.gridx = 2;
		gbc_verticalStrut_10.gridy = 15;
		add(verticalStrut_10, gbc_verticalStrut_10);

		JButton btnSettings = new JButton("Settings");
		GridBagConstraints gbc_btnSettings = new GridBagConstraints();
		gbc_btnSettings.insets = new Insets(0, 0, 0, 5);
		gbc_btnSettings.gridx = 2;
		gbc_btnSettings.gridy = 16;
		add(btnSettings, gbc_btnSettings);

		JButton btnStartSingleplayer = new JButton("Start New Game");
		btnStartSingleplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.setModel(generateModel(dataSupplier.getSingleplayerMaps()));
			}
		});
		GridBagConstraints gbc_btnStartSingleplayer = new GridBagConstraints();
		gbc_btnStartSingleplayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStartSingleplayer.insets = new Insets(0, 0, 5, 5);
		gbc_btnStartSingleplayer.gridx = 2;
		gbc_btnStartSingleplayer.gridy = 1;
		add(btnStartSingleplayer, gbc_btnStartSingleplayer);

	}

	protected ListModel<IMapDefinition> generateModel(ChangingList<IMapDefinition> maps) {
		DefaultListModel<IMapDefinition> model = new DefaultListModel<IMapDefinition>();
		for (IMapDefinition curr : maps.getItems())
			model.addElement(curr);
		return model;
	}
}
