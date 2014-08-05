package net.jmodwyer.beacon.beaconPoC;

import net.jmodwyer.ibeacon.ibeaconPoC.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class BeaconPoCPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    }

}
