package net.jmodwyer.ibeacon.ibeaconPoC;

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
	
	
}
