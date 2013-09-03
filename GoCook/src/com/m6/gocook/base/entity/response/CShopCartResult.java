package com.m6.gocook.base.entity.response;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.entity.IParseable;
import com.m6.gocook.base.protocol.Protocol;

public class CShopCartResult extends BaseResponse implements IParseable<String>{

	private String orderId;

	
	public CShopCartResult(String value) {
		parse(value);
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public boolean parse(String value) {
		if (TextUtils.isEmpty(value)) {
			return false;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(value);
			if (jsonObject != null) {
				errorCode = jsonObject.optInt(BaseResponse.ERROR);
				result = jsonObject.optInt(BaseResponse.RESULT);
				if (result == Protocol.VALUE_RESULT_OK) {
					orderId = jsonObject.optString("order_id");
				}
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
}
