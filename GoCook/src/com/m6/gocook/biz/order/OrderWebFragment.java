package com.m6.gocook.biz.order;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.m6.gocook.base.fragment.BaseWebFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;

public class OrderWebFragment extends BaseWebFragment {

	private CookieManager mCookieManager;
	
	private AuthTask mTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setTitle(mTitle);
		}
		
		if (mTask == null) {
			mTask = new AuthTask(getActivity());
			mTask.execute((Void) null);
		}
		showProgress(true);
	}
	
	private void loadUrl(String cookie) {
		mCookieManager = CookieManager.getInstance();
		mCookieManager.setAcceptCookie(true);
		mCookieManager.removeSessionCookie();
		if (!TextUtils.isEmpty(cookie)) {
			mCookieManager.setCookie(Protocol.URL_ROOT, cookie);
			CookieSyncManager.createInstance(getActivity());
			CookieSyncManager.getInstance().sync();
		}
		
		WebView webView = getWebView();
		
		webView.clearCache(true);
		webView.clearHistory();
		
		webView.loadUrl(mUrl);
	}
	
	public class AuthTask extends AsyncTask<Void, Void, String> {
		
		private Context mContext;
		
		public AuthTask(Context context) {
			mContext = context.getApplicationContext();
		}

		@Override
		protected String doInBackground(Void... params) {
			return OrderModel.getCookie(mContext);
		}
		
		@Override
		protected void onPostExecute(String result) {
			mTask = null;
			if (isAdded()) {
				loadUrl(result);
			}
		}
		
		@Override
		protected void onCancelled() {
			mTask = null;
		}
	}
	
}
