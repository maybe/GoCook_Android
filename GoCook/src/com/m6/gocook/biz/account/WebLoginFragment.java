package com.m6.gocook.biz.account;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.fragment.BaseWebFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.util.net.NetUtils;

public class WebLoginFragment extends BaseWebFragment {

	private LoginTask mLoginTask;
	
	private ProgressDialog mProgressDialog;
	
	public static final String PARAM_JUMP_LOGIN = "param_jump_login";
	private boolean mJumpLogin = false;
	
	private String mUrl;
	private int mRnd;
	
	private CookieManager mCookieManager;
	
	/**
	 * 跳转到登录页面
	 * 
	 * @param context
	 */
	public static void JumpToLoginFragment(Context context) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(WebLoginFragment.PARAM_JUMP_LOGIN, true);
		Intent intent = FragmentHelper.getIntent(context, BaseActivity.class,
				WebLoginFragment.class.getName(), 
				WebLoginFragment.class.getName()
				,bundle);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mProgressDialog = new ProgressDialog(getActivity());
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			mJumpLogin = bundle.getBoolean(PARAM_JUMP_LOGIN, false);
		}
		
		Random random = new Random();
		mRnd = Math.abs(random.nextInt());
		mUrl = String.format(Protocol.URL_LOGIN_WEB, String.valueOf(mRnd));
	}
	
	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_web_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.biz_account_tab_login));
		
		WebView webView = (WebView) getView().findViewById(R.id.webview);
		
		mCookieManager = CookieManager.getInstance();
		mCookieManager.setAcceptCookie(true);
		mCookieManager.removeSessionCookie();
		
		CookieSyncManager.createInstance(getActivity());
		CookieSyncManager.getInstance().startSync();
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearCache(true);
		webView.clearHistory();
		
		webView.loadUrl(mUrl);
		webView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					if (isAdded()) {
						showProgress(false);
					}
				}
				super.onProgressChanged(view, newProgress);
			}
			
		});
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageFinished(WebView view, String url) {
				String cookie = mCookieManager.getCookie(url);
				AccountModel.saveLoginCookie(getActivity(), cookie);
				super.onPageFinished(view, url);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!TextUtils.isEmpty(url)) {
					if (mLoginTask == null) {
						if (mProgressDialog != null) {
							mProgressDialog.setMessage(getString(R.string.biz_account_login_progress));
							mProgressDialog.setCanceledOnTouchOutside(false);
							mProgressDialog.show();
						}
						mLoginTask = new LoginTask(getActivity(), url, String.valueOf(mRnd));
						mLoginTask.execute((Void) null);
					}
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		showProgress(true);
	}
	
	private class LoginTask extends AsyncTask<Void, Void, Map<String, Object>> {

		private Context mContext;
		private String mRnd;
		private String mUrl;
		
		public LoginTask(Context context, String url, String rnd) {
			mContext = context.getApplicationContext();
			mRnd = rnd;
			mUrl = url;
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			String html = NetUtils.httpGet(mUrl, AccountModel.getLoginCookie(mContext));
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("input[name=tb_data]");
			String data = null;
			if (elements != null && elements.size() > 0) {
				data = elements.get(0).val();
			}
			String result = AccountModel.login(mContext, data, mRnd);
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject json = new JSONObject(result);
					int responseCode = json.optInt(AccountModel.RETURN_RESULT);
					if (responseCode == Protocol.VALUE_RESULT_OK) {
						String icon = json.optString(AccountModel.RETURN_ICON);
						String userName = json.optString(AccountModel.RETURN_USERNAME);
						String userId =  json.optString(AccountModel.RETURN_USERID);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put(AccountModel.RETURN_ICON, icon);
						map.put(AccountModel.RETURN_USERNAME, userName);
						
						AccountModel.saveUserId(mContext, userId);
						AccountModel.saveUsername(mContext, userName);
						AccountModel.saveAvatarPath(mContext, icon);
						return map;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			mLoginTask = null;
			if (isAdded()) {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				
				if (result != null && !result.isEmpty()) {
					Toast.makeText(mContext, R.string.biz_account_login_success, Toast.LENGTH_LONG).show();
					getActivity().finish();
//					if(mJumpLogin) {
//						getActivity().getSupportFragmentManager().popBackStackImmediate();
//					} else {
//					}
				} else {
					Toast.makeText(mContext, R.string.biz_account_login_failure, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mLoginTask = null;
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
		}
	}
}
