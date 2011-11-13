/**
 * 
 */
package com.antonio081014.filemanagement;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author antonio081014
 * 
 */
public class MyAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mctrx;

	public MyAdapter(Context ctx) {
		this.mctrx = ctx;
	}

	public MyAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mctrx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public boolean clearTable() {
		return mDb.delete(DatabaseHelper.DATABASE_TABLE, null, null) > 0;
	}

	public void close() {
		// mDb.close();
		mDbHelper.close();
	}

	public long insertFile(String prop, String name, String path) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(DatabaseHelper.KEY_PROP, prop);
		initialValues.put(DatabaseHelper.KEY_NAME, name);
		initialValues.put(DatabaseHelper.KEY_PATH, path);

		return mDb.insert(DatabaseHelper.DATABASE_TABLE, null, initialValues);
	}

	public Cursor fetchAllFiles() {

		return mDb.query(DatabaseHelper.DATABASE_TABLE, new String[] {
				DatabaseHelper.KEY_ROWID, DatabaseHelper.KEY_PROP,
				DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_PATH }, null, null,
				null, null, null);
	}

	public Cursor fetchFile(String str) {
		Cursor mCursor = mDb.query(true, DatabaseHelper.DATABASE_TABLE,
				new String[] { DatabaseHelper.KEY_ROWID,
						DatabaseHelper.KEY_PROP, DatabaseHelper.KEY_NAME,
						DatabaseHelper.KEY_PATH }, DatabaseHelper.KEY_PROP
						+ "=\"" + str + "\"", null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public Cursor fetchFile(long id) {
		Cursor mCursor = mDb.query(true, DatabaseHelper.DATABASE_TABLE,
				new String[] { DatabaseHelper.KEY_ROWID,
						DatabaseHelper.KEY_PROP, DatabaseHelper.KEY_NAME,
						DatabaseHelper.KEY_PATH }, DatabaseHelper.KEY_ROWID
						+ "=" + id + "", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean deleteFile(long rowId) {
		Cursor mCursor = mDb.query(DatabaseHelper.DATABASE_TABLE, new String[] {
				DatabaseHelper.KEY_ROWID, DatabaseHelper.KEY_PROP,
				DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_PATH },
				DatabaseHelper.KEY_ROWID + "=" + Long.toString(rowId), null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			Log.i("FMS", Integer.toString(mCursor.getColumnCount()));
			String path = new String(mCursor.getBlob(mCursor
					.getColumnIndex(DatabaseHelper.KEY_PATH)));
			File file = new File(path);
			boolean successful = file.delete();
			successful &= mDb.delete(DatabaseHelper.DATABASE_TABLE,
					DatabaseHelper.KEY_ROWID + "=" + rowId, null) > 0;
			return successful;
		}

		return false;
	}
}
