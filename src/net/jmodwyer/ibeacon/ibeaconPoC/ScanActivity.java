package net.jmodwyer.ibeacon.ibeaconPoC;

import java.util.Collection;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

/**
 * Adapted from original code written by D Young of Radius Networks.
 * @author dyoung, jodwyer
 *
 */
public class ScanActivity extends Activity implements IBeaconConsumer {
    
	protected static final String TAG = "ScanActivity";
    private final String MODE_SCANNING = "Stop Scanning";
    private final String MODE_STOPPED = "Start Scanning";    
    private FileHelper fileHelper; 
    private IBeaconManager iBeaconManager;
    private Region region; 
    private int eventNum = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		verifyBluetooth();
		iBeaconManager = IBeaconManager.getInstanceForApplication(this);
		iBeaconManager.bind(this);
		region = new Region("myRangingUniqueId", null, null, null);
		fileHelper = new FileHelper(getExternalFilesDir(null));
		// Initialise scan button.
		getScanButton().setText(MODE_STOPPED);
    }
    
    @Override 
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }
    
    @Override 
    protected void onPause() {
    	super.onPause();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);    		
    }
    
    @Override 
    protected void onResume() {
    	super.onResume();
    	if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);    		
    }

    @Override
    public void onIBeaconServiceConnect() {}
    
    /**
     * 
     * @param view
     */
	public void onScanButtonClicked(View view) {
		toggleScanState();
	}
	
	/**
	 * 
	 */
	private void toggleScanState() {
		Button scanButton = getScanButton();
		String currentState = scanButton.getText().toString();
		if (currentState.equals(MODE_SCANNING)) {
			stopScanning(scanButton);
		} else {
			startScanning(scanButton);
		}
	}

	/**
	 * start looking for beacons.
	 */
	private void startScanning(Button scanButton) {
		// Set UI elements to the correct state.
		scanButton.setText(MODE_SCANNING);
		((EditText)findViewById(R.id.scanText)).setText("");
		logToDisplay("*** New Scan ***");
		//Start scanning again.
        iBeaconManager.setRangeNotifier(new RangeNotifier() {
        	@Override 
        	public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
        		if (iBeacons.size() > 0) {
        			Iterator <IBeacon> beaconIterator = iBeacons.iterator();
        			while (beaconIterator.hasNext()) {
        				IBeacon iBeacon = beaconIterator.next();
        				logBeaconData(iBeacon);
        			}
        		}
        	}
        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {   
        	// TODO - OK, what now then?
        }	
		fileHelper.prepareExternalFile();
	}

	/**
	 * Stop looking for beacons.
	 */
	private void stopScanning(Button scanButton) {
		try {
			iBeaconManager.stopRangingBeaconsInRegion(region);
			} catch (RemoteException e) {
				// TODO - OK, what now then?
			}
		// Flush location details to a file.
		fileHelper.closeExternalFile();
		// Display file created message.
		Toast.makeText(getBaseContext(),
				"File saved to:" + getFilesDir().getAbsolutePath(),
				Toast.LENGTH_SHORT).show();
		scanButton.setText(MODE_STOPPED);
	}

	/**
	 * 
	 * @return
	 */
	private Button getScanButton() {
		return (Button)findViewById(R.id.scanButton);
	}
	
    /**
     * 
     * @param iBeacon
     */
	private void logBeaconData(IBeacon iBeacon) {
		StringBuffer logString = new StringBuffer();
		
		logString.append(eventNum++
				+ " "
				+ iBeacon.getMajor() 
				+ "-" 
				+ iBeacon.getMinor()
				+ " RSSI: "
				+ iBeacon.getRssi()
				+ " Proximity: "
				+ BeaconHelper.getProximityString(iBeacon.getProximity())
				);

		logToDisplay(logString.toString());
		logString.append("\n");
		fileHelper.writeToExternalFile(logString.toString());
	}
    
	/**
	 * 
	 * @param line
	 */
    private void logToDisplay(final String line) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	    	EditText editText = (EditText)ScanActivity.this
    					.findViewById(R.id.scanText);
    	    	editText.append(line+"\n");            	
    	    }
    	});
    }
    
 	private void verifyBluetooth() {

		try {
			if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");			
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
			            System.exit(0);					
					}					
				});
				builder.show();
			}			
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");			
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
		            System.exit(0);					
				}
				
			});
			builder.show();
			
		}
		
	}	  
    
}
