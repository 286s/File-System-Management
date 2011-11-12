/**
 * 
 */
package com.antonio081014.filemanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author antonio081014
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "data";
	public static final String DATABASE_TABLE = "arielFilesTable";
	public static final int DATABASE_VERSION = 2;

	public static final String KEY_ROWID = "_id";
	public static final String KEY_PROP = "fileProp";
	public static final String KEY_NAME = "fileName";
	public static final String KEY_PATH = "filePath";

	/**
	 * Database creation sql statement; Table: ID; Property; Name; Path;
	 */
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_PROP
			+ " text not null, " + KEY_NAME + " text not null, " + KEY_PATH
			+ " text not null);";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	/*
	 *
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(DATABASE_CREATE);
	}

	/*
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
	}

}
