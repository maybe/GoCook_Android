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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.log.Logger;

public class NetUtils {

	public static final String TAG = "NetUtils";
	
	private static final String BOUNDARY = "---------------HttpPostGoCook";
	
	public static final String POST = "POST";
	
	public static final String GET = "GET";
	
	private static final int HTTP_READ_TIMEOUT = 180000;
	
	private static final int HTTP_CONNECT_TIMEOUT = 180000;

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
		String endData = "\r\n--" + BOUNDARY + "--\r\n";
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
			// Workaround for bug pre-Froyo, see here for more info:
			// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
			System.setProperty("http.keepAlive", "false"); 
			if (!TextUtils.isEmpty(cookie)) {
				conn.setRequestProperty("Cookie", cookie);
			}
			conn.setUseCaches(false);
			conn.setChunkedStreamingMode(0);
			conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
			conn.setReadTimeout(HTTP_READ_TIMEOUT);
			return conn;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存Cookie
	 * 
	 * @param context
	 * @param conn
	 */
	private static void saveCookie(Context context, HttpURLConnection conn) {
		Map<String, List<String>> headers = conn.getHeaderFields();
		if (headers != null) {
			List<String> cookies = headers.get("Set-Cookie");
			if (cookies != null) {
				for (String cookie : cookies) {
					if (!TextUtils.isEmpty(cookie) && cookie.contains("expires")) {
						String[] cookieArray = cookie.split(";");
						if (cookieArray != null && cookieArray.length > 0) {
							AccountModel.saveCookie(context, cookieArray[0]);
						}
					}
				}
			}
		}
	}
	
	public static String httpPost(String urlString, String data) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, null);
			conn.connect();
			OutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("data", URLEncoder.encode(data, "UTF-8")));
			writeStream(out, params);
			out.flush();
			out.close();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			result = readStream(in);
			
			for(BasicNameValuePair pair : params) {
				System.out.println(pair.getName() + ":" + pair.getValue() + "\n");
			}
			System.out.println("result : " + result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * POST请求默认带cookie
	 * 
	 * @param context
	 * @param urlString
	 * @param params
	 * @return
	 */
	public static String httpPost(Context context, String urlString, List<BasicNameValuePair> params) {
		return httpPost(context, urlString, params, AccountModel.getCookie(context));
	}
	
	/**
	 * 带cookie的POST请求
	 * 
	 * @param context
	 * @param urlString
	 * @param params
	 * @param cookie
	 * @return
	 */
	public static String httpPost(Context context, String urlString, List<BasicNameValuePair> params, String cookie) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, cookie);
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
			
//			for(BasicNameValuePair pair : params) {
//				System.out.println(pair.getName() + ":" + pair.getValue() + "\n");
//			}
			System.out.println("result : " + result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	public static String httpPost(Context context, String urlString, File file, String fileParamName) {
		return httpPost(context, urlString, file, fileParamName, AccountModel.getCookie(context));
	}
	
	public static String httpPost(Context context, String urlString, File file, String fileParamName, String cookie) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, cookie);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			conn.connect();
			OutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			writeFileStream(out, file, fileParamName);
			writeEnd(out);
			out.flush();
			out.close();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			saveCookie(context, conn);
			result = readStream(in);
//			System.out.println("result : " + result);
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
		return httpPost(context, urlString, params, file, fileParamName, AccountModel.getCookie(context));
	}
	
	public static String httpPost(Context context, String urlString, List<BasicNameValuePair> params, File file, String fileParamName, String cookie) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			conn = getHttpURLConnection(urlString, POST, cookie);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
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
//			System.out.println("file :" + file.getPath());
			System.out.println("result : " + result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
	/**
	 * 默认的GET请求不带cookie
	 * 
	 * @param urlString
	 * @return
	 */
	public static String httpGet(String urlString) {
		return NetUtils.httpGet(urlString, null);
	}
	
	/**
	 * 带cookie的GET请求
	 * 
	 * @param urlString
	 * @param cookie
	 * @return
	 */
	public static String httpGet(String urlString, String cookie) {
		String result = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
			conn.setReadTimeout(HTTP_READ_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("x-client-identifier", "Mobile");
			// HttpURLConnection reuse cause EOF Exception
			System.setProperty("http.keepAlive", "false");
			if(!TextUtils.isEmpty(cookie)) {
				conn.setRequestProperty("Cookie", cookie);			
			}
			conn.setDoInput(true);
			conn.connect();
			InputStream in = new BufferedInputStream(
					conn.getInputStream());
			result = readStream(in);
			System.out.println("result : " + result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}
	
}
