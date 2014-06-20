package net.jmodwyer.ibeacon.ibeaconPoC;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class IBeaconPoCPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    }

}
