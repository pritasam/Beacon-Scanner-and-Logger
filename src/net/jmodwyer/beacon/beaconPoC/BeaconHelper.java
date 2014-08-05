package net.jmodwyer.beacon.beaconPoC;

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
	 * Converts the Radius Networks beacon proximity value passed in and returns an
	 * appropriate human readable String.
	 * @param proximity double value expressing proximity in metres from Radius Networks beacon class
	 * @return human readable String representing that proximity
	 */
	public static String getProximityString(double proximity) {
		String proximityString;
		if (proximity == -1.0) {
			// -1.0 is passed back by the SDK to indicate an unknown distance
			proximityString = "Unknown";
		} else if (proximity < 0.5) {
			proximityString = "Immediate";
		} else if (proximity < 2.0) {
				proximityString = "Near";
		} else {
			proximityString = "Far";
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
