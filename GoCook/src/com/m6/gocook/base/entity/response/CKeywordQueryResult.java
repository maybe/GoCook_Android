package com.m6.gocook.base.entity.response;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.entity.IParseable;
import com.m6.gocook.base.model.ResponseData;

public class CKeywordQueryResult implements IParseable<String> {

	private int pageIndex;
	private int pageRows;
	private int totalCount;
	private List<CWareItem> rows;

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

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<CWareItem> getRows() {
		return rows;
	}

	public void setRows(List<CWareItem> rows) {
		this.rows = rows;
	}

	@Override
	public boolean parse(String value) {
		if (TextUtils.isEmpty(value)) {
			return false;
		}
		
		try {
			ResponseData responseData = new ResponseData(value);
			if (responseData.isSuccessful()) {
				JSONObject jsonObject = new JSONObject(responseData.getData());
				if (jsonObject != null) {
					pageIndex = jsonObject.optInt("PageIndex");
					pageRows = jsonObject.optInt("PageRows");
					JSONArray rowArray = jsonObject.optJSONArray("Rows");
					if (rowArray != null) {
						int size  = rowArray.length();
						rows = new ArrayList<CWareItem>();
						for (int i = 0; i < size; i++) {
							JSONObject rowObject = rowArray.optJSONObject(i);
							if (rowObject != null) {
								CWareItem item = new CWareItem();
								item.setCode(rowObject.optString("Code"));
								item.setId(rowObject.optInt("Id"));
								item.setImageUrl(rowObject.optString("ImageUrl"));
								item.setName(rowObject.optString("Name"));
								item.setNorm(rowObject.optString("Norm"));
								item.setPrice(rowObject.optDouble("Price"));
								item.setRemark(rowObject.optString("Remark"));
								item.setUnit(rowObject.optString("Unit"));
								item.setTotalCount(rowObject.optInt("TotalCount"));
								JSONArray dealMethods = rowObject.optJSONArray("DealMethod");
								if (dealMethods != null) {
									int length = dealMethods.length();
									List<String> methods = new ArrayList<String>();
									for (int j = 0; j < length; j++) {
										methods.add(dealMethods.optString(j));
									}
									item.setDealMethod(methods);
								}
								rows.add(item);
							}
						}
						
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
