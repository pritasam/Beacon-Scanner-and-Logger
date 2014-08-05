package net.jmodwyer.beacon.beaconPoC;

import android.app.Activity;
import android.os.Bundle;

public class AppPreferenceActivity extends Activity {

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		  			getFragmentManager().beginTransaction().replace(android.R.id.content,
		                new BeaconPoCPreferencesFragment()).commit();
	}

}
