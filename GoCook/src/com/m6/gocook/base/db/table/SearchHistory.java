package com.m6.gocook.base.db.table;

public class SearchHistory {
	
	public static final String TABLE = "search_history_table";
	
	public static final String _ID = "_id";
	public static final String CONTENT = "content";
	
	public static final String CREATE_TABLE =
	        "CREATE TABLE " + TABLE + " ("
	        + _ID + " INTEGER PRIMARY KEY,"
	        + CONTENT + " TEXT"
	        + ");";
}
