package com.m6.gocook.base.entity.request;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.base.model.BaseData;

public class CShopcartInfo extends BaseData {
	
	private List<CShopcartWareInfo> wares;
	
	public CShopcartInfo(List<CShopcartWareInfo> wares) {
		this.wares = wares;
	}
	
	@Override
	public String getJsonData() {
		try {
			JSONArray wareArray = new JSONArray();
			for (CShopcartWareInfo ware : wares) {
				wareArray.put(ware.getJsonObject());
			}
			JSONObject postJsonObject = new JSONObject();
			postJsonObject.put("Wares", wareArray.toString());
			return postJsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class CShopcartWareInfo {
		private int wareId; //商品编号
		private double quantity; //商品数量
		private String remark; //加工说明
		
		public CShopcartWareInfo(int wareId, double quantity, String remark) {
			this.wareId = wareId;
			this.quantity = quantity;
			this.remark = remark;
		}
		
		public JSONObject getJsonObject() {
			try {
				JSONObject postJsonObject = new JSONObject();
				postJsonObject.put("WareId", wareId);
				postJsonObject.put("Quantity", quantity);
				postJsonObject.put("Remark", remark);
				return postJsonObject;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
