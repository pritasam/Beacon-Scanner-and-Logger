package net.jmodwyer.beacon.beaconPoC;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import android.app.Application;

public class BeaconScannerApp extends Application {

	/**
	 * Global reference to FileHelper instance.
	 */
	private FileHelper fileHelper;
	private BackgroundPowerSaver backgroundPowerSaver;
	private BeaconManager beaconManager;
	
	public BeaconManager getBeaconManager() {
		return beaconManager;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		fileHelper = new FileHelper(getExternalFilesDir(null));
		// Allow scanning to continue in the background.
		backgroundPowerSaver = new BackgroundPowerSaver(this);
		beaconManager = BeaconManager.getInstanceForApplication(this);
	}
	
	/**
	 * 
	 */
	public FileHelper getFileHelper() {
		return this.fileHelper;
	}

}
