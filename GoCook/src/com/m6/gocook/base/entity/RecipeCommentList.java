package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.recipe.RecipeModel;


public class RecipeCommentList implements IParseable<JSONObject> {

	private ArrayList<RecipeCommentItem> comments;
	
	@Override
	public boolean parse(JSONObject object) {
		
		if(object == null) {
			return false;
		}
		
		try {
			
			if(object.optInt(Protocol.KEY_RESULT) != Protocol.VALUE_RESULT_OK) {
				return false;
			}
			
			JSONArray array = object.getJSONArray(Protocol.KEY_RECIPE_COMMENT);
			int size = array.length();
			comments = new ArrayList<RecipeCommentItem>(size);
			if(comments == null) {
				return false;
			}
			for(int i = 0; i < size; i++) {
				JSONObject json = array.optJSONObject(i);
				if(json != null) {
					RecipeCommentItem comment = new RecipeCommentItem();
					comment.userId = json.optString(Protocol.KEY_RECIPE_COMMENT_USER_ID);
					comment.name = json.optString(Protocol.KEY_RECIPE_COMMENT_NAME);
					comment.portrait = json.optString(Protocol.KEY_RECIPE_COMMENT_PORTRAIT);
					comment.content = json.optString(Protocol.KEY_RECIPE_COMMENT_CONTENT);
					comment.createTime = json.optString(Protocol.KEY_RECIPE_COMMENT_CREATE_TIME);
					comments.add(comment);
				}
			}
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public class RecipeCommentItem {
		private String userId;
		private String name;
		private String portrait;
		private String content;
		private String createTime;
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPortrait() {
			return portrait;
		}
		public void setPortrait(String portrait) {
			this.portrait = portrait;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		
		
	}

}
