/**
 * 
 */
package com.antonio081014.filemanagement;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * @author antonio081014
 * 
 */
public class Images extends ListActivity {

	private MyAdapter myAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		myAdapter = new MyAdapter(this);
		myAdapter.open();
		fillData(Main.IMAGE);
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
}
