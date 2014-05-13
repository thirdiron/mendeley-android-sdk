package com.mendeley.api.network;

import android.database.sqlite.SQLiteDatabase;

public class DownloadTable {

	  public static final String TABLE_NAME = "download_buffer";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_MENDELEY_ID = "mendeley_id";
	  public static final String COLUMN_LINK = "link";
	  public static final String COLUMN_DATE = "date";
	  public static final String COLUMN_STATUS = "status";

	  private static final String DATABASE_CREATE = "create table " 
	      + TABLE_NAME
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_MENDELEY_ID + " text null, " 
	      + COLUMN_LINK + " text null," 
	      + COLUMN_DATE + " text null," 
	      + COLUMN_STATUS + " text null"
	      + ");";

	  public static void onCreate(SQLiteDatabase database) {
		  database.execSQL(DATABASE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
		  database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		  onCreate(database);
	  }
}
