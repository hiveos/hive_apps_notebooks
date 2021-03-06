package hive.apps.notebooks;

import hive.apps.notebooks.helpers.HiveHelper;

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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug.FlagToString;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditNotebook extends Activity {

	EditText mNotebookName;
	Spinner mNotebookStyle;
	Spinner mNotebookColor;
	String id, name, style, color;

	Shelf mShelf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editnotebook);

		Intent i = getIntent();
		id = i.getStringExtra("id");
		name = i.getStringExtra("name");
		style = i.getStringExtra("style");
		color = i.getStringExtra("color");

		mNotebookName = (EditText) findViewById(R.id.edit_notebook_name);
		mNotebookStyle = (Spinner) findViewById(R.id.edit_notebook_style);
		mNotebookColor = (Spinner) findViewById(R.id.edit_notebook_color);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setLogo(R.drawable.ic_navigation_cancel);
		actionBar.setHomeAsUpIndicator(R.drawable.action_arrow);
		actionBar.setTitle(R.string.cancel);

		ArrayAdapter<CharSequence> typeadapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_styles,
						android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> coloradapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_cover_color,
						android.R.layout.simple_spinner_item);

		typeadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mNotebookStyle.setAdapter(typeadapter);

		coloradapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mNotebookColor.setAdapter(coloradapter);

		mNotebookName.setText(name);
		mNotebookStyle.setSelection(getStyleInt(style));
		mNotebookColor.setSelection(getColorInt(color));

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

			if (mNotebookName.getText().toString().length() == 0) {
				Toast toast = Toast.makeText(this,
						R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
				toast.show();
			}
			if (mNotebookName.getText().toString().length() > 40) {
				Toast toast = Toast.makeText(this,
						R.string.error_notebook_name_too_long,
						Toast.LENGTH_LONG);
				toast.show();
			}

			else {
				super.onBackPressed();

				String name = mNotebookName.getText().toString();
				String style = mNotebookStyle.getSelectedItem().toString();
				String color = mNotebookColor.getSelectedItem().toString();

				new EditTask().execute(name, style, color);

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

	public int getStyleInt(String style) {
		int x = 0;
		if (style.equals("Lines")) {
			x = 0;
		}
		if (style.equals("Grid")) {
			x = 1;
		}
		if (style.equals("Plain")) {
			x = 2;
		}
		return x;
	}

	public int getColorInt(String color) {
		int x = 0;
		if (color.equals("White")) {
			x = 0;
		}
		if (color.equals("Gray")) {
			x = 1;
		}
		if (color.equals("Blue")) {
			x = 2;
		}
		if (color.equals("Dark Blue")) {
			x = 3;
		}
		if (color.equals("Purple")) {
			x = 4;
		}
		if (color.equals("Dark Purple")) {
			x = 5;
		}
		if (color.equals("Green")) {
			x = 6;
		}
		if (color.equals("Dark Green")) {
			x = 7;
		}
		if (color.equals("Orange")) {
			x = 8;
		}
		if (color.equals("Dark Orange")) {
			x = 9;
		}
		if (color.equals("Red")) {
			x = 10;
		}
		if (color.equals("Dark Red")) {
			x = 11;
		}
		return x;
	}

	private class EditTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_edit_notebook);

			if (isNetworkAvailable()) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							3);

					String name = params[0];
					String style = params[1];
					String color = params[2];

					nameValuePairs.add(new BasicNameValuePair("item", id
							.toString()));
					nameValuePairs.add(new BasicNameValuePair("name", name
							.toString()));
					nameValuePairs.add(new BasicNameValuePair("style", style
							.toString()));
					nameValuePairs.add(new BasicNameValuePair("color", color
							.toString()));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);

					// call refresh

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
			Intent i = new Intent(getApplicationContext(), Shelf.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}
	}

}
