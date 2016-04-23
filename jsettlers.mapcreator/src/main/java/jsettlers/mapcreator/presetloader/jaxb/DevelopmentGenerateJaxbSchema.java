/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.presetloader.jaxb;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

@SuppressWarnings("javadoc")
public class DevelopmentGenerateJaxbSchema {

	public static void main(String[] args) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Presets.class);
		SchemaOutputResolver sor = new SchemaOutputResolver() {

			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				File file = new File("src/jsettlers/mapcreator/presetloader/preset.xsd");
				StreamResult result = new StreamResult(file);
				result.setSystemId(file.toURI().toURL().toString());
				return result;
			}
		};
		jaxbContext.generateSchema(sor);
		System.out.println("Finished!");
	}
}
