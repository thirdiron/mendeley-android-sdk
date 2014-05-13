package com.mendeley.api.network;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class BufferContentProvider extends ContentProvider {
 
	private DatabaseHelper database;

	private static final int PAGES = 10;
	private static final int PAGE_ID = 20;

	private static final String AUTHORITY = "com.mendeley.mendelyapi.network";

	private static final String BASE_PATH = "pages";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE =      ContentResolver.CURSOR_DIR_BASE_TYPE + "/pages";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/pages";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, PAGES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PAGE_ID);
	}

	public static Context context;
	
	@Override
	public boolean onCreate() {
		
		context = getContext();
		database = DatabaseHelper.getInstance(context);
		return false;
	}

	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DownloadTable.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
			case PAGES:
				break;
			case PAGE_ID:
				queryBuilder.appendWhere(DownloadTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		Log.e("", "--- " +uri);
		Log.e("", "+++ " + database);
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
//		int rowsDeleted = 0;
		long id = 0;
		switch (uriType) {
			case PAGES:
				id = sqlDB.insert(DownloadTable.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
			case PAGES:
				rowsDeleted = sqlDB.delete(DownloadTable.TABLE_NAME, selection, selectionArgs);
				break;
			case PAGE_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = sqlDB.delete(DownloadTable.TABLE_NAME,
							DownloadTable.COLUMN_ID + "=" + id, null);
				} else {
					rowsDeleted = sqlDB.delete(DownloadTable.TABLE_NAME,
							DownloadTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
			case PAGES:
				rowsUpdated = sqlDB.update(DownloadTable.TABLE_NAME, values, selection, selectionArgs);
				break;
			case PAGE_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(DownloadTable.TABLE_NAME, values, 
							DownloadTable.COLUMN_ID + "=" + id, null);
				} else {
					rowsUpdated = sqlDB.update(DownloadTable.TABLE_NAME, values,
							DownloadTable.COLUMN_ID + "=" + id 
							+ " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}


} 