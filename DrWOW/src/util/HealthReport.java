package util;

/**
 * A health report is a collection of information gathered by a server channel after
 * it asks the server to return it's status report. The health report may also contain
 * information not given by the server, such as the amount of time it took to get the
 * report and ping time to the server.
 */
public class HealthReport {
	/**
	 * Define what integer represents a TIMEOUT.
	 */
	public final static int TIMEOUT = 65535;
	
	private int pingTime;
	private double avgQueryTime;
	private double memoryUsed;
	private double memoryAvailable;
	
	/**
	 * Create a health report.
	 * @param _ping Ping response time.
	 * @param _queryTime Average query response time.
	 * @param _memoryUsed Memory used on the server.
	 * @param _memoryLeft Memory available on the server.
	 */
	public HealthReport(int _ping, double _queryTime, double _memoryUsed, double _memoryLeft) {
		pingTime = _ping;
		avgQueryTime = _queryTime;
		memoryUsed = _memoryUsed;
		memoryAvailable = _memoryLeft;
	}
	
	/**
	 * Return the number of milliseconds it took to contact the server and receive a
	 * response.
	 * @return Ping time in milliseconds, or HealthReport.TIMEOUT if the server timed out.
	 */
	public int getResponseTime() {
		return pingTime;
	}
	
	/**
	 * Return the number of seconds (with floating-point precision) it takes to reply to
	 * a query on average.
	 * @return Floating-point number.
	 */
	public double getAverageQueryTime() {
		return avgQueryTime;
	}
	
	/**
	 * Get the amount of memory being used on the server.
	 * @return Memory being used.
	 */
	public double getMemoryUsed() {
		return memoryUsed;
	}
	
	/**
	 * Return the maximum available memory on the server.
	 * @return Memory available.
	 */
	public double getMemoryAvailable() {
		return memoryAvailable;
	}
}
