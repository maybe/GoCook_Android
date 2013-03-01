package com.m6.gocook.biz.main;

import android.text.TextUtils;


public class TabHelper {
	
	public enum Tab {
		
		SEARCH("search"),
		HOT("hot"),
		SHOPPING("shopping"),
		ACCOUNT("account");
		
		private String tag;
		private String title;
		private Tab(String tag) {
			this.tag = tag;
		}
		
		@Override
		public String toString() {
			return this.tag;
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
	
	public static String getActionBarTitle(String tabId) {
		if(Tab.SEARCH.equals(tabId)) {
			return Tab.SEARCH.toString();
		} else if(Tab.HOT.equals(tabId)) {
			return Tab.HOT.toString();
		} else if(Tab.SHOPPING.equals(tabId)) {
			return Tab.SHOPPING.toString();
		} else if(Tab.ACCOUNT.equals(tabId)) {
			return Tab.ACCOUNT.toString();
		} else {
			return Tab.SEARCH.toString(); // 默认显示热门搜索
		}
	}

}
