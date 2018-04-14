/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.graphics.debug.datviewer;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOKeyEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GOZoomEvent;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.Color;
import jsettlers.common.utils.FileUtils;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.Sequence;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DatFileViewer extends JFrame implements ListSelectionListener {
	private JLabel lblDatType;
	private JLabel lblNumUiSeqs;
	private JLabel lblNumSettlerSeqs;
	private JLabel lblNumLandscapeSeqs;
	private JList listView;
	private Surface glCanvas;
	private File gfxDirectory;
	private DefaultListModel<String> listItems;
	private AdvancedDatFileReader reader;

	private enum ImageSet {
		SETTLERS,
		GUI,
		LANDTILES
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new DatFileViewer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private DatFileViewer() {
		glCanvas = new Surface();

		JPanel infoField = new JPanel();
		infoField.setLayout(new BoxLayout(infoField, BoxLayout.PAGE_AXIS));

		lblDatType = new JLabel();
		lblNumUiSeqs = new JLabel();
		lblNumSettlerSeqs = new JLabel();
		lblNumLandscapeSeqs = new JLabel();

		infoField.add(lblDatType);
		infoField.add(lblNumSettlerSeqs);
		infoField.add(lblNumLandscapeSeqs);
		infoField.add(lblNumUiSeqs);

		listItems = new DefaultListModel<>();
		listView = new JList<>(listItems);
		listView.getSelectionModel().addListSelectionListener(this);

		JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane2.setTopComponent(new JScrollPane(listView));
		splitPane2.setBottomComponent(infoField);
		splitPane2.setResizeWeight(0.5);
		splitPane2.setEnabled(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(splitPane2);
		splitPane.setRightComponent(glCanvas);
		splitPane.setResizeWeight(0.10);

		this.setTitle("DatFileViewer");
		this.setJMenuBar(createMenu());
		this.getContentPane().add(splitPane);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 600));
		this.setVisible(true);
	}

	private JMenuBar createMenu() {
		JMenu openMenu = new JMenu("Open");
		JMenuItem openDirItem = new JMenuItem("GFX Folder");
		openMenu.add(openDirItem);

		JMenu exportMenu = new JMenu("Export Images");
		JMenuItem exportThis = new JMenuItem("from this file");
		JMenuItem exportAll = new JMenuItem("from all files");
		exportMenu.add(exportThis);
		exportMenu.add(exportAll);

		JMenu showMenu = new JMenu("Show");
		JMenuItem showSettlers = new JMenuItem("Settlers");
		JMenuItem showGui = new JMenuItem("GUI");
		JMenuItem showLandscape = new JMenuItem("Landscape");
		showMenu.add(showSettlers);
		showMenu.add(showGui);
		showMenu.add(showLandscape);

		openDirItem.addActionListener((e) -> {
			JFileChooser openDirDlg = new JFileChooser();
			openDirDlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (openDirDlg.showDialog(null, null) == JFileChooser.APPROVE_OPTION) {
				gfxDirectory = new File(openDirDlg.getSelectedFile().getAbsolutePath());

				FileUtils.iterateChildren(gfxDirectory, (currentFile) -> {
					String fileName = currentFile.getName();
					if (currentFile.isFile() && fileName.endsWith(".dat")) {
						listItems.addElement(currentFile.getName());
					}
				});
			}
		});

		exportThis.addActionListener((e) -> {
			onExportSelectedFile();
		});

		exportAll.addActionListener((e) -> {
			onExportAllFiles();
		});

		showSettlers.addActionListener((e) -> {
			glCanvas.currentSet = ImageSet.SETTLERS;
			glCanvas.invalidate();
		});

		showGui.addActionListener((e) -> {
			glCanvas.currentSet = ImageSet.GUI;
			glCanvas.invalidate();
		});

		showLandscape.addActionListener((e) -> {
			glCanvas.currentSet = ImageSet.LANDTILES;
			glCanvas.invalidate();
		});

		JMenuBar bar = new JMenuBar();
		bar.add(openMenu);
		bar.add(exportMenu);
		bar.add(showMenu);
		return bar;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getFirstIndex() < 0 || e.getValueIsAdjusting())
			return;

		String fileName = (String) listView.getSelectedValue();

		DatFileType type = DatFileType.RGB565;
		File file = new File(gfxDirectory, fileName);

		if (file.getName().contains(DatFileType.RGB555.getFileSuffix())) {
			type = DatFileType.RGB555;
		} else if (file.getName().contains(DatFileType.RGB565.getFileSuffix())) {
			type = DatFileType.RGB565;
		}

		reader = new AdvancedDatFileReader(file, type);
		showFileInfo(type, reader);

		glCanvas.resetOffset();
		glCanvas.resetZoom();
		glCanvas.invalidate();
		glCanvas.requestFocus();
	}

	private void showFileInfo(DatFileType type, AdvancedDatFileReader datFile) {
		Sequence<GuiImage> ui = datFile.getGuis();
		SequenceList<Image> settlers = datFile.getSettlers();
		Sequence<LandscapeImage> landscapes = datFile.getLandscapes();

		lblDatType.setText("Type: " + type.toString());
		lblNumUiSeqs.setText("# GUI Sequences: " + String.valueOf(ui.length()));
		lblNumSettlerSeqs.setText("# Settler Sequences: " + String.valueOf(settlers.size()));
		lblNumLandscapeSeqs.setText("# Landscape Sequences: " + String.valueOf(landscapes.length()));
	}

	private void onExportAllFiles() {
		if (listItems.isEmpty())
			return;

		JFileChooser openDirDlg = new JFileChooser();
		openDirDlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (openDirDlg.showDialog(null, null) == JFileChooser.APPROVE_OPTION) {
			File exportDir = openDirDlg.getSelectedFile();
			FileUtils.iterateChildren(gfxDirectory, (File currentFile) -> {
				String fileName = currentFile.getName();
				if (currentFile.isFile() && fileName.endsWith(".dat")) {
					DatFileType type;
					if (currentFile.getName().contains(DatFileType.RGB555.getFileSuffix())) {
						type = DatFileType.RGB555;
					} else
						type = DatFileType.RGB565;

					AdvancedDatFileReader file = new AdvancedDatFileReader(new File(gfxDirectory, fileName), type);
					exportFile(exportDir, file);
				}
			});

			JOptionPane.showMessageDialog(null, "Export completed!", "DatFileViewer - Export", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void onExportSelectedFile() {
		int selectedIndex = listView.getSelectedIndex();
		if (selectedIndex < 0)
			return;

		JFileChooser openDirDlg = new JFileChooser();
		openDirDlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (openDirDlg.showDialog(null, null) == JFileChooser.APPROVE_OPTION) {
			File exportDir = new File(openDirDlg.getSelectedFile().getAbsolutePath());

			File datfile = new File(gfxDirectory, listItems.get(selectedIndex));
			DatFileType type;
			if (datfile.getName().contains(DatFileType.RGB555.getFileSuffix())) {
				type = DatFileType.RGB555;
			} else
				type = DatFileType.RGB565;

			AdvancedDatFileReader file = new AdvancedDatFileReader(datfile, type);
			exportFile(exportDir, file);
		}
	}

	// region Export

	private void exportFile(File dir, AdvancedDatFileReader reader) {
		exportSequences(new File(dir, "settlers"), reader.getSettlers());

		Sequence<GuiImage> guis = reader.getGuis();
		if (guis.length() > 0) {
			exportSequence(new File(dir, "gui"), 0, guis);
		}

		Sequence<LandscapeImage> landscapes = reader.getLandscapes();
		if (landscapes.length() > 0) {
			exportSequence(new File(dir, "landscape"), 1, landscapes);
		}
	}

	private <T extends Image> void exportSequences(File dir, SequenceList<T> sequences) {
		for (int index = 0; index < sequences.size(); index++) {
			Sequence<T> seq = sequences.get(index);
			exportSequence(dir, index, seq);
		}
	}

	private <T extends Image> void exportSequence(File dir, int index, Sequence<T> seq) {
		File seqdir = new File(dir, index + "");
		if (!seqdir.exists()) {
			if (!seqdir.mkdirs()) {
				JOptionPane.showMessageDialog(null, "Can't create path: " + seqdir.toString(), "DatFileViewer - Export", JOptionPane.ERROR_MESSAGE);
			}
		}

		for (int j = 0; j < seq.length(); j++) {
			T image = seq.getImage(j);
			exportSingleImage((SingleImage) image, new File(seqdir, j + ".png"));
			if (image instanceof SettlerImage && ((SettlerImage) image).getTorso() != null) {
				exportSingleImage((SingleImage) ((SettlerImage) image).getTorso(), new File(seqdir, j + "_torso.png"));
			}
		}
	}

	private void exportSingleImage(SingleImage image, File file) {
		// does not work if gpu does not support non-power-of-two
		BufferedImage rendered = image.convertToBufferedImage();
		if (rendered == null) {
			return;
		}

		try {
			ImageIO.write(rendered, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// endregion

	private class Surface extends GLSurface implements GOModalEventHandler {
		private Color[] colors = new Color[] { Color.WHITE };
		private float zoom = 1.0f;
		private int offsetY = 0;
		private int offsetX = 0;
		ImageSet currentSet;

		Surface() {
			currentSet = ImageSet.SETTLERS;
			resetOffset();
		}

		void resetOffset() {
			offsetY = 0;
			offsetX = 0;
		}

		void resetZoom() {
			zoom = 1.0f;
		}

		// region Drawing Code

		@Override
		protected void redraw(GLDrawContext gl2, int width, int height) {
			if (reader == null)
				return;

			TextDrawer txtRenderer = gl2.getTextDrawer(EFontSize.NORMAL);
			txtRenderer.drawString(0.0f, height - 15.f, currentSet.toString());
			txtRenderer.drawString(0.0f, height - 30.f, String.format("Offset: %d, %d", offsetX, offsetY));
			txtRenderer.drawString(0.0f, height - 45.f, String.format("Zoom: %f", zoom));

			// Zoom
			gl2.glTranslatef(width / 2, height / 2, 0);
			gl2.glScalef(zoom, zoom, zoom);
			gl2.glTranslatef(-width / 2, -height / 2, 0);

			// Move
			gl2.glTranslatef(offsetX, offsetY, 0);

			// Render
			int yPos = height - 100;
			if (currentSet == ImageSet.SETTLERS) {
				SequenceList<Image> sequences = reader.getSettlers();
				drawMultipleSequences(gl2, yPos, sequences);
			} else if (currentSet == ImageSet.GUI) {
				Sequence<GuiImage> sequences = reader.getGuis();
				drawSingleSequence(gl2, yPos, 20, sequences);
			} else {
				Sequence<LandscapeImage> sequences = reader.getLandscapes();
				drawSingleSequence(gl2, yPos, 20, sequences);
			}
		}

		private <T extends Image> void drawMultipleSequences(GLDrawContext gl2, int y, SequenceList<T> sequences) {

			int seqIndex = 0;
			TextDrawer drawer = gl2.getTextDrawer(EFontSize.NORMAL);
			for (int i = 0; i < sequences.size(); i++) {
				Sequence<T> seq = sequences.get(i);

				int maxHeight = drawSingleSequence(gl2, y, 20, seq);

				gl2.color(0, 0, 0, 1);
				drawer.drawString(-20, y + 20, seqIndex + ":");

				seqIndex++;
				y -= maxHeight + 50;
			}
		}

		private <T extends Image> int drawSingleSequence(GLDrawContext gl2, int y, int xSpacing, Sequence<T> seq) {
			int x = 0;
			int maxHeight = 0;
			for (int index = 0; index < seq.length(); ++index) {
				T image = seq.getImage(index);
				maxHeight = Math.max(maxHeight, image.getHeight());

				drawImage(gl2, x, y, index, (SingleImage) image);
				x += Math.max(50, image.getWidth()) + xSpacing;
			}
			return maxHeight;
		}

		private void drawImage(GLDrawContext gl2, int x, int y, int index, SingleImage image) {
			image.drawAt(gl2, x - image.getOffsetX(), y + image.getHeight() + image.getOffsetY(), colors[index % colors.length]);

			gl2.color(1, 0, 0, 1);
			float[] line = new float[] {
					x, y, 0,
					x, y + image.getHeight() + image.getOffsetY(), 0,
					x - image.getOffsetX(), y + image.getHeight() + image.getOffsetY(), 0
			};
			gl2.drawLine(line, false);
		}

		// endregion

		// region GOEventHandler

		private UIPoint oldPos = new UIPoint(0, 0);

		@Override
		public void handleEvent(GOEvent event) {
			event.setHandler(this);
		}

		@Override
		public void phaseChanged(GOEvent event) {
			if (event instanceof GODrawEvent) {
				if (event.getPhase() == 1) {
					oldPos = ((GODrawEvent) event).getDrawPosition();
				}
			}
		}

		@Override
		public void finished(GOEvent event) {
			if (event instanceof GOKeyEvent) {
				String keyCode = ((GOKeyEvent) event).getKeyCode();
				if ("UP".equalsIgnoreCase(keyCode)) {
					offsetY -= 400 / zoom;
				} else if ("DOWN".equalsIgnoreCase(keyCode)) {
					offsetY += 400 / zoom;
				} else if ("LEFT".equalsIgnoreCase(keyCode)) {
					offsetX += 200 / zoom;
				} else if ("RIGHT".equalsIgnoreCase(keyCode)) {
					offsetX -= 200 / zoom;
				} else if ("L".equalsIgnoreCase(keyCode)) {
					currentSet = ImageSet.LANDTILES;
					resetOffset();
				} else if ("S".equalsIgnoreCase(keyCode)) {
					currentSet = ImageSet.SETTLERS;
					resetOffset();
				} else if ("G".equalsIgnoreCase(keyCode)) {
					currentSet = ImageSet.GUI;
					resetOffset();
				}

				this.invalidate();
			} else if (event instanceof GOZoomEvent) {
				zoom *= ((GOZoomEvent) event).getZoomFactor();
				this.invalidate();
			}
		}

		@Override
		public void aborted(GOEvent event) {
		}

		@Override
		public void eventDataChanged(GOEvent event) {
			if (event instanceof GODrawEvent) {
				UIPoint currentPos = ((GODrawEvent) event).getDrawPosition();
				double dx = currentPos.getX() - oldPos.getX();
				double dy = currentPos.getY() - oldPos.getY();

				// When zoomed in, don't move the view as far
				offsetX += dx / zoom;
				offsetY += dy / zoom;

				oldPos = currentPos;
				this.invalidate();
			}
		}

		// endregion
	}
}
