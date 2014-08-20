package net.jmodwyer.beacon.beaconPoC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.jmodwyer.ibeacon.ibeaconPoC.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

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

/**
 * Adapted from original code written by D Young of Radius Networks.
 * @author dyoung, jodwyer
 *
 */
public class ScanActivity extends Activity implements BeaconConsumer {
    
	// Constant Declaration
	private static final String PREFERENCE_SCANINTERVAL = "scanInterval";
	private static final String PREFERENCE_TIMESTAMP = "timestamp";
	private static final String PREFERENCE_POWER = "power";
	private static final String PREFERENCE_PROXIMITY = "proximity";
	private static final String PREFERENCE_RSSI = "rssi";
	private static final String PREFERENCE_MAJORMINOR = "majorMinor";
	private static final String PREFERENCE_UUID = "uuid";
	private static final String PREFERENCE_INDEX = "index";
    private static final String MODE_SCANNING = "Stop Scanning";
    private static final String MODE_STOPPED = "Start Scanning";
    protected static final String TAG = "ScanActivity";
    
    private FileHelper fileHelper; 
    private BeaconManager beaconManager;
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
	private String scanInterval;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		verifyBluetooth();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		BeaconScannerApp app = (BeaconScannerApp)this.getApplication();
		beaconManager = app.getBeaconManager();
		//beaconManager.setForegroundScanPeriod(10);

		// Add parser for iBeacons;
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		beaconManager.bind(this);
		
		region = new Region("myRangingUniqueId", null, null, null);

		fileHelper = app.getFileHelper();
		// Initialise scan button.
		getScanButton().setText(MODE_STOPPED);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBeaconServiceConnect() {}
    
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
	    
	    index = (Boolean)prefs.get(PREFERENCE_INDEX);
	    uuid = (Boolean)prefs.get(PREFERENCE_UUID);
		majorMinor = (Boolean)prefs.get(PREFERENCE_MAJORMINOR);
		rssi = (Boolean)prefs.get(PREFERENCE_RSSI); 
		proximity = (Boolean)prefs.get(PREFERENCE_PROXIMITY);
		power = (Boolean)prefs.get(PREFERENCE_POWER);
		timestamp = (Boolean)prefs.get(PREFERENCE_TIMESTAMP);
		scanInterval = (String)prefs.get(PREFERENCE_SCANINTERVAL);
		
		// Get current background scan interval (if specified)
		if (prefs.get(PREFERENCE_SCANINTERVAL) != null) {
			beaconManager.setBackgroundBetweenScanPeriod(Long.parseLong(scanInterval));
		}
		
		logToDisplay("Scanning...");
		
		// Initialise scan log
		logString = new StringBuffer();
		
		//Start scanning again.
        beaconManager.setRangeNotifier(new RangeNotifier() {
        	@Override 
        	public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        		if (beacons.size() > 0) {
        			Iterator <Beacon> beaconIterator = beacons.iterator();
        			while (beaconIterator.hasNext()) {
        				Beacon beacon = beaconIterator.next();
        				logBeaconData(beacon);
        			}
        		}
        	}
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {   
        	// TODO - OK, what now then?
        }	

	}

	/**
	 * Stop looking for beacons.
	 */
	private void stopScanning(Button scanButton) {
		try {
			beaconManager.stopRangingBeaconsInRegion(region);
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
	private void logBeaconData(Beacon beacon) {

		StringBuffer scan = new StringBuffer();
		
		if (index.booleanValue()) {
			scan.append(eventNum++ + "");
		}				
		
		if (uuid.booleanValue()) {
			scan.append(" UUID: " + beacon.getId1());
		}		
		
		if (majorMinor.booleanValue()) {
			scan.append(" Maj. Mnr.: " + beacon.getId2() + "-" + beacon.getId3());
		}
		
		if (rssi.booleanValue()) {
			scan.append(" RSSI: " + beacon.getRssi());
		}
				
		if (proximity.booleanValue()) {
			scan.append(" Proximity: " + BeaconHelper.getProximityString(beacon.getDistance()));
		}
		
		if (power.booleanValue()) {
			scan.append(" Power: "+ beacon.getTxPower());
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
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
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
