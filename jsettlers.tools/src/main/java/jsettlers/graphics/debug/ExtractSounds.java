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
package jsettlers.graphics.debug;

import jsettlers.graphics.image.reader.bytereader.ByteReader;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.main.swing.SwingManagedJSettlers;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Exports all sounds as wav files.
 *
 * @author Michael Zangl
 */
public class ExtractSounds extends SoundManager {

	private ExtractSounds() {
		super(null);
	}

	public static void main(String[] args) throws IOException {
		SwingManagedJSettlers.setupResources(true, args);

		ByteReader file = openSoundFile();
		int[][] starts = getSoundStarts(file);

		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File base = fc.getSelectedFile();

			for (int i = 0; i < starts.length; i++) {
				File groupDir = new File(base, "" + i);
				groupDir.mkdir();

				for (int j = 0; j < starts[i].length; j++) {
					File seqFile = new File(groupDir, j + ".wav");
					short[] data = getSoundData(file, starts[i][j]);
					exportTo(data, seqFile);
					System.out.println("Exported file " + i + "." + j);
				}
			}
		}
	}

	/**
	 * Export data to a given wav file path.
	 *
	 * @param data
	 * @param seqFile
	 * @throws IOException
	 */
	private static void exportTo(short[] data, File seqFile) throws IOException {
		FileOutputStream os = new FileOutputStream(seqFile);
		os.write(new byte[] { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'A', 'V',
				'E', 'f', 'm', 't', ' ', 0x10, 0, 0, 0, 1, 0, 2, 0, 0x22,
				(byte) 0x56, 0, 0, 0x10, (byte) 0xb1, 2, 0, 4, 0, 0x10, 0, 'd',
				'a', 't', 'a' });
		for (short d : data) {
			os.write(d & 0xff);
			os.write((d / 256) & 0xff);
			os.write(d & 0xff);
			os.write((d / 256) & 0xff);
		}

		os.close();
	}
}
