package com.m6.gocook.base.db.table;

public class RecipePurchaseList {
	
	public static final String TABLE = "recipe_purchase_list_table";
	
	public static final String _ID = "_id";
	public static final String RECIPE_ID = "recipe_id";
	public static final String RECIPE_NAME = "recipe_name";
	
	public static final String CREATE_TABLE =
	        "CREATE TABLE " + TABLE + " ("
	        + _ID + " INTEGER PRIMARY KEY,"
	        + RECIPE_ID + " INTEGER,"
	        + RECIPE_NAME + " TEXT"
	        + ");";
}
