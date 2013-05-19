package com.m6.gocook.base.entity;

import org.json.JSONObject;

import com.m6.gocook.base.protocol.Protocol;

public class People implements IParseable<JSONObject> {

	/** 默认1为失败 */
	private int result = 1;
	
	private String name;
	private String image;
	private int fans;
	private int follows;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getFollows() {
		return follows;
	}

	public void setFollows(int follows) {
		this.follows = follows;
	}

	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}

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
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
}
