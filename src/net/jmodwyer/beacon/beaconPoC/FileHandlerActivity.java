package net.jmodwyer.beacon.beaconPoC;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.jmodwyer.ibeacon.ibeaconPoC.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Handles the listing of files and allows the user to select files for deletion and/or e-mailing.
 * @author justin
 *
 */
public class FileHandlerActivity extends Activity {
	
	private FileHelper fileHelper;
	private ArrayList<String> list;
	private static String selectedItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    
	    // Get the file helper and list files.
		BeaconScannerApp app = (BeaconScannerApp)this.getApplication();
		fileHelper = app.getFileHelper();
	    
	    setContentView(R.layout.activity_filelist);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    refreshFileList();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listfiles_activity_actions, menu);
	    return true;
	}
	
	// Handle the user selecting items from the action bar.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_deletefiles:
			// Only try and delete a file if one has been selected.
			if (selectedItem != null) { 
				// delete selected file.
				if (fileHelper.deleteFile(selectedItem)) {
					Toast.makeText(FileHandlerActivity.this, "File: " + selectedItem + " has been deleted.", Toast.LENGTH_SHORT).show();
					// refresh ListView
					refreshFileList();
				}
			}
			return true;
		case R.id.action_transferfiles:
			// Only try and delete a file if one has been selected.
			if (selectedItem != null) {
				// Have we configured a recipient address in preferences? Only try and send a mail if we have.
				HashMap<String, Object> prefs = new HashMap<String, Object>();
				SharedPreferences sharedPrefs = PreferenceManager
						.getDefaultSharedPreferences(this);
				prefs.putAll(sharedPrefs.getAll());
				String recipient = (String) prefs.get("email");
				
				// Note we're not validating the format of the email address entered by the user, we're just accepting
				// that they've entered a valid address.
				if (recipient != null && recipient.length() > 0) {
					// Who are we sending to?
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL, new String[] {recipient});
					i.putExtra(Intent.EXTRA_SUBJECT,
							"Scan details from Beacon Scanner.");
					i.putExtra(Intent.EXTRA_TEXT,
							"Your scan details are attached.");
					// Attach the file - we do this by passing a URI, which we getting from the file 
					// itself, rather than mucking around with String representations of paths.
					File attachment = fileHelper.getFile(selectedItem);
					i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
					// Reset selectedItem to clear selection
					selectedItem = null;
					try {
						startActivity(Intent.createChooser(i, "Send mail..."));
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(FileHandlerActivity.this,
								"There are no email clients installed.",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(FileHandlerActivity.this, 
							"You need to enter a Destination Email address in the Settings screen if you'd like to email scan files.", 
							Toast.LENGTH_SHORT).show();
				}
			} 
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Refresh the list of files so the current state is always displayed. 
	 * We call this when intialising the activity, and also after deleting a file.  
	 */
	private void refreshFileList() {
	    ListView listView = (ListView) findViewById(R.id.listview);
	    list = fileHelper.listFiles();
	    listView.setAdapter(new ArrayAdapter<String>(this, R.layout.file_list_item, list));
	    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
	 
	    listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		      ListView lv = (ListView) arg0;
		      TextView tv = (TextView) lv.getChildAt(arg2);
		      selectedItem = tv.getText().toString();
		   } 
		});
	}
	
}
