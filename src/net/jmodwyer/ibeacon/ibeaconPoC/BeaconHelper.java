package net.jmodwyer.ibeacon.ibeaconPoC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BeaconHelper {

	/**
	 * Decodes the Radius Networks IBeacon proximity constant passed in and returns an
	 * appropriate human readable String.
	 * @param proximity constant expressing proximity from Radius Networks IBeacon class
	 * @return human readable String representing that proximity
	 */
	public static String getProximityString(int proximity) {
		String proximityString;
		switch (proximity) {
		case 1 : proximityString = "Immediate";
		break;
		case 2 : proximityString = "Near";
		break;
		case 3 : proximityString = "Far";
		break;
		default: proximityString = "Unknown";
		}
		return proximityString;
	}
	
	/**
	 * Get the current date and time formatted as expected by PBS' application.
	 * @return
	 */
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.SSS", Locale.US);
	    Date now = new Date();
	    return sdf.format(now);
	}
	
}
