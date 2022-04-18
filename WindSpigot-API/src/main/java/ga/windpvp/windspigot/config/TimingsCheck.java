package ga.windpvp.windspigot.config;

public class TimingsCheck {

	private static boolean enableTimings;

	protected static void setEnableTimings(boolean enableTimings) {
		TimingsCheck.enableTimings = enableTimings;
	}

	public static boolean getEnableTimings() {
		return enableTimings;
	}

}
