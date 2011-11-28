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

    public Cursor fetchFileByType(String str) {
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

    /*
     * @param rowId: the corresponding id for the item in the database.
     * 
     * @param fromDatabaseOnly: if wish to delete item from database only.
     * 
     * @return if delete operation succeeded.
     */
    public boolean deleteFile(long rowId, boolean fromDatabaseOnly) {
	Cursor mCursor = mDb.query(DatabaseHelper.DATABASE_TABLE, new String[] {
		DatabaseHelper.KEY_ROWID, DatabaseHelper.KEY_PROP,
		DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_PATH },
		DatabaseHelper.KEY_ROWID + "=" + Long.toString(rowId), null,
		null, null, null);
	if (mCursor != null) {
	    mCursor.moveToFirst();
	    Log.i("FMS", Integer.toString(mCursor.getColumnCount()));
	    boolean successful = mDb.delete(DatabaseHelper.DATABASE_TABLE,
		    DatabaseHelper.KEY_ROWID + "=" + rowId, null) > 0;
	    if (!fromDatabaseOnly) {
		String path = new String(mCursor.getBlob(mCursor
			.getColumnIndex(DatabaseHelper.KEY_PATH)));
		File file = new File(path);
		successful &= file.delete();
	    }
	    successful &= mDb.delete(DatabaseHelper.DATABASE_TABLE,
		    DatabaseHelper.KEY_ROWID + "=" + rowId, null) > 0;
	    mCursor.close();
	    return successful;
	}
	return false;
    }
}
