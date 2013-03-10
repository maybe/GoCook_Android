package com.m6.gocook.biz.main;

import android.text.TextUtils;


public class TabHelper {
	
	public enum Tab {
		
		SEARCH("search", "热门搜索"),
		HOT("hot", "最近流行"),
		SHOPPING("shopping", "购买清单"),
		ACCOUNT("account", "我的账户");
		
		public String tag;
		public String title;
		private Tab(String tag, String title) {
			this.tag = tag;
			this.title = title;
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
	public static String getActionBarTitle(String tabId) {
		if(Tab.SEARCH.equals(tabId)) {
			return Tab.SEARCH.title;
		} else if(Tab.HOT.equals(tabId)) {
			return Tab.HOT.title;
		} else if(Tab.SHOPPING.equals(tabId)) {
			return Tab.SHOPPING.title;
		} else if(Tab.ACCOUNT.equals(tabId)) {
			return Tab.ACCOUNT.title;
		} else {
			return Tab.SEARCH.title; // 默认显示热门搜索
		}
	}

}
