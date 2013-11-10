package com.m6.gocook.base.fragment;

import android.os.Bundle;
import android.text.TextUtils;
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
	
	private String mUrl;
	
	public static BaseWebFragment newInstance(String url, String title) {
		BaseWebFragment fragment = new BaseWebFragment();
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
		
		Bundle bundle = getArguments();
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(bundle != null ? bundle.getString(PARAM_TITLE) : "");
		
		WebView webView = (WebView) getView().findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
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
		
		showProgress(true);
	}

}
