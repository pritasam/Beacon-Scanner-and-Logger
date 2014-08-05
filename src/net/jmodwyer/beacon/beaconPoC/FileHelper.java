/**
 * Helper class for dealing with files.
 */
package net.jmodwyer.beacon.beaconPoC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.text.format.DateFormat;

/**
 * @author justin
 */
public class FileHelper {

	private OutputStreamWriter osw;
	private File extStorage;
	private final String FILENAME_PREFIX = "beacons";

	/**
	 * Initialise extStorage using the reference passed in.
	 * @param file
	 */
	public FileHelper(File file) {
		extStorage = file;
	}

	/**
	 * Create a file using the String passed in as content.
	 * @param fileData String representing the data we want to write to a file.
	 */
	public void createFile(String fileData) {
		try 
		{    
			if (isExternalStorageAvailableAndWriteable()) {	
				String now = (DateFormat.format("dd-MM-yyyy_HH-mm-ss", new java.util.Date()).toString());
				File file = new File(extStorage, FILENAME_PREFIX + "_" + now);                                
				FileOutputStream fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos);
				osw.write(fileData);
				osw.flush();
				osw.close();
			}
		} 
		catch (IOException ioe) { 
			ioe.printStackTrace(); 
		}
	}

	/**
	 * Delete the file referenced by the path passed in.
	 * @param path String representing the path to the file.
	 * @return true if deletion was successful, false otherwise.
	 */
	public boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}

	/**
	 * Return the file referenced by the path passed in.
	 * @param path String representing the path to the file.
	 * @return the file at the location designated by the path String passed in.
	 */
	public File getFile(String path) {
		File file = new File(path);
		return file;
	}


	/**
	 * List the paths of all files in the app's external storage directory.
	 * @return ArrayList containing all discovered file paths.
	 */
	public ArrayList<String> listFiles() {
		ArrayList<String> paths = new ArrayList<String>();
		File[] fileList = extStorage.listFiles();
		for (File file : fileList) {
			if (file.isFile()) {
				paths.add(file.getPath());
			}
		}
		return paths;
	}

	/**
	 * Is external storage available and can we write to it? 
	 * @return boolean - true if storage is available and writeable, false otherwise. 
	 */
	public static boolean isExternalStorageAvailableAndWriteable() {
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			//---you can read and write the media---
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			//---you can only read the media---
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			//---you cannot read nor write the media---
			externalStorageAvailable = externalStorageWriteable = false;
		}
		return externalStorageAvailable && externalStorageWriteable;
	}
	
}
