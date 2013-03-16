package com.m6.gocook.base.view;

import com.m6.gocook.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchListView extends FrameLayout {

	private ListView mSearchHistoryView;
	private ListView mSearchResultView;
	private TextView mEmptyOrErrorText;
	private ImageView mEmptyImage;
	
	public SearchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SearchListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Context context = getContext();
		
		mSearchHistoryView = (ListView) LayoutInflater.from(context).inflate(R.layout.base_history_listview, null);
		mSearchResultView = (ListView) LayoutInflater.from(context).inflate(R.layout.base_search_listview, null);
		mEmptyOrErrorText = new TextView(context);
		
		mEmptyImage = new ImageView(context);
		mEmptyImage.setImageDrawable(null);
		mEmptyImage.setVisibility(GONE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		
		addView(mSearchHistoryView, params);
		addView(mSearchResultView, params);
		
		FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		
		params1.gravity = Gravity.CENTER_HORIZONTAL;
		params1.setMargins(10, 10, 10, 10);
		addView(mEmptyOrErrorText, params1);
		
		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
		        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		
		params2.gravity = Gravity.CENTER_HORIZONTAL;
		params2.setMargins(10, 120, 10, 10);
		addView(mEmptyImage, params2);

		mEmptyOrErrorText.setTextSize(16);
		mEmptyOrErrorText.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.GONE);
	}
	
	/**
	 * 搜索建议或者搜索历史
	 * @param adapter
	 */
	public void setHistoryOrSuggestAdapter(BaseAdapter adapter){
		mEmptyOrErrorText.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.GONE);
		mSearchResultView.setVisibility(View.GONE);
		mSearchHistoryView.setVisibility(View.VISIBLE);
		mSearchHistoryView.setAdapter(adapter);
	}
	
	/**
	 * 设置搜索结果listview
	 * @param adapter
	 */
	public void setSearchResultAdapter(BaseAdapter adapter){
		mEmptyOrErrorText.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.GONE);
		mSearchHistoryView.setVisibility(View.GONE);
		mSearchResultView.setVisibility(View.VISIBLE);
		mSearchResultView.setAdapter(adapter);
	}

	/**
	 * 搜索结果为空
	 * @param keyword
	 */
	public void setNoResult4Search(String keyword) {
		mSearchHistoryView.setVisibility(View.GONE);
		mSearchResultView.setVisibility(View.GONE);
		mEmptyOrErrorText.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.VISIBLE);
	}

	/**
	 * 网络异常
	 */
	public void setNetworkInvalid() {
		mSearchHistoryView.setVisibility(View.GONE);
		mSearchResultView.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.GONE);
		mEmptyOrErrorText.setVisibility(View.VISIBLE);
		mEmptyOrErrorText.setText(R.string.biz_search_error_tip);
	}

	/**
	 * 搜索结果是否为空
	 * @return
	 */
	public boolean isSearchAdapterNULL() {
		return null == mSearchResultView.getAdapter() || mSearchResultView.getAdapter().isEmpty();
	}

	/**
	 * 显示搜索结果list
	 */
	public void showSearchResultView() {
		mSearchHistoryView.setVisibility(View.GONE);
		mSearchResultView.setVisibility(View.VISIBLE);
		mEmptyOrErrorText.setVisibility(View.GONE);
		mEmptyImage.setVisibility(View.GONE);
	}

	/**
	 * 获取搜索结果listview
	 */
	public ListView getSearchResultListView() {
		return mSearchResultView;
	}
	
	/**
	 * 获取搜索历史listview
	 */
	public ListView getSearchHistoryListView() {
		return mSearchHistoryView;
	}
	
}
