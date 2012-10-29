package jsettlers.xmlcoder;

import java.io.FileOutputStream;
import java.io.IOException;

import jsettlers.network.webserver.xml.AEXMLEncoder;

public class XMLEncoderTest {
	private int intA;
	private float floatA;
	private String stringA;
	private String[] stringArray;
	private double[] doubleArray;
	private InnerTestClass innerObject;

	public static void main(String args[]) throws IllegalArgumentException, IOException {
		XMLEncoderTest o = getInitializedTestObject();

		new AEXMLEncoder(new FileOutputStream("test.xml")).writeObject(o);
	}

	private static XMLEncoderTest getInitializedTestObject() {
		XMLEncoderTest o = new XMLEncoderTest();
		o.intA = 42;
		o.floatA = 42.42f;
		o.stringA = "hello world";
		o.stringArray = new String[] { "String1", "string2", null, null };
		o.doubleArray = new double[] { 0.123, 1, 2, 3, 4, 5 };
		o.setInnerObject(new InnerTestClass());
		o.getInnerObject().setInnerFloat(123.4f);
		o.getInnerObject().setInnerInt(1343);
		o.getInnerObject().setInnerString2("inner Hello World String");
		return o;
	}

	public int getIntA() {
		return intA;
	}

	public void setIntA(int intA) {
		this.intA = intA;
	}

	public float getFloatA() {
		return floatA;
	}

	public void setFloatA(float floatA) {
		this.floatA = floatA;
	}

	public String getStringA() {
		return stringA;
	}

	public void setStringA(String stringA) {
		this.stringA = stringA;
	}

	public String[] getStringArray() {
		return stringArray;
	}

	public void setStringArray(String[] stringArray) {
		this.stringArray = stringArray;
	}

	public double[] getDoubleArray() {
		return doubleArray;
	}

	public void setDoubleArray(double[] doubleArray) {
		this.doubleArray = doubleArray;
	}

	public InnerTestClass getInnerObject() {
		return innerObject;
	}

	public void setInnerObject(InnerTestClass innerObject) {
		this.innerObject = innerObject;
	}
}
