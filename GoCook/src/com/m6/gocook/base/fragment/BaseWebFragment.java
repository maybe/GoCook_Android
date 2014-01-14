package com.m6.gocook.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.m6.gocook.R;
import com.m6.gocook.base.view.ActionBar;


public class BaseWebFragment extends BaseFragment {
	
	private static final String PARAM_URL = "param_url";
	private static final String PARAM_TITLE = "param_title";
	
	protected String mUrl;
	protected String mTitle;
	
	private WebView mWebView;
	
	public static BaseWebFragment newInstance(Context context, String fragmentName, String url, String title) {
		BaseWebFragment fragment = (BaseWebFragment) Fragment.instantiate(context, fragmentName);
		Bundle bundle = new Bundle();
		bundle.putString(PARAM_URL, url);
		bundle.putString(PARAM_TITLE, title);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			mUrl = bundle.getString(PARAM_URL);
			mTitle = bundle.getString(PARAM_TITLE);
		}
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
		actionBar.setTitle(mTitle);
		
		mWebView = (WebView) getView().findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setWebChromeClient(new WebChromeClient() {
			
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
		
		mWebView.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageFinished(WebView view, String url) {
				mWebView.requestFocus();
				super.onPageFinished(view, url);
			}
		});
		
		showProgress(true);
	}

	public WebView getWebView() {
		return mWebView;
	}
}
