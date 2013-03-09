package com.m6.gocook.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.m6.gocook.util.log.Logger;

public class NetUtils {
	
	public static final String TAG = "NetUtils";
	
	/**
	 * Check network connection status
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
	    ConnectivityManager connMgr = (ConnectivityManager) context.
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	} 
	
	/**
	 * Check wifi connection status
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check mobile network connection status
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Given a string representation of a URL, sets up a connection and gets
	 * an input stream.
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
    public static InputStream downloadUrlToStream(String urlString) {
    	HttpURLConnection conn = null;
    	InputStream stream = null;
    	try {
    		URL url = new URL(urlString);
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000 /* milliseconds */);
    		conn.setConnectTimeout(15000 /* milliseconds */);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);
    		// Starts the query
    		conn.connect();
    		int response = conn.getResponseCode();
    		Logger.d(TAG, "The response is: " + response);
    		stream = conn.getInputStream();
    	} catch (final IOException e) {
            Logger.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
    		if(conn != null) {
    			conn.disconnect();
    		}
    	}
        return stream;
    }
    
    /**
	 * Given a URL, establishes an HttpUrlConnection and retrieves 
	 * the web page content as a InputStream, which it returns as
	 * a string.
	 *  
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static String downloadUrlToString(String urlString) {
		InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	    try {
	        is = downloadUrlToStream(urlString);
	        // Convert the InputStream into a string
	        return readIt(is, len);
	    // Makes sure that the InputStream is closed after the app is finished using it.
	    } catch (final IOException e) {
            Logger.e(TAG, "Error in downloadBitmap - " + e);
        } finally {
        	try {
        		if (is != null) {
        			is.close();
        		} 
        	} catch (final IOException e) {
			}
	    }
		return null;
	}
	
	/**
	 * Reads an InputStream and converts it to a String.
	 * 
	 * @param stream The inputStream to read
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		if(stream != null) {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");        
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}
		return null;
	}
}
