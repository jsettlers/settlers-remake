package jsettlers.xmlcoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.network.webserver.xml.AEXMLDecoder;

import org.xml.sax.SAXException;

public class XMLDecoderTest {
	public static void main(String args[]) throws FileNotFoundException, IOException, SAXException {
		AEXMLDecoder decoder = new AEXMLDecoder(new FileInputStream("test.xml"));
		XMLEncoderTest o = decoder.readObject();
		System.out.println(o);
	}
}
