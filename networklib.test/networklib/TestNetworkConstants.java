package networklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.HashSet;

import org.junit.Test;

public class TestNetworkConstants {

	@Test
	public void testKeysUnique() throws IllegalArgumentException, IllegalAccessException {
		HashSet<Integer> keys = new HashSet<Integer>();

		for (Field field : NetworkConstants.Keys.class.getFields()) {
			assertEquals(int.class, field.getType());
			int value = field.getInt(null);

			assertFalse("The key " + value + " of field " + field + " is used at least twice!", keys.contains(value));
			keys.add(value);
		}
	}
}
