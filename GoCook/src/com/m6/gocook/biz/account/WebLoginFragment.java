package com.m6.gocook.biz.account;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.WebView;

import com.m6.gocook.base.fragment.BaseWebFragment;

public class WebLoginFragment extends BaseWebFragment {

	private LoginTask mLoginTask;
	
	@Override
	public boolean customShouldOverrideUrlLoading(WebView view, String url) {
		if (!TextUtils.isEmpty(url)) {
			if (mLoginTask == null) {
				mLoginTask = new LoginTask(getActivity(), url, 12);
				mLoginTask.execute((Void) null);
			}
			return true;
		}
		return super.customShouldOverrideUrlLoading(view, url);
	}
	
	private class LoginTask extends AsyncTask<Void, Void, String> {

		private Context mContext;
		private int mRnd;
		private String mUrl;
		
		public LoginTask(Context context, String url, int rnd) {
			mContext = context.getApplicationContext();
			mRnd = rnd;
			mUrl = url;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			try {
				Document doc = Jsoup.connect(mUrl).get();
				Elements elements = doc.select("input[name=tb_data]");
				String data = null;
				if (elements != null && elements.size() > 0) {
					data = elements.get(0).text();
					
				}
				return AccountModel.login(mContext, data, mRnd);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mLoginTask = null;
			
			if (!TextUtils.isEmpty(result)) {
				
			}
		}
		
		@Override
		protected void onCancelled() {
			mLoginTask = null;
		}
	}
}
