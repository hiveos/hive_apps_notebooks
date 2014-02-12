package hive.apps.notebooks;

import hive.apps.notebooks.helpers.HiveHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddNotebook extends Activity {

	Button doneButton;
	Button colorselect;
	Shelf shelfObject;
	EditText notebookName;
	TextView notebookNameDisplay;
	public static String actualNotebookName;
	public static long selectedcolor;

	Shelf mShelf;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnotebook);
		notebookName = (EditText) findViewById(R.id.notebookNameId);
		shelfObject = new Shelf();
		// final ImageView notebookcoverimg = (ImageView)
		// findViewById(R.id.notebookcover);
		final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);
		final Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setLogo(R.drawable.ic_navigation_cancel);
		// actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setHomeAsUpIndicator(R.drawable.action_arrow);

		ArrayAdapter<CharSequence> typeadapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_styles,
						android.R.layout.simple_spinner_item);
		typeadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nbbgstyle.setAdapter(typeadapter);

		nbbgstyle
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						if (nbbgstyle.getSelectedItemId() == 0) {

						}
						if (nbbgstyle.getSelectedItemId() == 1) {

						}
						if (nbbgstyle.getSelectedItemId() == 2) {

						}
						if (nbbgstyle.getSelectedItemId() == 3) {

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		ArrayAdapter<CharSequence> coloradapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_cover_color,
						android.R.layout.simple_spinner_item);
		coloradapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		notebookcovercolor.setAdapter(coloradapter);

		notebookcovercolor
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Log.e("TAG",
								(String) notebookcovercolor.getSelectedItem());

						if (notebookcovercolor.getSelectedItemId() == 1) {

						}
						if (notebookcovercolor.getSelectedItemId() == 2) {

						}
						if (notebookcovercolor.getSelectedItemId() == 3) {

						}
						if (notebookcovercolor.getSelectedItemId() == 4) {

						}
						if (notebookcovercolor.getSelectedItemId() == 5) {

						}
						if (notebookcovercolor.getSelectedItemId() == 6) {

						}
						if (notebookcovercolor.getSelectedItemId() == 7) {

						}
						if (notebookcovercolor.getSelectedItemId() == 8) {

						}
						if (notebookcovercolor.getSelectedItemId() == 9) {

						}
						if (notebookcovercolor.getSelectedItemId() == 10) {

						}
						if (notebookcovercolor.getSelectedItemId() == 11) {

						}
						if (notebookcovercolor.getSelectedItemId() == 12) {

						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});

		mShelf = new Shelf();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addnotebook, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_savenotebook:

			String tmpann;
			actualNotebookName = notebookName.getText().toString();
			tmpann = actualNotebookName;

			if (actualNotebookName.length() == 0) {
				Toast toast = Toast.makeText(this,
						R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
				toast.show();
			}
			if (actualNotebookName.length() > 40) {
				Toast toast = Toast.makeText(this,
						R.string.error_notebook_name_too_long,
						Toast.LENGTH_LONG);
				toast.show();
			}

			else {
				super.onBackPressed();
				final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);
				final Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);

				selectedcolor = notebookcovercolor.getSelectedItemId();

				File notebooksRoot = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + tmpann);
				if (!notebooksRoot.exists()) {
					notebooksRoot.mkdirs();
				}

				String newnotebookname = actualNotebookName;
				String newnotebookstyle = nbbgstyle.getSelectedItem()
						.toString();
				String newnotebookcolor = notebookcovercolor.getSelectedItem()
						.toString();

				new AddTask().execute(newnotebookname, newnotebookstyle,
						newnotebookcolor);
			}

			return true;
		default:
			return false;

		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private class AddTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_add_notebook);

			if (isNetworkAvailable()) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							3);

					String name = params[0];
					String style = params[1];
					String color = params[2];

					nameValuePairs.add(new BasicNameValuePair("name", name
							.toString()));
					nameValuePairs.add(new BasicNameValuePair("style", style
							.toString()));
					nameValuePairs.add(new BasicNameValuePair("color", color
							.toString()));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);

					//call refresh
					
					finish();

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
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (isNetworkAvailable()) {

			}

		}

	}
}