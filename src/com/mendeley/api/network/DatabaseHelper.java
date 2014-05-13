package com.mendeley.api.network;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mendeleysdk.db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper instance;
	
	protected static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
		
		return instance;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		DownloadTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
		int newVersion) {
		DownloadTable.onUpgrade(database, oldVersion, newVersion);
	}
	
	protected long insert(String link) {
		ContentValues cv = new ContentValues();
		cv.put(DownloadTable.COLUMN_LINK, link);
		return getWritableDatabase().insert(DownloadTable.TABLE_NAME, null, cv);
	}
	
	protected int delete(String id) {
		return getWritableDatabase().delete(DownloadTable.TABLE_NAME, 
				DownloadTable.COLUMN_ID+"=?", 
				new String[]{id});
	}
	
	protected ArrayList<String> getAllLinks() {
		
		ArrayList<String> links = new ArrayList<String>();
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DownloadTable.TABLE_NAME);

		Cursor cursor = queryBuilder.query(getWritableDatabase(),
				new String[]{DownloadTable.COLUMN_LINK}, 
				null, null, null, null, null);
		
		if (cursor.moveToFirst()){
			   do{
				   links.add(cursor.getString(cursor.getColumnIndex(DownloadTable.COLUMN_LINK)));
			   } while(cursor.moveToNext());
			}
			cursor.close();
		
		return links;
	}
}
