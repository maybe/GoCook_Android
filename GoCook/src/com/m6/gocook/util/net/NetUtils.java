package com.m6.gocook.util.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.log.Logger;

public class NetUtils {

	public static final String TAG = "NetUtils";
	
	private static final String BOUNDARY = "HttpPostGoCook";
	
	public static final String POST = "POST";
	
	public static final String GET = "GET";

	/**
	 * Check network connection status
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
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
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * Given a string representation of a URL, sets up a connection and gets an
	 * input stream.
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
			if (conn != null) {
				conn.disconnect();
			}
		}
		return stream;
	}

	/**
	 * Given a URL, establishes an HttpUrlConnection and retrieves the web page
	 * content as a InputStream, which it returns as a string.
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
			return readStream(is, len);
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
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
	 * @param in
	 *            The inputStream to read
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static String readStream(InputStream in, int len)
			throws IOException, UnsupportedEncodingException {
		if (in != null) {
			Reader reader = new InputStreamReader(in, "UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}
		return null;
	}
	
	private static String readStream(InputStream in) throws IOException {
		if(in != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			String data;
			while((data = reader.readLine()) != null) {
				stringBuilder.append(data);
			}
			return stringBuilder.toString();
		}
		return null;
	}
	
	private static void writeStream(OutputStream out, List<BasicNameValuePair> params) throws IOException {
		StringBuilder sbBuilder = new StringBuilder();
		for(NameValuePair param : params) {
			sbBuilder.append(param.getName());
			sbBuilder.append("=");
			sbBuilder.append(param.getValue());
			sbBuilder.append("&");
		}
		sbBuilder.deleteCharAt(sbBuilder.length() - 1);
		out.write(sbBuilder.toString().getBytes());
	}
	
	private static void writeStringStream(OutputStream out, List<BasicNameValuePair> params) throws IOException {
		for(NameValuePair param : params) {
			StringBuilder sbBuilder = new StringBuilder();
			sbBuilder.append("--" + BOUNDARY + "\r\n");
			sbBuilder.append(("Content-Disposition: form-data; name=\"" + param.getName() + "\"\r\n"));
			sbBuilder.append("\r\n");
			out.write(sbBuilder.toString().getBytes());
			out.write(param.getValue().getBytes());
			out.write("\r\n".getBytes());
		}
	}
	
	private static void writeFileStream(OutputStream out, File file, String paramName) throws IOException {
		if(file == null || !file.exists() || TextUtils.isEmpty(paramName)) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + file.getPath() + "\"\r\n");
		sb.append("Content-Type: application/octet-stream\r\n\r\n");
		out.write(sb.toString().getBytes());
		out.write(getBytes(file));
		out.write("\r\n".getBytes());
	}
	
	private static void writeEnd(OutputStream out) throws IOException {
		String endData = "--" + BOUNDARY + "--\r\n";
		out.write(endData.getBytes());
		out.write("\r\n".getBytes());
	}
	
	private static byte[] getBytes(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);  
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = in.read(b)) != -1) {  
            out.write(b, 0, n);  
        }  
        in.close();  
        return out.toByteArray(); 
	}
	
	private static HttpURLConnection getHttpURLConnection(String urlString, String method, String cookie) {
		if (TextUtils.isEmpty(urlString)) {
			return null;
		}
		
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.setRequestProperty("x-client-identifier", "Mobile");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cookie", cookie);
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(0);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(10000);
			return conn;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void saveCookie(Context context, HttpURLConnection conn) {
		String cookie = conn.getHeaderField("Set-Cookie");
		if(!TextUtils.isEmpty(cookie)) {
			AccountModel.saveCookie(context, cookie);
			System.out.println(cookie);
		}
	}

	public static String httpPost(Context context, String urlString, List<BasicNameValuePair> params) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, AccountModel.getCookie(context));
			conn.connect();
			OutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			writeStream(out, params);
			out.flush();
			out.close();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			saveCookie(context, conn);
			result = readStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	public static String httpPost(Context context, String urlString, List<BasicNameValuePair> params, File file, String fileParamName) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, AccountModel.getCookie(context));
			conn.connect();
			OutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			writeFileStream(out, file, fileParamName);
			writeStringStream(out, params);
			writeEnd(out);
			out.flush();
			out.close();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			saveCookie(context, conn);
			result = readStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	public static String httpGet(String urlString) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("x-client-identifier", "Mobile");
			conn.setDoInput(true);
			conn.connect();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			result = readStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	private static String getParams(List<String> params) {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}
