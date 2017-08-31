package jsettlers.common.utils.collections.map;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.Test;

import jsettlers.testutils.TestUtils;

/**
 * Created by Andreas Eberle on 16.05.2016.
 */
public class ArrayListMapTest {

	@Test
	public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
		ArrayListMap<String, String> map = new ArrayListMap<>();

		for (int i = 0; i < 17; i++) { // add more than default capacity of 16 to cause an increase in capacity
			map.put("key_" + i, "value_" + i);
		}

		for (int i = 0; i < 12; i++) { // remove most of the elements, to make sure a newly created map wouldn't increase its capacity.
			map.remove("key_" + i);
		}

		ArrayListMap<String, String> readMap = TestUtils.serializeAndDeserialize(map);

		ByteArrayOutputStream byteOutStream1 = new ByteArrayOutputStream();
		ObjectOutputStream oos1 = new ObjectOutputStream(byteOutStream1);
		oos1.writeObject(map);
		oos1.close();

		ByteArrayOutputStream byteOutStream2 = new ByteArrayOutputStream();
		ObjectOutputStream oos2 = new ObjectOutputStream(byteOutStream2);
		oos2.writeObject(readMap);
		oos2.close();

		byte[] bytes1 = byteOutStream1.toByteArray();
		byte[] bytes2 = byteOutStream2.toByteArray();

		assertArrayEquals(bytes1, bytes2);
	}
}
