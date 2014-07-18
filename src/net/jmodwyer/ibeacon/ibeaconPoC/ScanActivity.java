package net.jmodwyer.ibeacon.ibeaconPoC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    
    // This StringBuffer will hold the scan data for any given scan.  
    private StringBuffer logString;
   
    // Preferences - will actually have a boolean value when loaded.
    private Boolean index;
    private Boolean uuid;
	private Boolean majorMinor;
	private Boolean rssi;
	private Boolean proximity;
	private Boolean power;
	private Boolean timestamp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		verifyBluetooth();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		iBeaconManager = IBeaconManager.getInstanceForApplication(this);
		//iBeaconManager.setForegroundScanPeriod(10);
		iBeaconManager.bind(this);
		region = new Region("myRangingUniqueId", null, null, null);
		//fileHelper = new FileHelper(getExternalFilesDir(null));
		BeaconScannerApp app = (BeaconScannerApp)this.getApplication();
		fileHelper = app.getFileHelper();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
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
	
 	// Handle the user selecting "Settings" from the action bar.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    	case R.id.Settings:
	            // Show settings
	    		Intent api = new Intent(this, AppPreferenceActivity.class);
	            startActivityForResult(api, 0);
	            return true;
	    	case R.id.action_listfiles:
	    		// Launch list files activity
	    		Intent fhi = new Intent(this, FileHandlerActivity.class);
	            startActivity(fhi);
	            return true;	    			    		
	        default:
	            return super.onOptionsItemSelected(item);
	     }
	 }
	
	/**
	 * Start and stop scanning, and toggle button label appropriately.
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
		
		// Reset event counter
		eventNum = 1;
		// Get current values for logging preferences
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);		
	    HashMap <String, Object> prefs = new HashMap<String, Object>();
	    prefs.putAll(sharedPrefs.getAll());
	    
	    index = (Boolean)prefs.get("index");
	    uuid = (Boolean)prefs.get("uuid");
		majorMinor = (Boolean)prefs.get("majorMinor");
		rssi = (Boolean)prefs.get("rssi"); 
		proximity = (Boolean)prefs.get("proximity");
		power = (Boolean)prefs.get("power");
		timestamp = (Boolean)prefs.get("timestamp"); 
		
		logToDisplay("Scanning...");
		
		// Initialise scan log
		logString = new StringBuffer();
		
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
		String scanData = logString.toString();
		if (scanData.length() > 0) {
			// Write file
			fileHelper.createFile(scanData);
			// Display file created message.
			Toast.makeText(getBaseContext(),
					"File saved to:" + getFilesDir().getAbsolutePath(),
					Toast.LENGTH_SHORT).show();
			scanButton.setText(MODE_STOPPED);
		} else {
			// We didn't get any data, so there's no point writing an empty file.
			Toast.makeText(getBaseContext(),
					"No data captured during scan, output file will not be created.",
					Toast.LENGTH_SHORT).show();
			scanButton.setText(MODE_STOPPED);
		}
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

		StringBuffer scan = new StringBuffer();
		
		if (index.booleanValue()) {
			scan.append(eventNum++ + "");
		}				
		
		if (uuid.booleanValue()) {
			scan.append(" UUID: " + iBeacon.getProximityUuid());
		}		
		
		if (majorMinor.booleanValue()) {
			scan.append(" Maj. Mnr.: " + iBeacon.getMajor() + "-" + iBeacon.getMinor());
		}
		
		if (rssi.booleanValue()) {
			scan.append(" RSSI: " + iBeacon.getRssi());
		}
				
		if (proximity.booleanValue()) {
			scan.append(" Proximity: " + BeaconHelper.getProximityString(iBeacon.getProximity()));
		}
		
		if (power.booleanValue()) {
			scan.append(" Power: "+ iBeacon.getTxPower());
		}
		
		if (timestamp.booleanValue()) {
			scan.append(" Timestamp: " + BeaconHelper.getCurrentTimeStamp());
		}
	    
		logToDisplay(scan.toString());
		scan.append("\n");
		logString.append(scan.toString());
		
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
