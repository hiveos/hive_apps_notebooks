package hive.apps.notebooks;

import hive.apps.notebooks.helpers.HiveHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Shelf extends Activity implements OnClickListener,
		OnLongClickListener, OnRefreshListener {

	private LinearLayout ShelfHolder;
	private LayoutParams params;
	private LayoutParams sveskaParams;
	private LayoutParams sveskaTitleParams;
	private LinearLayout polica;
	private RelativeLayout sveska;
	private ImageView sveskaCover;
	private TextView sveskaTitle;
	private int policaCounter;
	private int policaNaKojojSeNalazimo;
	private int sveskaCounter;
	public static int ukupniSveskaCounter;
	private Boolean popunjenePocetnePolice = false;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	public static ArrayList<RelativeLayout> sveske = new ArrayList<RelativeLayout>();
	public static ArrayList<String> imenaSveski = new ArrayList<String>();
	private File notebooksRoot;
	private File[] brojSveskica;
	private String foldernoIme;
	private int brojSveskiZaLoadati;
	private int identifikacija;
	private Boolean isNeededToLoad = false;
	public CitanjeXMLa citanjeXMLaObjekt;
	public String stilOdSveske;
	String upLoadServerUri = "";
	private int serverResponseCode = 0;
	private String putDoFajlaZaUploadati = "";
	int id_first;
	int id_notebook;

	ArrayList<String> fileNamesWithExtentions = new ArrayList<String>();

	String[] mNotebooks;
	ArrayList<String> mNotebookIds = new ArrayList<String>();
	ArrayList<String> mNotebookNames = new ArrayList<String>();
	ArrayList<String> mNotebookStyles = new ArrayList<String>();
	ArrayList<String> mNotebookColors = new ArrayList<String>();
	ArrayList<Integer> iNotebookIds = new ArrayList<Integer>();

	public static final String SHELF_STYLE = "shelfstyle";

	private String defValue = "";

	protected Object mActionMode;

	View selectednotebook;

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);

		getWindow().setWindowAnimations(0);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setTitle(R.string.title_notebooks);
		actionBar.setIcon(null);
		actionBar.setDisplayUseLogoEnabled(false);

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		new FetchTask().execute();
		mPullToRefreshLayout.setRefreshing(true);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// new uploadNotebooks().execute();
		super.onStop();
	}

	@Override
	protected void onPause() {
		uN();
		new uploadFileA().execute();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isNetworkAvailable()) {
			Intent mNoNetworkIntent = new Intent();
			mNoNetworkIntent.setAction("hive.action.General");
			mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
			sendBroadcast(mNoNetworkIntent);
			finish();
		}

	}

	@Override
	public void onRefreshStarted(View view) {
		new FetchTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.shelf, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_addnotebook:
			Intent AddNotebook = new Intent(this, AddNotebook.class);
			startActivity(AddNotebook);
			return true;
		case R.id.action_settings:
			Intent Settings = new Intent(this, SettingsActivity.class);
			startActivity(Settings);
			return true;
		default:
			return false;

		}

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editnotebook, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_deletenotebook:

			return true;

		case R.id.action_editnotebooks:
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	private void inicijaliziraj() {
		ShelfHolder = (LinearLayout) findViewById(R.id.ShelfHolder);
		policaCounter = 0;
		sveskaCounter = 0;
		policaNaKojojSeNalazimo = 0;
		ukupniSveskaCounter = 0;
		sveske.clear();
		police.clear();
		ShelfHolder.removeAllViews();

		notebooksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/");
		if (!notebooksRoot.exists()) {
			notebooksRoot.mkdirs();
		}

		brojSveskiZaLoadati = mNotebooks.length;
	}

	public void dodajPolicu() {
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		police.add(polica);
		policaCounter++;
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		polica.setLayoutParams(params);
		polica.setGravity(Gravity.CENTER_HORIZONTAL);

		if (getShelfStyle().equals("no")) {
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
		}
		if (getShelfStyle().equals("simple")) {
			// Common values can go here
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
			polica.setBackgroundResource(R.drawable.shelf_simple);

			// Specific values or adjustments go inside this if statement
			if (isTablet(this)) {
				polica.setPadding(0, 0, 0, 67);
			} else {
				polica.setPadding(0, 0, 0, 70);
			}

		}
		if (getShelfStyle().equals("wooden")) {
			// Other common values can go here
			params.topMargin = 0;
			params.leftMargin = 0;
			params.rightMargin = 0;
			polica.setPadding(50, 20, 50, 0);
			polica.setBackgroundResource(R.drawable.shelf_wooden);

			// Specific values or adjustments go inside this if statement
			if (isTablet(this)) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT, 215);
				polica.setLayoutParams(params);
			} else {

			}

		}

		ShelfHolder.addView(polica);
	}

	@SuppressLint("ResourceAsColor")
	public void dodajSvesku(int position) {

		if (sveskaCounter < 4) {
			sveskaCover = new ImageView(this);
			sveskaTitle = new TextView(this);

			sveska = new RelativeLayout(this);
			sveska.setBackgroundResource(R.drawable.notebook_bg);
			sveska.setGravity(Gravity.BOTTOM);

			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			if (isTablet(this)) {
				sveskaTitleParams = new LayoutParams(192,
						LayoutParams.WRAP_CONTENT);
			} else {
				sveskaTitleParams = new LayoutParams(192,
						LayoutParams.WRAP_CONTENT);
			}

			sveskaParams.leftMargin = 25;
			sveskaParams.bottomMargin = 0;
			sveskaParams.topMargin = 0;

			sveska.setOnClickListener(this);
			sveska.setOnLongClickListener(this);

			sveska.setId(ukupniSveskaCounter);
			sveske.add(sveska);
			sveskaCounter++;
			ukupniSveskaCounter++;

			sveskaTitle.setBackgroundResource(R.drawable.notebook_title_bg);
			sveskaTitle.setPadding(10, 5, 10, 20);
			sveskaTitle.setTextAppearance(this, R.style.NotebookTitleStyle);
			sveskaTitle.setLayoutParams(sveskaTitleParams);

			sveska.addView(sveskaCover);
			sveska.addView(sveskaTitle);

			setNotebookCover("nocolor", AddNotebook.selectedcolor);

			if (isNeededToLoad) {
				sveskaTitle.setText(mNotebookNames.get(position));

				if (mNotebookColors.get(position).equals("White"))
					setNotebookCover("White", 1);
				if (mNotebookColors.get(position).equals("Gray"))
					setNotebookCover("Gray", 2);
				if (mNotebookColors.get(position).equals("Blue"))
					setNotebookCover("Blue", 3);
				if (mNotebookColors.get(position).equals("Dark Blue"))
					setNotebookCover("Dark Blue", 4);
				if (mNotebookColors.get(position).equals("Purple"))
					setNotebookCover("Purple", 5);
				if (mNotebookColors.get(position).equals("Dark Purple"))
					setNotebookCover("Dark Purple", 6);
				if (mNotebookColors.get(position).equals("Green"))
					setNotebookCover("Green", 7);
				if (mNotebookColors.get(position).equals("Dark Green"))
					setNotebookCover("Dark Green", 8);
				if (mNotebookColors.get(position).equals("Orange"))
					setNotebookCover("Orange", 9);
				if (mNotebookColors.get(position).equals("Dark Orange"))
					setNotebookCover("Dark Orange", 10);
				if (mNotebookColors.get(position).equals("Red"))
					setNotebookCover("Red", 11);
				if (mNotebookColors.get(position).equals("Dark Red"))
					setNotebookCover("Dark Red", 12);

				isNeededToLoad = false;

			} else
				sveskaTitle.setText("" + ukupniSveskaCounter);

			police.get(policaNaKojojSeNalazimo).addView(
					sveske.get(ukupniSveskaCounter - 1), sveskaParams);

		} else if (sveskaCounter >= 4) {
			if (ukupniSveskaCounter >= 20)
				dodajPolicu();

			policaNaKojojSeNalazimo++;
			sveskaCounter = 0;
			dodajSvesku(position);
		}
	}

	private void loadajSveske() {
		for (int i = 0; i < mNotebooks.length; i++) {
			isNeededToLoad = true;
			dodajSvesku(i);
		}
	}

	private void obrisiSvesku(int id) {
		File notebooktodelete = null;

		for (int i = 0; i < ukupniSveskaCounter; i++) {
			if (sveske.get(i).getId() == identifikacija) {
				notebooktodelete = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/"
								+ fileNamesWithExtentions.get(id));
				break;
			}
		}

		deleteDir(notebooktodelete);

	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	public void showToast(String message) {

		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);

		toast.show();

	}

	@Override
	public void onClick(View arg0) {
		stilOdSveske = mNotebookStyles.get(arg0.getId());
		Glavna.stil = stilOdSveske;
		Glavna.imeSveske = mNotebookNames.get(arg0.getId());
		File fajlOMG = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/" + mNotebookNames.get(arg0.getId()));
		if (!fajlOMG.exists()) {
			new downloadTask().execute(arg0.getId() + "");
		} else {
			Intent gotoNotebookInt = new Intent(getApplicationContext(),
					Glavna.class);
			startActivity(gotoNotebookInt);
		}
	}

	@Override
	public boolean onLongClick(View arg0) {

		identifikacija = arg0.getId();
		selectednotebook = arg0;
		mActionMode = this.startActionMode(mActionModeCallback);
		// arg0.setSelected(true);

		return true;
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.editnotebook, menu);
			selectednotebook.setSelected(true);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_editnotebooks: {
				Intent i = new Intent(getApplication(), EditNotebook.class);
				i.putExtra("id", mNotebookIds.get(selectednotebook.getId()));
				i.putExtra("name", mNotebookNames.get(selectednotebook.getId()));
				i.putExtra("style",
						mNotebookStyles.get(selectednotebook.getId()));
				i.putExtra("color",
						mNotebookColors.get(selectednotebook.getId()));
				startActivity(i);

				mode.finish();
			}
				return true;

			case R.id.action_deletenotebook: {
				try {
					Log.d("HHH", iNotebookIds.get(selectednotebook.getId())
							+ " | " + selectednotebook.getId());
					new DeleteTask().execute(selectednotebook.getId());
//					obrisiSvesku(selectednotebook.getId());
					mode.finish();
				} catch (Exception e) {
					Toast.makeText(Shelf.this,
							R.string.error_deleting_notebook,
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					mode.finish();

				}
			}
			default:
				return false;
			}
		}

		public void onDestroyActionMode(ActionMode mode) {
			selectednotebook.setSelected(false);
			mActionMode = null;
		}
	};

	public void checkSharedPrefs() {
		if (getShelfStyle().equals("no")) {
			Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
		}
		if (getShelfStyle().equals("simple")) {
			Toast.makeText(this, "simple", Toast.LENGTH_SHORT).show();
		}
		if (getShelfStyle().equals("wooden")) {
			Toast.makeText(this, "wooden", Toast.LENGTH_SHORT).show();
		}
	}

	public String getShelfStyle() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		return settings.getString(SHELF_STYLE, "");
	}

	public void setNotebookCover(String color, long colorint) {
		if (color.equals("White") || colorint == 1)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_white);
		if (color.equals("Grey") || colorint == 2)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_gray);
		if (color.equals("Blue") || colorint == 13)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_blue);
		if (color.equals("Dark Blue") || colorint == 4)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_dark_blue);
		if (color.equals("Purple") || colorint == 5)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_purple);
		if (color.equals("Dark Purple") || colorint == 6)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_dark_purple);
		if (color.equals("Green") || colorint == 7)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_green);
		if (color.equals("Dark Green") || colorint == 8)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_dark_green);
		if (color.equals("Orange") || colorint == 9)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_orange);
		if (color.equals("Dark Orange") || colorint == 10)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_dark_orange);
		if (color.equals("Red") || colorint == 11)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_red);
		if (color.equals("Dark Red") || colorint == 12)
			sveskaCover.setBackgroundResource(R.drawable.xnotebook_dark_red);
	}

	public int intShelfStyle() {
		int temp = 0;
		if (getShelfStyle().equals("no")) {
			temp = 0;
		}
		if (getShelfStyle().equals("simple")) {
			temp = 1;
		}
		if (getShelfStyle().equals("wooden")) {
			temp = 2;
		}
		return temp;
	}

	public void refreshShelf() {
		new FetchTask().execute();
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public class FetchTask extends AsyncTask<String, Integer, String> {

		String response;
		boolean isEmpty = true;

		ArrayList<String> sNotebookIds = new ArrayList<String>();

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_list_notebook);

			if (isNetworkAvailable()) {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse responseGet;
					responseGet = client.execute(get);
					HttpEntity resEntityGet = responseGet.getEntity();

					if (resEntityGet != null) {
						response = EntityUtils.toString(resEntityGet);
					} else {

					}

					extractData();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Intent mNoNetworkIntent = new Intent();
				mNoNetworkIntent.setAction("hive.action.General");
				mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
				sendBroadcast(mNoNetworkIntent);
				finish();
			}

			return null;
		}

		private void extractData() {
			clearUp();

			if (!response.equals("")) {
				Log.d("TAG", response);

				mNotebooks = response.split(";");

				for (int i = 0; i < mNotebooks.length; i++) {
					iNotebookIds.add(Integer.parseInt(mNotebooks[i].substring(
							mNotebooks[i].indexOf("id=") + 3,
							mNotebooks[i].indexOf(",name"))));
					mNotebookIds.add(mNotebooks[i].substring(
							mNotebooks[i].indexOf("id=") + 3,
							mNotebooks[i].indexOf(",name")));
					mNotebookNames.add(mNotebooks[i].substring(
							mNotebooks[i].indexOf("name=") + 5,
							mNotebooks[i].indexOf(",style")));
					mNotebookStyles.add(mNotebooks[i].substring(
							mNotebooks[i].indexOf("style=") + 6,
							mNotebooks[i].indexOf(",color")));
					mNotebookColors.add(mNotebooks[i].substring(mNotebooks[i]
							.indexOf("color=") + 6));

					isEmpty = false;
				}
			}
		}

		private void clearUp() {
			Shelf.this.runOnUiThread(new Runnable() {
				public void run() {
					LinearLayout NoNotebook = (LinearLayout) findViewById(R.id.no_notebook);
					NoNotebook.setVisibility(View.GONE);
				}
			});
			clearArrays();
		}

		private void clearArrays() {
			mNotebookNames.clear();
			mNotebookStyles.clear();
			mNotebookColors.clear();
			iNotebookIds.clear();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (isNetworkAvailable()) {
				if (!isEmpty) {
					addNotebooks();
				} else {
					displayNoNotebooks();
				}
			}
			mPullToRefreshLayout.setRefreshComplete();
		}

		private void addNotebooks() {
			inicijaliziraj();

			for (int i = 0; i < 5; i++)
				dodajPolicu();

			if (brojSveskiZaLoadati != 0)
				loadajSveske();
		}

		private void displayNoNotebooks() {
			Shelf.this.runOnUiThread(new Runnable() {
				public void run() {
					LinearLayout NoNotebook = (LinearLayout) findViewById(R.id.no_notebook);
					NoNotebook.setVisibility(View.VISIBLE);
				}
			});
		}

	}

	private class DeleteTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_delete_notebook);

			if (isNetworkAvailable()) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					int id = params[0];
					nameValuePairs.add(new BasicNameValuePair("item",
							mNotebookIds.get(id)));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
					new FetchTask().execute();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Intent mNoNetworkIntent = new Intent();
				mNoNetworkIntent.setAction("hive.action.General");
				mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
				sendBroadcast(mNoNetworkIntent);
				finish();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (isNetworkAvailable()) {
				new FetchTask().execute();
			}

		}

	}

	public void uN() {
		File notebooksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/");
		File zNotebooksRoot = new File(
				Environment.getExternalStorageDirectory()
						+ "/HIVE/Zipped_Notebooks/");
		if (!notebooksRoot.exists())
			notebooksRoot.mkdirs();
		if (!zNotebooksRoot.exists())
			zNotebooksRoot.mkdirs();

		File[] notebookFolders = notebooksRoot.listFiles();

		for (File f : notebookFolders) {
			Zipper zipper = new Zipper();
			File zipFile = new File(Environment.getExternalStorageDirectory()
					+ "/HIVE/Zipped_Notebooks/" + f.getName() + ".zip");
			Log.d("ZIPFILE:", zipFile.getAbsolutePath() + "");
			try {
				zipFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				zipper.zipDirectory(f, zipFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void uF() {
		File zippedNotebooksRoot = new File(
				Environment.getExternalStorageDirectory()
						+ "/HIVE/Zipped_Notebooks/");
		File[] zippedNotebooks = zippedNotebooksRoot.listFiles();

		ArrayList<String> mNotebooksToUpload = new ArrayList<String>();
		uploadFileA authTask = new uploadFileA();
		HiveHelper mHiveHelper = new HiveHelper();

		for (File zf : zippedNotebooks) {
			upLoadServerUri = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_push_notebook);
			int i = 0;
			for (String name : mNotebookNames) {
				upLoadServerUri = getResources().getString(R.string.api_base)
						+ mHiveHelper.getUniqueId()
						+ getResources().getString(R.string.api_push_notebook);
				if (zf.getName().substring(0, zf.getName().length() - 4)
						.equals(name)) {
					upLoadServerUri += "/" + mNotebookIds.get(i);
					Log.d("upLoadServerUri", upLoadServerUri);
					uploadFile(zf.getAbsolutePath());
				}
				i++;
			}

		}
	}

	public int uploadFile(String sourceFileUri) {
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {
			return 0;
		} else {
			try {
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);
				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
						+ fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					runOnUiThread(new Runnable() {
						public void run() {
							Log.d("File uploading status:", "Completed");
						}
					});
				}
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {
				ex.printStackTrace();
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			return serverResponseCode;
		}
	}

	public class uploadFileA extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d("status", "usli smo");
			uF();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.d("status", "uspjeli smo");
			super.onPostExecute(result);
		}

	}

	public class downloadTask extends AsyncTask<String, Void, File> {

		@Override
		protected File doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d("status", "usli smo");
			Log.d("ID::", arg0[0]);
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_pull_notebook);
			File downloadedNotebook = null;
			id_first = Integer.parseInt(arg0[0]);

			File tempDir = new File(Environment.getExternalStorageDirectory()
					+ "/HIVE/Temp");

			try {
				HttpRequest request = HttpRequest.post(url).send(
						"item=" + mNotebookIds.get(id_first));
				Log.d("aaaa", url + " " + arg0[0]);
				if (request.ok()) {
					downloadedNotebook = new File(
							Environment.getExternalStorageDirectory()
									+ "/HIVE/Temp/"
									+ mNotebookNames.get(id_first) + ".zip");
					if (!tempDir.exists())
						tempDir.mkdirs();
					request.receive(downloadedNotebook);
				}
			} catch (Exception e) {

			}

			return downloadedNotebook;
		}

		@Override
		protected void onPostExecute(File file) {
			// TODO Auto-generated method stub
			Log.d("status", "uspjeli smo");
			super.onPostExecute(file);
			File extractTo = new File(Environment.getExternalStorageDirectory()
					+ "/HIVE/Notebooks/" + mNotebookNames.get(id_first));
			if (!extractTo.exists()) {
				extractTo.mkdirs();
			}
			try {
				Zipper.unzip(file, extractTo);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Shelf.this.runOnUiThread(new Runnable() {
				public void run() {
					Intent gotoNotebookInt = new Intent(
							getApplicationContext(), Glavna.class);
					startActivity(gotoNotebookInt);
				}
			});

		}

	}

}