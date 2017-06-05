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
package jsettlers.graphics.ui.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used in ANT build scripts to convert an XML layout file to a java class.
 * 
 * @author michael
 *
 */
public class LayoutConverter {
	public static String PACKAGE = "jsettlers.graphics.ui.layout";

	private File sourceXMLDirectory;
	private File genDirectory;
	private String layoutName;

	public void setGenDirectory(File genDirectory) {
		this.genDirectory = genDirectory;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public void setSourceXMLDirectory(File sourceXMLDirectory) {
		this.sourceXMLDirectory = sourceXMLDirectory;
	}

	public void execute() throws IOException {
		try {
			String name = layoutName;
			if (name != null) {
				convertLayout(name);
			} else {
				Pattern filePattern = Pattern.compile("^(\\w+)\\.xml$");
				for (File f : sourceXMLDirectory.listFiles()) {
					if (!f.isFile() || f.isHidden()) {
						continue;
					}
					Matcher m = filePattern.matcher(f.getName());
					if (m.matches()) {
						convertLayout(m.group(1));
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void convertLayout(String name) throws IOException {
		File xmlFile = new File(sourceXMLDirectory, name + ".xml");
		if (!xmlFile.isFile()) {
			throw new IOException("Could not find source file: " + xmlFile);
		}
		LayoutLoader loader = new LayoutLoader();
		loader.setDtdDirectory(sourceXMLDirectory);
		LayoutSourceGenerator sourceGenerator = loader.loadFromXML(name, xmlFile);

		// size alternatives
		for (EGeneratedLayoutSize s : EGeneratedLayoutSize.values()) {
			File sizeXmlFile = new File(sourceXMLDirectory, name + "." + s.toString().toLowerCase(Locale.ENGLISH) + ".xml");
			if (sizeXmlFile.isFile()) {
				sourceGenerator.addSize(s, loader.loadLayoutFromXML(sizeXmlFile));
			}
		}

		// now write
		File genPackage = genDirectory;
		for (String s : PACKAGE.split("\\.")) {
			genPackage = new File(genPackage, s);
		}
		genPackage.mkdirs();

		File genFile = new File(genPackage, name + ".java");
		sourceGenerator.setPackageName(PACKAGE);
		try (FileWriter writer = new FileWriter(genFile)) {
			sourceGenerator.writeSourceTo(writer);
		}
		System.out.println("Converted " + xmlFile + " -> " + genFile);
	}

	public static void main(String[] args) throws IOException {
		LayoutConverter c = new LayoutConverter();
		c.setSourceXMLDirectory(new File(args[0]));
		c.setGenDirectory(new File(args[1]));
		c.execute();
	}
}
