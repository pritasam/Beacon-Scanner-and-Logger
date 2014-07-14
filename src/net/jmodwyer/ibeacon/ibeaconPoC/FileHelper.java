/**
 * 
 */
package net.jmodwyer.ibeacon.ibeaconPoC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Environment;
import android.text.format.DateFormat;

/**
 * @author justin
 *
 */
public class FileHelper {

	private OutputStreamWriter osw;
	private File extStorage;
	private final String FILENAME = "beacons";
	
	/**
	 * Initialise extStorage using the reference passed in.
	 * @param file
	 */
	public FileHelper(File file) {
		extStorage = file;
	}
	
    /**
     * 
     * @return OutputStreamWriter
     */
    public OutputStreamWriter prepareExternalFile() {
    	
 		try 
 		{    
 			
 			if (isExternalStorageAvailableAndWriteable()) {
 				
 				String now = (DateFormat.format("dd-MM-yyyy_HH-mm-ss", new java.util.Date()).toString());
 				File file = new File(extStorage, FILENAME + "_" + now);                                
 				FileOutputStream fos = new FileOutputStream(file);
 				osw = new OutputStreamWriter(fos);
 				               
 			}
 		} 
 		catch (IOException ioe) { 
 			ioe.printStackTrace(); 
 		}
 		
 		return osw;
 		
     }
     
    /**
     * We've finished writing the file, close the output stream writer.
     */
     public void closeExternalFile() {
     	try {
     		osw.close();
     	} 
     	catch (IOException ioe) {
     		ioe.printStackTrace();
     	}     	
     }
     
     /**
      * 
      * @param content
      * @param osw
      */
     public void writeToExternalFile(String content) {
     	try
     	{
 				osw.write(content);
 				osw.flush();
 		} 
 		catch (IOException ioe) { 
 			ioe.printStackTrace(); 
 		}
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
      *  
      * @return
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
