package net.jmodwyer.ibeacon.ibeaconPoC;

import android.app.Application;

public class BeaconScannerApp extends Application {

	/**
	 * Global reference to FileHelper instance.
	 */
	private FileHelper fileHelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		fileHelper = new FileHelper(getExternalFilesDir(null));
	}
	
	/**
	 * 
	 */
	public FileHelper getFileHelper() {
		return this.fileHelper;
	}

}
