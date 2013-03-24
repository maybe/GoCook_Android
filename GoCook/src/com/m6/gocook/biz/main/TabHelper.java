package com.m6.gocook.biz.main;

import com.m6.gocook.R;

import android.content.Context;
import android.text.TextUtils;


public class TabHelper {
	
	public enum Tab {
		
		SEARCH("search"),
		HOT("hot"),
		SHOPPING("shopping"),
		ACCOUNT("account");
		
		public String tag;
		private Tab(String tag) {
			this.tag = tag;
		}
		
		public boolean equals(String object) {
			if(TextUtils.isEmpty(object) || TextUtils.isEmpty(tag)) {
				return false;
			}
			if(tag.equals(object)) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * 获取主页actionbar title，当参数为null时，默认取第一个tab的名称
	 * 
	 * @param tabId
	 * @return
	 */
	public static String getActionBarTitle(Context context, String tabId) {
		if(Tab.SEARCH.equals(tabId)) {
			return context.getString(R.string.biz_main_tab_search);
		} else if(Tab.HOT.equals(tabId)) {
			return context.getString(R.string.biz_main_tab_hot);
		} else if(Tab.SHOPPING.equals(tabId)) {
			return context.getString(R.string.biz_main_tab_shopping);
		} else if(Tab.ACCOUNT.equals(tabId)) {
			return context.getString(R.string.biz_main_tab_account);
		} else {
			return context.getString(R.string.biz_main_tab_search); // 默认显示热门搜索
		}
	}

}
