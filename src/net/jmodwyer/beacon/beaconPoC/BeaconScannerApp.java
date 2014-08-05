package net.jmodwyer.beacon.beaconPoC;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import android.app.Application;

public class BeaconScannerApp extends Application {

	/**
	 * Global reference to FileHelper instance.
	 */
	private FileHelper fileHelper;
	private BackgroundPowerSaver backgroundPowerSaver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		fileHelper = new FileHelper(getExternalFilesDir(null));
		backgroundPowerSaver = new BackgroundPowerSaver(this);
	}
	
	/**
	 * 
	 */
	public FileHelper getFileHelper() {
		return this.fileHelper;
	}

}
