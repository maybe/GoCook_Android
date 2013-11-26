package com.m6.gocook.base.entity.response;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.entity.IParseable;
import com.m6.gocook.base.protocol.Protocol;

public class CKeywordQueryResult extends BaseResponse implements IParseable<String> {

	private int result;
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
			JSONObject jsonObject = new JSONObject(value);
			if (jsonObject != null) {
				result = jsonObject.optInt(BaseResponse.RESULT);
				errorCode = jsonObject.optInt(BaseResponse.ERROR);
				if (result == Protocol.VALUE_RESULT_OK) {
					pageIndex = jsonObject.optInt("page");
					pageRows = jsonObject.optInt("total_count");
					JSONArray rowArray = jsonObject.optJSONArray("wares");
					if (rowArray != null) {
						int size  = rowArray.length();
						rows = new ArrayList<CWareItem>();
						for (int i = 0; i < size; i++) {
							JSONObject rowObject = rowArray.optJSONObject(i);
							if (rowObject != null) {
								CWareItem item = new CWareItem();
								item.setCode(rowObject.optString("code"));
								item.setId(rowObject.optInt("id"));
								item.setImageUrl(rowObject.optString("image_url"));
								item.setName(rowObject.optString("name"));
								item.setNorm(rowObject.optString("norm"));
								item.setPrice(rowObject.optDouble("price"));
								item.setRemark(rowObject.optString("remark"));
								item.setUnit(rowObject.optString("unit"));
								JSONArray dealMethods = rowObject.optJSONArray("deal_method");
								if (dealMethods != null) {
									int length = dealMethods.length();
									ArrayList<String> methods = new ArrayList<String>();
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
