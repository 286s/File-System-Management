package com.antonio081014.filemanagement;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {
	private static final String TAG = "FMS";

	public static final String IMAGE = "Image";
	public static final String AUDIO = "Audio";
	public static final String MOVIE = "Movie";
	public static final String DOCUMENTS = "Document";
	public static final String ZIPS = "Zip";
	public static final String APKS = "Apk";

	private boolean scanned = false;
	private static final int changeImage = 0;
	private static final int menu_Refresh = 0;
	private static final int dialog_Progress = 0;
	private static final int dialog_Exit = 1;

	Button imageFiles;
	Button audioFiles;
	Button movieFiles;
	Button docsFiles;
	Button zipsFiles;
	Button apksFiles;

	TextView img;
	TextView aud;
	TextView mov;
	TextView doc;
	TextView zip;
	TextView apk;

	ProgressDialog progressDialog;
	SearchThread mThread;

	private MyAdapter myAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myAdapter = new MyAdapter(this);
		myAdapter.open();

		imageFiles = (Button) findViewById(R.id.btn_imageFiles);
		imageFiles.setBackgroundResource(R.drawable.icon_folders);
		imageFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", IMAGE);
				startActivityForResult(intent, changeImage);
			}
		});
		audioFiles = (Button) findViewById(R.id.btn_audioFiles);
		audioFiles.setBackgroundResource(R.drawable.icon_folders);
		audioFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", AUDIO);
				startActivityForResult(intent, changeImage);
			}
		});
		movieFiles = (Button) findViewById(R.id.btn_movieFiles);
		movieFiles.setBackgroundResource(R.drawable.icon_folders);
		movieFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", MOVIE);
				startActivityForResult(intent, changeImage);
			}
		});
		docsFiles = (Button) findViewById(R.id.btn_docsFiles);
		docsFiles.setBackgroundResource(R.drawable.icon_folders);
		docsFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", DOCUMENTS);
				startActivityForResult(intent, changeImage);
			}
		});
		zipsFiles = (Button) findViewById(R.id.btn_zipsFiles);
		zipsFiles.setBackgroundResource(R.drawable.icon_folders);
		zipsFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", ZIPS);
				startActivityForResult(intent, changeImage);
			}
		});
		apksFiles = (Button) findViewById(R.id.btn_apksFiles);
		apksFiles.setBackgroundResource(R.drawable.icon_folders);
		apksFiles.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, Images.class);
				intent.putExtra("NAME", APKS);
				startActivityForResult(intent, changeImage);
			}
		});

		// scanned = true;
		// readFiles();
		updateLayoutData();
	}

	// @Override
	// public void onPause() {
	// super.onPause();
	// if (!scanned && myAdapter != null)
	// myAdapter.close();
	// }
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// if (myAdapter != null && !scanned) {
	// myAdapter.close();
	// }
	// }
	//
	// @Override
	// public void onResume() {
	// super.onResume();
	// if (myAdapter == null) {
	// myAdapter = new MyAdapter(this);
	// myAdapter.open();
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, menu_Refresh, 0, R.string.menu_refresh);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case menu_Refresh:
			scanned = true;
			showDialog(dialog_Progress);
			mThread = new SearchThread();
			mThread.start();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case dialog_Progress:
			progressDialog = ProgressDialog
					.show(
							Main.this,
							"",
							"Please wait, it might take 1 min if you have too many files.",
							true, true);
			return progressDialog;
		case dialog_Exit:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to exit?").setCancelable(
					false).setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Main.this.finish();
						}
					}).setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (scanned == true) {
				scanned = false;
				mThread.interrupt();
				// mThread.stop();
				updateLayoutData();
				progressDialog.dismiss();

			} else {
				showDialog(dialog_Exit);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * Read the file/directory hierarchy;
	 * 
	 * Start with the Exteral Storage Directory;
	 * 
	 * Using DFS function to implement this.
	 */
	public void readFiles() {
		if (!scanned)
			return;
		// File storage = Environment.getRootDirectory();
		File storage = Environment.getExternalStorageDirectory();
		// Log.i(TAG, storage.getAbsolutePath());
		// Log.i(TAG, Environment.getExternalStorageState());
		// Log.i(TAG, Environment.getDataDirectory().getAbsolutePath());
		// Log.i(TAG, Environment.getExternalStoragePublicDirectory(
		// ACCOUNT_SERVICE).getAbsolutePath());
		myAdapter.clearTable();
		readSingleFile(storage);
		scanned = false;
	}

	/*
	 * @param file, The general meaning file/directory need to read;
	 * 
	 * Skip the directories starts with dot.
	 */
	public void readSingleFile(File file) {
		if (scanned == false)
			return;
		if (file == null)
			return;
		File[] files = file.listFiles();
		if (files == null)
			return;
		for (File f : files) {
			if (!scanned)
				return;
			if (f.isFile()) {
				insertFile(f);
			} else if (f.isDirectory()) {
				if (f.getName().startsWith("."))
					continue;
				readSingleFile(f);
			}
		}
	}

	/*
	 * @param file, The single file need to read;
	 * 
	 * Skip the file starts with dot.
	 */
	public void insertFile(File file) {
		String name = file.getName().toLowerCase();
		if (name.startsWith("."))
			return;
		if (name.endsWith(".jpg") || name.endsWith(".gif")
				|| name.endsWith(".png") || name.endsWith(".bmp")
				|| name.endsWith(".webp")) {
			myAdapter.insertFile("Image", file.getName(), file
					.getAbsolutePath());
		} else if (name.endsWith(".wav") || name.endsWith(".m4a")
				|| name.endsWith(".ogg") || name.endsWith(".aac")
				|| name.endsWith(".mp3") || name.endsWith(".flac")
				|| name.endsWith(".mid") || name.endsWith(".xmf")
				|| name.endsWith(".mxmf") || name.endsWith(".rtx")
				|| name.endsWith(".imy") || name.endsWith(".ota")
				|| name.endsWith(".rtttl")) {
			myAdapter.insertFile("Audio", file.getName(), file
					.getAbsolutePath());
		} else if (name.endsWith(".3gp") || name.endsWith(".mp4")) {
			myAdapter.insertFile("Movie", file.getName(), file
					.getAbsolutePath());
		}
	}

	/*
	 * Update the counts for each Type;
	 */
	private void updateLayoutData() {
		img = (TextView) findViewById(R.id.tv_images);
		img
				.setText(getResources().getText(R.string.images)
						+ " ("
						+ Integer.toString(myAdapter.fetchFile(IMAGE)
								.getCount()) + ")");
		aud = (TextView) findViewById(R.id.tv_audios);
		aud
				.setText(getResources().getText(R.string.audios)
						+ " ("
						+ Integer.toString(myAdapter.fetchFile(AUDIO)
								.getCount()) + ")");
		mov = (TextView) findViewById(R.id.tv_movies);
		mov
				.setText(getResources().getText(R.string.movies)
						+ " ("
						+ Integer.toString(myAdapter.fetchFile(MOVIE)
								.getCount()) + ")");
		doc = (TextView) findViewById(R.id.tv_docs);
		doc.setText(getResources().getText(R.string.documents) + " ("
				+ Integer.toString(myAdapter.fetchFile(DOCUMENTS).getCount())
				+ ")");
		zip = (TextView) findViewById(R.id.tv_zips);
		zip.setText(getResources().getText(R.string.zipfiles) + " ("
				+ Integer.toString(myAdapter.fetchFile(ZIPS).getCount()) + ")");
		apk = (TextView) findViewById(R.id.tv_apks);
		apk.setText(getResources().getText(R.string.apkfiles) + " ("
				+ Integer.toString(myAdapter.fetchFile(APKS).getCount()) + ")");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (myAdapter == null) {
			myAdapter = new MyAdapter(this);
			myAdapter.open();
		}
		updateLayoutData();
	}

	private class SearchThread extends Thread {

		@Override
		public void run() {
			readFiles();
			handler.sendEmptyMessage(0);
		}

		private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				updateLayoutData();
				progressDialog.dismiss();
			}
		};
	}
}