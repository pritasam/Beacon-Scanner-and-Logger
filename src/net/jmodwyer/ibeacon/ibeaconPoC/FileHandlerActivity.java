package net.jmodwyer.ibeacon.ibeaconPoC;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileHandlerActivity extends Activity {
	
	private FileHelper fileHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    
	    // Get the file helper and list files.
		BeaconScannerApp app = (BeaconScannerApp)this.getApplication();
		fileHelper = app.getFileHelper();
	    fileHelper.listFiles();
	    
	    setContentView(R.layout.activity_filelist);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    ListView listview = (ListView) findViewById(R.id.listview);
	    ArrayList<String> list = fileHelper.listFiles();

	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);
	    
	}
	
}
