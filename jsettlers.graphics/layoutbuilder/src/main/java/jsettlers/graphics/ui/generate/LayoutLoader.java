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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class reads an XML layout file and generates a panel structure out of it.
 * 
 * @author michael
 *
 */
public class LayoutLoader {

	private final class LayoutXmlHandler extends DefaultHandler {
		private LinkedList<LayoutPanel> panelStack = new LinkedList<>();
		private LayoutPanel root;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			switch (qName) {
			case "element":
				LayoutPanel panel = new LayoutPanel(attributes);
				if (!panelStack.isEmpty()) {
					panelStack.getLast().addChild(panel);
				} else {
					root = panel;
				}
				panelStack.add(panel);
				break;
			case "image":
				panelStack.getLast().addArgument(new ImageArgument(attributes));
				break;
			case "material":
				panelStack.getLast().addArgument(new UncheckedEnumArgument(attributes, "material", "jsettlers.common.material.EMaterialType"));
				break;
			case "soldierType":
				panelStack.getLast().addArgument(new UncheckedEnumArgument(attributes, "soldierType", "jsettlers.common.movable.ESoldierType"));
				break;
			case "localized":
				panelStack.getLast().addArgument(new LocalizedArgument(attributes));
				break;
			case "action":
				panelStack.getLast().addArgument(new ActionArgument(attributes));
				break;
			case "null":
				panelStack.getLast().addArgument(new NullArgument());
				break;
			case "font-size":
				panelStack.getLast().addArgument(new UncheckedEnumArgument(attributes, "size", "go.graphics.text.EFontSize"));
				break;
			case "alignment":
				panelStack.getLast().addArgument(
						new UncheckedEnumArgument(attributes, "alignment", "jsettlers.graphics.ui.Label.EHorizontalAlignment"));
				break;

			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (qName) {
			case "element":
				panelStack.removeLast();
				break;
			}
		}

		public LayoutPanel getRootPanel() {
			return root;
		}
	}

	private File dtdDirectory = new File("");

	public LayoutSourceGenerator loadFromXML(String layoutName, File file) throws IOException {
		return new LayoutSourceGenerator(layoutName, loadLayoutFromXML(file));
	}

	public void setDtdDirectory(File dtdDirectory) {
		this.dtdDirectory = dtdDirectory;
	}

	public LayoutPanel loadLayoutFromXML(File file) throws IOException {
		try {
			InputStream is = new FileInputStream(file);
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(true);
			saxParserFactory.setNamespaceAware(true);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			LayoutXmlHandler xmlHandler = new LayoutXmlHandler();
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.setEntityResolver((publicId, systemId) -> {
				if (systemId.contains("layout.dtd")) {
					return new InputSource(new FileInputStream(new File(dtdDirectory, "layout.dtd")));
				} else {
					return null;
				}
			});
			xmlReader.parse(new InputSource(is));

			return xmlHandler.getRootPanel();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

}
