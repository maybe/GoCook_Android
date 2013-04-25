package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.m6.gocook.base.protocol.Protocol;

import android.util.Pair;

public class Popular implements IParseable<JSONObject> {
	
	/** 默认1为失败 */
	private int result = 1;
	private String topNewImg;
	private String topHotImg;
	private ArrayList<Pair<String, String[]>> recommendItems;
	
	
	@Override
	public boolean parse(JSONObject object) {
		try {
			if(object == null) {
				return false;
			}
			
			result = object.optInt(Protocol.KEY_RESULT);
			if(result != Protocol.VALUE_RESULT_OK) {
				return false;
			}
			
			topNewImg = object.optString(Protocol.KEY_POPULAR_TOPNEW_IMG);
			topHotImg = object.optString(Protocol.KEY_POPULAR_TOPHOT_IMG);
			recommendItems = new ArrayList<Pair<String, String[]>>();
			JSONArray array = object.optJSONArray(Protocol.KEY_POPULAR_RECOMMEND_ITEMS);
			if(array != null) {
				int size = array.length();
				for(int i = 0; i < size; i++) {
					JSONObject json = array.optJSONObject(i);
					String name = json.optString(Protocol.KEY_POPULAR_RECOMMEND_ITEM_NAME);
					JSONArray urls = json.optJSONArray(Protocol.KEY_POPULAR_RECOMMEND_ITEM_IMG);
					String[] imgs = null;
					if(urls != null) {
						int length = urls.length();
						imgs = new String[length];
						for(int j = 0; j < length; j++) {
							imgs[j] = urls.optString(j);
						}
					}
					recommendItems.add(new Pair<String, String[]>(name, imgs));
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getResult() {
		return result;
	}


	public void setResult(int result) {
		this.result = result;
	}


	public String getTopNewImg() {
		return topNewImg;
	}


	public void setTopNewImg(String topNewImg) {
		this.topNewImg = topNewImg;
	}


	public String getTopHotImg() {
		return topHotImg;
	}


	public void setTopHotImg(String topHotImg) {
		this.topHotImg = topHotImg;
	}


	public ArrayList<Pair<String, String[]>> getRecommendItems() {
		return recommendItems;
	}


	public void setRecommendItems(ArrayList<Pair<String, String[]>> recommendItems) {
		this.recommendItems = recommendItems;
	}

}
