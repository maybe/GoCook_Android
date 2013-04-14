package com.m6.gocook.base.entity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeEntity implements IParseable<JSONObject> {
	
	private class Material {
		private String name;
		private String remark;
		
		public Material(String name, String remark) {
			this.name = name;
			this.remark = remark;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
	}
	
	private class Procedure {
		private String desc;
		private String imageURL;
		
		public Procedure(String desc, String imageURL) {
			this.desc = desc;
			this.imageURL = imageURL;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getImageURL() {
			return imageURL;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}
	}

	private int id;
	
	private String name;
	
	private String coverImgURL;
	
	private String desc;
	
	private String author;
	
	private int dishCount;
	
	private int collectCount;
	
	private ArrayList<Material> materials;
	
	private ArrayList<Procedure> procedures;
	
	private String tip;
	
	private int commentCount;
	
	private String commentStrs;


	@Override
	public boolean parse(JSONObject object) {
		
		// Temporary Fake Data
		this.name = "鱼香肉丝";
		this.desc = "";

		
		
		return true;
	}
	
	
	
	
}
