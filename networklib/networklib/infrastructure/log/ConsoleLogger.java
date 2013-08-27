package networklib.infrastructure.log;

/**
 * Logger that directly send the output to the standard outout.
 * 
 * @author Andreas Eberle
 * 
 */
public class ConsoleLogger extends StreamLogger {

	public ConsoleLogger(String loggerId) {
		super(loggerId, System.out);
	}
}
