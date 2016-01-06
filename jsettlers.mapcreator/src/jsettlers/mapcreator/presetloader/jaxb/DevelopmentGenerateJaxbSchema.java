package jsettlers.mapcreator.presetloader.jaxb;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

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
