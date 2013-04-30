package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;


import com.m6.gocook.base.protocol.Protocol;

public class RecipeListItem implements IParseable<JSONObject> {
	
	/** 默认1为失败 */
	private int result = 1;
	private ArrayList<RecipeHotItem> hotRecipes;

	public class RecipeHotItem {
		
		private String id;
		private String name;
		private String image;
		private int collectCount;
		private String material;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
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
		public int getCollectCount() {
			return collectCount;
		}
		public void setCollectCount(int collectCount) {
			this.collectCount = collectCount;
		}
		public String getMaterial() {
			return material;
		}
		public void setMaterial(String material) {
			this.material = material;
		}
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
			
			JSONArray array = object.getJSONArray(Protocol.KEY_POPULAR_HOT_RECIPES);
			if(array != null) {
				int size = array.length();
				hotRecipes = new ArrayList<RecipeListItem.RecipeHotItem>(size);
				for(int i = 0; i < size; i++) {
					JSONObject json = array.optJSONObject(i);
					if(json != null) {
						RecipeHotItem recipe = new RecipeHotItem();
						recipe.id = json.optString(Protocol.KEY_POPULAR_HOT_RECIPE_ID);
						recipe.name = json.optString(Protocol.KEY_POPULAR_HOT_NAME);
						recipe.image = json.optString(Protocol.KEY_POPULAR_HOT_IMAGE);
						recipe.collectCount = json.optInt(Protocol.KEY_POPULAR_HOT_COLLECTION);
//						recipe.material = json.optString(Protocol.);
						hotRecipes.add(recipe);
					}
				}
				return true;
			}
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
	public ArrayList<RecipeHotItem> getHotRecipes() {
		return hotRecipes;
	}
	public void setHotRecipes(ArrayList<RecipeHotItem> hotRecipes) {
		this.hotRecipes = hotRecipes;
	}
	
	
}
