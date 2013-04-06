package wowServLibs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import wowServLibs.Hit.Type;

/**
 * Compute and track statistics related to WOW.
 */
public class Statistics {
	private static int totalHits;
	private static int totalAdds;
	private static int totalDeletes;
	private static int totalUpdates;
	private static int totalQueries;
	private static int today;
	private static int thisWeek;
	private static int lastWeek;
	private static int thisMonth;
	private static int lastMonth;
	private static long upTime = 0L;
	private static int onCampus;
	private static int offCampus;
	private static String cachedStats;
	private static Calendar lastCalculation;
	private static ArrayList<Hit> hits = new ArrayList<Hit>();

	/**
	 * Stores the query to the array of hits Shows if the database was accessed
	 * 
	 * @param ipAddress
	 *            IP Address from the query
	 */
	public static synchronized void addQueryHit(InetAddress ipAddress) {
		totalHits++;
		totalQueries++;
		hits.add(new Hit(Type.QUERY, ipAddress));
	}

	/**
	 * Stores the add query to the array of hits Shows if an element was added
	 * to the database
	 * 
	 * @param ipAddress
	 *            IP Address from the query
	 */
	public static synchronized void addAddHit(InetAddress ipAddress) {
		totalHits++;
		totalAdds++;
		hits.add(new Hit(Type.ADD, ipAddress));
	}

	/**
	 * Stores the delete query to the array of hits Shows if an element was
	 * deleted from the database
	 * 
	 * @param ipAddress
	 *            IP Address from the query
	 */
	public static synchronized void addDeleteHit(InetAddress ipAddress) {
		totalHits++;
		totalDeletes++;
		hits.add(new Hit(Type.DELETE, ipAddress));
	}

	/**
	 * Stores the update query to the array of hits Shows if an element was
	 * updated in the database
	 * 
	 * @param ipAddress
	 *            IP Address from the query
	 */
	public static synchronized void addUpdateHit(InetAddress ipAddress) {
		totalHits++;
		totalUpdates++;
		hits.add(new Hit(Type.UPDATE, ipAddress));
	}

	/**
	 * Returns the total amount of hits done to the database regardless of type
	 * 
	 * @return totalHits;
	 */
	public static synchronized int getTotalHits() {
		return totalHits;
	}

	/**
	 * Returns the total amount of adds done to the database
	 * 
	 * @return totalAdds
	 */
	public static synchronized int getTotalAdds() {
		return totalAdds;
	}

	/**
	 * Returns the total amount of deletes done to the database
	 * 
	 * @return totalDeletes
	 */
	public static synchronized int getTotalDeletes() {
		return totalDeletes;
	}

	/**
	 * Returns the total amount of updates done to the database
	 * 
	 * @return totalUpdates
	 */
	public static synchronized int getTotalUpdates() {
		return totalUpdates;
	}

	/**
	 * Returns the total amount of queries done to the database
	 * 
	 * @return totalQueries
	 */
	public static synchronized int getTotalQueries() {
		return totalQueries;
	}

