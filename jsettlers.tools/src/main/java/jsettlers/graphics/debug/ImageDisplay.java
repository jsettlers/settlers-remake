/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.graphics.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ImageDisplay extends JFrame {

	private static final String FILE =
			// "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX/siedler3_14.f8007e01f.dat";
			"/home/michael/Desktop/sounds/VL-212.DX4";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3846777822789324058L;

	private final byte[] data;

	private JSpinner offsetSpinner = null;

	private JPanel root = null; // @jve:decl-index=0:visual-constraint="10,10"

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JSpinner pixelLengthSpinner = null;

	private JLabel jLabel2 = null;

	private JSpinner imageWidthSpinner = null;

	private JLabel lineLength_bytes = null;

	private JLabel jLabel3 = null;

	private JLabel jLabel4 = null;

	private JLabel jLabel5 = null;

	private JLabel jLabel6 = null;

	private DataImage image;

	private JCheckBox littleEndian = null;

	private JTextField redMask = null;

	private JTextField greenMask = null;

	private JTextField blueMask = null;

	public ImageDisplay(File imageFile) throws IOException {
		super(imageFile.getAbsolutePath());

		this.data = getBytesOfFile(imageFile);
		initialize();

		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(new Dimension(602, 400));
		this.setContentPane(getRoot());

		getImage().setFocusable(true);
		getRoot();
		InputMap input = getRoot().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actions = getRoot().getActionMap();

		input.put(KeyStroke.getKeyStroke("UP"), "up");
		input.put(KeyStroke.getKeyStroke("DOWN"), "down");
		input.put(KeyStroke.getKeyStroke("LEFT"), "left");
		input.put(KeyStroke.getKeyStroke("RIGHT"), "right");
		actions.put("up", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5230566743343442027L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getImage().moveMarked(0, -1);
			}
		});
		actions.put("down", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -901408786185210944L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getImage().moveMarked(0, 1);
			}
		});
		actions.put("left", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2545364896702320931L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getImage().moveMarked(-1, 0);
			}
		});
		actions.put("right", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5756131018979838342L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getImage().moveMarked(1, 0);
			}
		});

		getImage().setFocusable(true);
	}

	private byte[] getBytesOfFile(File imageFile) throws IOException {
		FileInputStream in = new FileInputStream(imageFile);
		byte[] data = new byte[in.available()];
		in.read(data);
		in.close();
		return data;
	}

	/**
	 * This method initializes offsetSpinner
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getOffsetSpinner() {
		if (this.offsetSpinner == null) {
			this.offsetSpinner = new JSpinner(new SpinnerNumberModel(getImage().getOffset(), 0, this.data.length, 1));
			this.offsetSpinner.setBounds(new Rectangle(322, 4, 100, 20));
			this.offsetSpinner.addChangeListener(e -> getImage().setOffset((Integer) ImageDisplay.this.offsetSpinner.getValue()));
		}
		return this.offsetSpinner;
	}

	/**
	 * This method initializes root
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRoot() {
		if (this.root == null) {
			this.jLabel6 = new JLabel();
			this.jLabel6.setBounds(new Rectangle(451, 83, 43, 15));
			this.jLabel6.setText("Blau");
			this.jLabel5 = new JLabel();
			this.jLabel5.setBounds(new Rectangle(321, 84, 43, 15));
			this.jLabel5.setText("Grün");
			this.jLabel4 = new JLabel();
			this.jLabel4.setBounds(new Rectangle(197, 82, 43, 15));
			this.jLabel4.setText("Rot");
			this.jLabel3 = new JLabel();
			this.jLabel3.setBounds(new Rectangle(16, 80, 165, 15));
			this.jLabel3.setText("Farbmaske (0x.. = hex)");
			this.lineLength_bytes = new JLabel();
			this.lineLength_bytes.setBounds(new Rectangle(383, 62, 43, 15));
			this.lineLength_bytes.setText("");
			this.jLabel2 = new JLabel();
			this.jLabel2.setBounds(new Rectangle(14, 56, 268, 15));
			this.jLabel2.setText("Zeilenlänge (in px)");
			this.jLabel1 = new JLabel();
			this.jLabel1.setBounds(new Rectangle(13, 28, 270, 19));
			this.jLabel1.setText("Pixellänge");
			this.jLabel = new JLabel();
			this.jLabel.setText("Offset (von Beginn der Datei, in Bytes)");
			this.jLabel.setBounds(new Rectangle(13, 8, 272, 15));
			this.root = new JPanel();
			this.root.setLayout(null);
			this.root.setSize(new Dimension(579, 331));
			this.root.add(getOffsetSpinner(), null);
			this.root.add(this.jLabel, null);
			this.root.add(this.jLabel1, null);
			this.root.add(getPixelLengthSpinner(), null);
			this.root.add(this.jLabel2, null);
			this.root.add(getImageWidthSpinner(), null);
			this.root.add(this.lineLength_bytes, null);
			this.root.add(this.jLabel3, null);
			this.root.add(this.jLabel4, null);
			this.root.add(this.jLabel5, null);
			this.root.add(this.jLabel6, null);
			this.root.add(getImage(), null);
			this.root.add(getLittleEndian(), null);
			this.root.add(getRedMask(), null);
			this.root.add(getGreenMask(), null);
			this.root.add(getBlueMask(), null);
		}
		return this.root;
	}

	private DataImage getImage() {
		if (this.image == null) {
			this.image = new DataImage(this.data);
			this.image.setBounds(new Rectangle(10, 110, 1200, 500));
		}
		return this.image;
	}

	/**
	 * This method initializes pixelLengthSpinner
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getPixelLengthSpinner() {
		if (this.pixelLengthSpinner == null) {
			this.pixelLengthSpinner = new JSpinner(new SpinnerNumberModel(getImage()
					.getPixelLength(), 1, 100, 1));
			this.pixelLengthSpinner.setBounds(new Rectangle(323, 32, 100, 20));
			this.pixelLengthSpinner.addChangeListener(e -> {
				getImage().setPixelLength((Integer) ImageDisplay.this.pixelLengthSpinner.getValue());
				reloadLineLength();
			});
		}
		return this.pixelLengthSpinner;
	}

	private void reloadLineLength() {
		this.lineLength_bytes.setText(getImage().getPixelLength()
				* getImage().getImageWidth() + " Bytes");
	}

	/**
	 * This method initializes imageWidthSpinner
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getImageWidthSpinner() {
		if (this.imageWidthSpinner == null) {
			this.imageWidthSpinner = new JSpinner(new SpinnerNumberModel(getImage()
					.getImageWidth(), 1, 1000, 1));
			this.imageWidthSpinner.setBounds(new Rectangle(325, 58, 100, 20));
			this.imageWidthSpinner.addChangeListener(e -> {
				getImage().setImageWidth((Integer) ImageDisplay.this.imageWidthSpinner.getValue());
				reloadLineLength();
			});
		}
		return this.imageWidthSpinner;
	}

	/**
	 * This method initializes littleEndian
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getLittleEndian() {
		if (this.littleEndian == null) {
			this.littleEndian = new JCheckBox();
			this.littleEndian.setBounds(new Rectangle(420, 38, 195, 17));
			this.littleEndian.setText("Little endian");
			this.littleEndian.setSelected(getImage().isLittleEndian());
			this.littleEndian.addChangeListener(e -> getImage().setLittleEndian(ImageDisplay.this.littleEndian.isSelected()));
		}
		return this.littleEndian;
	}

	/**
	 * This method initializes redMask
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRedMask() {
		if (this.redMask == null) {
			this.redMask = new JTextField();
			this.redMask.setBounds(new Rectangle(248, 80, 69, 24));
			this.redMask.setText("" + getImage().getRedMask());
			this.redMask.getDocument().addDocumentListener(
					new DocumentListener() {
						private void changed() {
							getImage().setRedMask(loadMaskField(ImageDisplay.this.redMask));
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							changed();
						}
					});
		}
		return this.redMask;
	}

	protected long loadMaskField(JTextField maskField) {
		String text = maskField.getText();
		long value = 0;
		boolean correct = false;
		try {
			if (text.matches("\\d+")) {
				value = Long.parseLong(text);
				correct = true;
			} else if (text.matches("0[xX][0-9a-f]+")) {
				value = Long.parseLong(text.substring(2), 16);
				correct = true;
			} else if (text.matches("0[bB][01]+")) {
				value = Long.parseLong(text.substring(2), 2);
				correct = true;
			}
		} catch (NumberFormatException e) {
			value = 0;
			correct = false;
		}

		if (correct) {
			maskField.setBackground(Color.WHITE);
		} else {
			maskField.setBackground(Color.RED);
		}
		return value;
	}

	/**
	 * This method initializes greenMask
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGreenMask() {
		if (this.greenMask == null) {
			this.greenMask = new JTextField();
			this.greenMask.setBounds(new Rectangle(370, 79, 77, 25));
			this.greenMask.setText("" + getImage().getGreenMask());

			this.greenMask.getDocument().addDocumentListener(
					new DocumentListener() {
						private void changed() {
							getImage().setGreenMask(loadMaskField(ImageDisplay.this.greenMask));
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							changed();
						}
					});
		}
		return this.greenMask;
	}

	/**
	 * This method initializes blueMask
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getBlueMask() {
		if (this.blueMask == null) {
			this.blueMask = new JTextField();
			this.blueMask.setBounds(new Rectangle(498, 77, 96, 28));
			this.blueMask.setText("" + getImage().getBlueMask());
			this.blueMask.getDocument().addDocumentListener(
					new DocumentListener() {
						private void changed() {
							getImage().setBlueMask(loadMaskField(ImageDisplay.this.blueMask));
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						@Override
						public void changedUpdate(DocumentEvent e) {
							changed();
						}
					});
		}
		return this.blueMask;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new ImageDisplay(new File(FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
} // @jve:decl-index=0:visual-constraint="10,10"