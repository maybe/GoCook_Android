package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.recipe.RecipeModel;

public class RecipeList implements IParseable<JSONObject> {
	
	/** 默认1为失败 */
	private int result = 1;
	private ArrayList<RecipeItem> recipes;

	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public ArrayList<RecipeItem> getRecipes() {
		return recipes;
	}
	public void setRecipes(ArrayList<RecipeItem> recipes) {
		this.recipes = recipes;
	}
	
	public static class RecipeItem {
		
		private String id;
		private String name;
		private String image;
		private int collectCount;
		private String materials;
		
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
		public String getMaterials() {
			return materials;
		}
		public void setMaterial(String material) {
			this.materials = material;
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
			
			JSONArray array = object.getJSONArray(Protocol.KEY_RECIPE_LIST_RECIPES);
			if(array != null) {
				int size = array.length();
				recipes = new ArrayList<RecipeList.RecipeItem>(size);
				for(int i = 0; i < size; i++) {
					JSONObject json = array.optJSONObject(i);
					if(json != null) {
						RecipeItem recipe = new RecipeItem();
						recipe.id = json.optString(Protocol.KEY_RECIPE_LIST_RECIPE_ID);
						recipe.name = json.optString(Protocol.KEY_RECIPE_LIST_NAME);
						recipe.image = json.optString(Protocol.KEY_RECIPE_LIST_IMAGE);
						recipe.collectCount = json.optInt(Protocol.KEY_RECIPE_LIST_COLLECTION);
						recipe.materials = RecipeModel.getRecipePureMaterials(json.optString(Protocol.KEY_RECIPE_LIST_MATERIALS));
						recipes.add(recipe);
					}
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