	/**
	 * Generates a Hit File
	 * 
	 * @throws IOException
	 */
	public static void generateHitFile() throws IOException {
		final String dateOut = Log.dateFormat("yyyy/MM/dd hh:mm:ss aaa");

		PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(
				StatsConstants.STATS_FILE)));

		output.println("[Last Modified On " + dateOut + "]");
		output.println(StatsConstants.HITS_PROPERTY + ": " + 0);
		output.println(StatsConstants.ON_CAMPUS_PROPERTY + ": " + 0);
		output.println(StatsConstants.OFF_CAMPUS_PROPERTY + ": " + 0);
		output.println(StatsConstants.QUERY_PROPERTY + ": " + 0);
		output.println(StatsConstants.ADD_PROPERTY + ": " + 0);
		output.println(StatsConstants.DELETE_PROPERTY + ": " + 0);
		output.println(StatsConstants.UPDATE_PROPERTY + ": " + 0);
		output.println("");
		output.println(StatsConstants.AVG_HIT_DAY_PROPERTY + ": " + 0);
		output.println(StatsConstants.AVG_HIT_WEEK_PROPERTY + ": " + 0);
		output.println(StatsConstants.AVG_HIT_MONTH_PROPERTY + ": " + 0);
		output.println(StatsConstants.RATIO_PROPERTY + ": " + 0);
		output.println("");
		output.println(StatsConstants.TODAY_PROPERTY + ": " + 0);
		output.println(StatsConstants.THIS_WEEK_PROPERTY + ": " + 0);
		output.println(StatsConstants.LAST_WEEK_PROPERTY + ": " + 0);
		output.println(StatsConstants.THIS_MONTH_PROPERTY + ": " + 0);
		output.println(StatsConstants.LAST_MONTH_PROPERTY + ": " + 0);
		output.println(StatsConstants.UP_TIME_PROPERTY + ": " + 0);
		output.close();
	}

	/**
	 * Loads the hits file and parses it into the appropriate variables
	 * 
	 * @throws IOException
	 */
	public static void loadHitFile() throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(
				StatsConstants.STATS_FILE));

		String buffer = null;
		String temp = "";
		while ((buffer = input.readLine()) != null) {
			temp += buffer + "\n";
		}

		cachedStats = temp;

		String parse = null;
		String[] tokens = null;

		tokens = temp.split("#|\n");
		/*
		 * final String lastModified = "Last Modified On"; final String
		 * datestamp = tokens[0].trim().replace("[", "").replace("]",
		 * "").replace(lastModified, "");
		 * 
		 * // Parse out the last modified date. Date lastParse = null; try {
		 * SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyy/MM/dd hh:mm:ss aaa"); lastParse =
		 * formatter.parse(datestamp);
		 * 
		 * lastCalculation = Calendar.getInstance();
		 * lastCalculation.setTime(lastParse); } catch (ParseException e) {
		 * System.err.println(StatsConstants.STATS_FILE + " is corrupted: " +
		 * e); throw new IOException("Parser could not read date stamp from " +
		 * StatsConstants.STATS_FILE); }
		 */

		for (int i = 0; i < tokens.length; i++) {
			parse = tokens[i].trim();

			if (parse.contains(StatsConstants.HITS_PROPERTY)) {
				totalHits = Integer.parseInt(parse
						.substring(StatsConstants.HITS_PROPERTY.length() + 2));
			} else if (parse.contains(StatsConstants.ADD_PROPERTY)) {
				totalAdds = Integer.parseInt(parse
						.substring(StatsConstants.ADD_PROPERTY.length() + 2));
			} else if (parse.contains(StatsConstants.DELETE_PROPERTY)) {
				totalDeletes = Integer
						.parseInt(parse
								.substring(StatsConstants.DELETE_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.UPDATE_PROPERTY)) {
				totalUpdates = Integer
						.parseInt(parse
								.substring(StatsConstants.UPDATE_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.TODAY_PROPERTY)) {
				today = Integer.parseInt(parse
						.substring(StatsConstants.TODAY_PROPERTY.length() + 2));
			} else if (parse.contains(StatsConstants.THIS_WEEK_PROPERTY)) {
				thisWeek = Integer
						.parseInt(parse
								.substring(StatsConstants.THIS_WEEK_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.THIS_MONTH_PROPERTY)) {
				thisMonth = Integer
						.parseInt(parse
								.substring(StatsConstants.THIS_MONTH_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.QUERY_PROPERTY)) {
				totalQueries = Integer.parseInt(parse
						.substring(StatsConstants.QUERY_PROPERTY.length() + 2));
			} else if (parse.contains(StatsConstants.UP_TIME_PROPERTY)) {
				upTime = Long
						.parseLong(parse
								.substring(StatsConstants.UP_TIME_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.ON_CAMPUS_PROPERTY)) {
				onCampus = Integer
						.parseInt(parse
								.substring(StatsConstants.ON_CAMPUS_PROPERTY
										.length() + 2));
			} else if (parse.contains(StatsConstants.OFF_CAMPUS_PROPERTY)) {
				offCampus = Integer
						.parseInt(parse
								.substring(StatsConstants.OFF_CAMPUS_PROPERTY
										.length() + 2));
			}
		}

		lastCalculation = Calendar.getInstance();
	}

	/**
	 * Reads in the hits.log file and converts it into a string to send to the
	 * requester as a formatted string to retrieve the stats
	 * 
	 * @return stats The statistics string containing the hits.log information
	 * @throws IOException
	 */
	public static synchronized String getStats() {
		return cachedStats;
	}

	/**
	 * Get the time (in milliseconds) of the last calculation. This is a
	 * timestamp, not the number of milliseconds that occurred since the last
	 * calculation.
	 * 
	 * @return A timestamp (as a long number) of the last calculation.
	 */
	public static synchronized long getLastCalculationInMillis() {
		return lastCalculation.getTimeInMillis();
	}

	/**
	 * Calculates that statistics and updates the file if necessary
	 */
	public static synchronized void calculateStats() throws IOException {
		final Calendar now = Calendar.getInstance();

		// Store uptime in seconds.
		upTime += Math.ceil((now.getTimeInMillis() / 1000)
				- (lastCalculation.getTimeInMillis() / 1000));

		// Convert current uptime (seconds) to days.
		float daysUp = (float) (upTime) / 86400f;

		if (daysUp < 1) {
			daysUp = 1f;
		}

		final float monthFactor = daysUp / 30f;
		final float weekFactor = daysUp / 7f;
		float avgDay = totalHits / ((daysUp > 0) ? daysUp : 1f);
		float avgWeek = totalHits / (weekFactor > 0 ? weekFactor : 1f);
		float avgMonth = totalHits / (monthFactor > 0 ? monthFactor : 1f);
		int hitsToday = today;

		if (now.get(Calendar.MONTH) != lastCalculation.get(Calendar.MONTH)) {
			// The month has changed since the last calculation.
			lastMonth = thisMonth;
			thisMonth = 0;
		}

		if (now.get(Calendar.WEEK_OF_MONTH) != lastCalculation
				.get(Calendar.WEEK_OF_MONTH)) {
			lastWeek = thisWeek;
			thisWeek = 0;
		}

		if (now.get(Calendar.DAY_OF_MONTH) != lastCalculation
				.get(Calendar.DAY_OF_MONTH)) {
			System.out.println(now.get(Calendar.DAY_OF_MONTH));
			System.out.println(lastCalculation.get(Calendar.DAY_OF_MONTH));
			today = 0;
		}

		final String dateOut = Log.dateFormat("yyyy/MM/dd hh:mm:ss aaa");

		// Extract the IP address ranges from the config.
		int[] startRange = new int[4];
		int[] endRange = new int[4];
		final String[] start = GlobalConfig.getIpStartRange().split("\\.");
		final String[] end = GlobalConfig.getIpEndRange().split("\\.");
		for (int i = 0; i < 4; i++) {
			startRange[i] = Integer.parseInt(start[i]);
			endRange[i] = Integer.parseInt(end[i]);
		}

		ArrayList<Hit> tempList = new ArrayList<Hit>(hits);

		// Process hits.
		processHits: for (Hit hit : tempList) {
			hits.remove(hit);

			if (hit.getTime().get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
				thisMonth++;
			} else {
				lastMonth++;
			}

			if (hit.getTime().get(Calendar.WEEK_OF_YEAR) == now
					.get(Calendar.WEEK_OF_YEAR)) {
				thisWeek++;
			} else {
				lastWeek++;
			}

			if (hit.getTime().get(Calendar.DAY_OF_YEAR) == now
					.get(Calendar.DAY_OF_YEAR)) {
				today++;
			} else {
				hitsToday++;
			}

			// Find whether or not the hit came from on-campus.
			int[] range = new int[4];
			final String[] rangeStr = hit.getIpAddress().getHostAddress()
					.split("\\.");
			for (int i = 0; i < 4; i++) {
				range[i] = Integer.parseInt(rangeStr[i]);

				if (!(range[i] >= startRange[i] && range[i] <= endRange[i])) {
					// Range not on campus.
					offCampus += 1;
					continue processHits;
				}
			}

			onCampus += 1;
		}

		tempList = null;

		float finalOnCampus = onCampus > 0 ? 1f : 0f;
		float finalOffCampus = (float) (offCampus / ((onCampus > 0) ? onCampus
				: ((offCampus != 0) ? offCampus : 1f)));

		final String ratioOnOff = finalOnCampus + ":" + finalOffCampus;

		PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(
				StatsConstants.STATS_FILE)));
		output.println("[Last Modified On " + dateOut + "]");
		output.println(StatsConstants.HITS_PROPERTY + ": " + totalHits);
		output.println(StatsConstants.ON_CAMPUS_PROPERTY + ": " + onCampus);
		output.println(StatsConstants.OFF_CAMPUS_PROPERTY + ": " + offCampus);
		output.println(StatsConstants.QUERY_PROPERTY + ": " + totalQueries);
		output.println(StatsConstants.ADD_PROPERTY + ": " + totalAdds);
		output.println(StatsConstants.DELETE_PROPERTY + ": " + totalDeletes);
		output.println(StatsConstants.UPDATE_PROPERTY + ": " + totalUpdates);
		output.println("");
		output.println(StatsConstants.AVG_HIT_DAY_PROPERTY + ": " + avgDay);
		output.println(StatsConstants.AVG_HIT_WEEK_PROPERTY + ": " + avgWeek);
		output.println(StatsConstants.AVG_HIT_MONTH_PROPERTY + ": " + avgMonth);
		output.println(StatsConstants.RATIO_PROPERTY + ": " + ratioOnOff);
		output.println("");
		output.println(StatsConstants.TODAY_PROPERTY + ": " + today);
		output.println(StatsConstants.THIS_WEEK_PROPERTY + ": " + thisWeek);
		output.println(StatsConstants.LAST_WEEK_PROPERTY + ": " + lastWeek);
		output.println(StatsConstants.THIS_MONTH_PROPERTY + ": " + thisMonth);
		output.println(StatsConstants.LAST_MONTH_PROPERTY + ": " + lastMonth);
		output.println(StatsConstants.UP_TIME_PROPERTY + ": " + upTime);
		output.close();

		BufferedReader input = new BufferedReader(new FileReader(
				StatsConstants.STATS_FILE));

		cachedStats = "";
		String temp = null;
		while ((temp = input.readLine()) != null) {
			cachedStats += temp + "\n";
		}

		input.close();

		lastCalculation = now;

		System.out.println("Stats have been re-calculated.");
	}

	/**
	 * Override the finalize method, which is called by the garbage collector.
	 * The new method will do a last-minute stats calculation.
	 */
	@Override
	protected void finalize() throws Throwable {
		calculateStats();

		super.finalize();
	}
}
