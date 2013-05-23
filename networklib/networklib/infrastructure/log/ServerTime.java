package networklib.infrastructure.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerTime {
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	public static String getTime() {
		return formatter.format(new Date());
	}
}
