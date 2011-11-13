/**
 * 
 */
package com.antonio081014.filemanagement;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * @author antonio081014
 * 
 */
public class Images extends ListActivity {

	private MyAdapter myAdapter;
	private String msg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		myAdapter = new MyAdapter(this);
		myAdapter.open();
		msg = (String) getIntent().getExtras().get("NAME");
		setTitle(msg + " List");
		fillData(msg);
		registerForContextMenu(getListView());
	}

	private void fillData(String str) {
		Cursor mCursor = myAdapter.fetchFile(str);
		startManagingCursor(mCursor);

		String[] from = new String[] { DatabaseHelper.KEY_NAME,
				DatabaseHelper.KEY_PATH };
		int[] to = new int[] { R.id.rowName, R.id.rowPath };

		SimpleCursorAdapter files = new SimpleCursorAdapter(this,
				R.layout.row_list, mCursor, from, to);

		this.setListAdapter(files);
	}

	@Override
	public void onPause() {
		super.onPause();
		myAdapter.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myAdapter != null) {
			myAdapter.close();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		myAdapter = new MyAdapter(this);
		myAdapter.open();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (msg.compareTo(Main.IMAGE) == 0) {
			Cursor mCursor = myAdapter.fetchFile(id);
			String uriString = new String(mCursor.getBlob(mCursor
					.getColumnIndex(DatabaseHelper.KEY_PATH)));
			// Toast.makeText(this, uriString, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setData(Uri.parse(uriString));
		}
	}

	/*
	 * Try to solve the problem caused by the database has never been closed;
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (myAdapter != null)
				myAdapter.close();
		}
		return super.onKeyDown(keyCode, event);
	}
}
