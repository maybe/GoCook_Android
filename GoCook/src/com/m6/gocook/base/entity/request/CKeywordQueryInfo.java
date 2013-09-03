package com.m6.gocook.base.entity.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.base.model.BaseData;

public class CKeywordQueryInfo extends BaseData {

	private String keyword;
	
	private int pageIndex;
	
	private int pageRows;
	
	public CKeywordQueryInfo(String keyword, int pageIndex, int pageRows) {
		this.keyword = keyword;
		this.pageIndex = pageIndex;
		this.pageRows = pageRows;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageRows() {
		return pageRows;
	}

	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}

	@Override
	public String getJsonData() {
		try {
			JSONObject postJsonObject = new JSONObject();
			postJsonObject.put("Keyword", keyword);
			postJsonObject.put("PageIndex", pageIndex);
			postJsonObject.put("PageRows", pageRows);
			return postJsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
