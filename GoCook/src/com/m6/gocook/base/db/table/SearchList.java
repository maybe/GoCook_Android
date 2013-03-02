package com.m6.gocook.base.db.table;

public class SearchList {

	public static final String TABLE = "search_list_table";
	
	public static final String _ID = "_id";
	public static final String NAME = "name";
	public static final String TREND = "trend";
	public static final String CATEGORY = "category";
	
	public static final String CREATE_TABLE =
	        "CREATE TABLE " + SearchList.TABLE + " ("
	        + _ID + " INTEGER PRIMARY KEY,"
	        + NAME + " TEXT,"
	        + TABLE + " TEXT,"
	        + TREND + " TEXT,"
	        + CATEGORY + " TEXT"
	        + ");";
}
