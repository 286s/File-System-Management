/**
 * 
 */
package com.antonio081014.filemanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

/**
 * @author antonio081014
 * 
 */
public class Images extends Activity {

	private MyAdapter myAdapter;
	private String msg;
	private ListView listview;

	private int[] show_ID;
	private String[] show_Name;
	private String[] show_Path;

	private static final int dialog_delete = 0;
	// private static final int dialog_share = 1;

	private int current_position = -1;

	BaseAdapter myBaseAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LinearLayout all = new LinearLayout(Images.this);
			all.setOrientation(LinearLayout.HORIZONTAL);

			ImageView imgV = new ImageView(Images.this);
			if (msg.compareTo(Main.IMAGE) == 0) {
				// Bitmap myBitmap =
				// BitmapFactory.decodeFile(show_Path[position]);
				// if (myBitmap != null)
				// imgV.setImageBitmap(myBitmap);
				// else
				imgV.setBackgroundResource(R.drawable.icon_image);

			} else if (msg.compareTo(Main.AUDIO) == 0) {
				imgV.setBackgroundResource(R.drawable.icon_audio);
			} else if (msg.compareTo(Main.MOVIE) == 0)
				imgV.setBackgroundResource(R.drawable.icon_movie);
			else if (msg.compareTo(Main.DOCUMENTS) == 0)
				imgV.setBackgroundResource(R.drawable.icon_docs);
			else if (msg.compareTo(Main.ZIPS) == 0)
				imgV.setBackgroundResource(R.drawable.icon_zips);
			else if (msg.compareTo(Main.APKS) == 0)
				imgV.setBackgroundResource(R.drawable.icon_apks);
			else
				imgV.setBackgroundResource(R.drawable.ic_launcher);

			imgV.setScaleType(ScaleType.CENTER);

			imgV.setAdjustViewBounds(true);
			imgV.setMaxHeight(30);
			imgV.setMaxWidth(23);
			imgV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			all.addView(imgV);

			LinearLayout sub = new LinearLayout(Images.this);
			sub.setOrientation(LinearLayout.VERTICAL);

			TextView tv_name = new TextView(Images.this);
			tv_name.setText(show_Name[position]);
			tv_name.setTextSize(18);
			tv_name.setTextColor(Color.MAGENTA);
			tv_name.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView tv_path = new TextView(Images.this);
			tv_path.setText(show_Path[position]);
			tv_path.setTextSize(10);
			tv_path.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			sub.addView(tv_name);
			sub.addView(tv_path);

			if (position % 2 == 0)
				all.setBackgroundColor(Color.BLACK);
			else
				all.setBackgroundColor(Color.WHITE);
			all.addView(sub);
			// all.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
			// LayoutParams.WRAP_CONTENT));
			return all;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (show_Name != null)
				return show_Name.length;
			return 0;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		myAdapter = new MyAdapter(this);
		msg = (String) getIntent().getExtras().get("NAME");
		setTitle(msg + " List");
		listview = (ListView) findViewById(R.id.list_display);
		listview.setAdapter(myBaseAdapter);
		fillData(msg);
		registerForContextMenu(listview);

		// Preview the file selected;
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				current_position = position;
			}
		});

		// listview.setOnItemLongClickListener(new OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View view,
		// int position, long id) {
		// current_position = position;
		// return true;
		// }
		// });
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.contextMenu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.single_file_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		current_position = info.position;
		Toast.makeText(getApplicationContext(), show_Name[current_position],
				Toast.LENGTH_LONG).show();

		switch (item.getItemId()) {
		case R.id.menu_delete:
			showDialog(dialog_delete);
			return true;
		case R.id.menu_share:
			share();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void share() {
		if (msg.compareTo(Main.IMAGE) == 0) {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri = Uri.parse(show_Path[current_position]);

			sharingIntent.setType("image/*");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			startActivity(Intent.createChooser(sharingIntent,
					"Share image using"));
		} else if (msg.compareTo(Main.AUDIO) == 0) {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri = Uri.parse(show_Path[current_position]);

			sharingIntent.setType("audio/*");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			startActivity(Intent.createChooser(sharingIntent,
					"Share audio using"));
		} else if (msg.compareTo(Main.MOVIE) == 0) {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri = Uri.parse(show_Path[current_position]);

			sharingIntent.setType("video/*");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			startActivity(Intent.createChooser(sharingIntent,
					"Share movie using"));
		} else {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			Uri screenshotUri = Uri.parse(show_Path[current_position]);

			sharingIntent.setType("application/*");
			sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			startActivity(Intent.createChooser(sharingIntent,
					"Share files using"));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case dialog_delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure to delete this file?")
					.setCancelable(false).setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									myAdapter.deleteFile(
											show_ID[current_position], true);
									current_position = -1;
									fillData(msg);
								}
							}).setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onResume() {
		myAdapter = new MyAdapter(this);
		// myAdapter.open();
		fillData(msg);
		myBaseAdapter.notifyDataSetChanged();
		super.onResume();
	}

	private void fillData(String str) {
		myAdapter.open();
		Cursor mCursor = myAdapter.fetchFile(str);

		int idIndex = mCursor.getColumnIndex(DatabaseHelper.KEY_ROWID);
		int nameIndex = mCursor.getColumnIndex(DatabaseHelper.KEY_NAME);
		int pathIndex = mCursor.getColumnIndex(DatabaseHelper.KEY_PATH);
		show_ID = new int[mCursor.getCount()];
		show_Name = new String[mCursor.getCount()];
		show_Path = new String[mCursor.getCount()];

		int i = 0;
		for (mCursor.moveToFirst(); !(mCursor.isAfterLast()); mCursor
				.moveToNext()) {
			show_ID[i] = mCursor.getInt(idIndex);
			show_Name[i] = mCursor.getString(nameIndex);
			show_Path[i] = mCursor.getString(pathIndex);
			i++;
		}
		mCursor.close();
		myAdapter.close();
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
}
