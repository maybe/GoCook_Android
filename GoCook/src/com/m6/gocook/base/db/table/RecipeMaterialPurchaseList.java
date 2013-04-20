package com.m6.gocook.base.db.table;

public class RecipeMaterialPurchaseList {

	public static final String TABLE = "recipe_material_purchase_list_table";
	
	public static final String _ID = "_id";
	public static final String RECIPE_ID = "recipe_id";
	public static final String MATERIAL_NAME = "material_name";
	public static final String MATERIAL_REMARK = "material_remark";
	public static final String IS_BOUGHT = "is_bought";
	
	public static final String CREATE_TABLE =
	        "CREATE TABLE " + TABLE + " ("
	        + _ID + " INTEGER PRIMARY KEY,"
	        + RECIPE_ID + " INTEGER,"
	        + MATERIAL_NAME + " TEXT,"
	        + MATERIAL_REMARK + " TEXT,"
	        + IS_BOUGHT + " INTEGER"
	        + ");";
}
