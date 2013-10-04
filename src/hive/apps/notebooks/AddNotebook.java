package hive.apps.notebooks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddNotebook extends Activity {

	Button doneButton;
	Shelf shelfObject;
	EditText notebookName;
	TextView notebookNameDisplay;
	public static String actualNotebookName;
	public static long selectedcolor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnotebook);
		notebookName = (EditText) findViewById(R.id.notebookNameId);
		shelfObject = new Shelf();
		final ImageButton notebookcoverimg = (ImageButton) findViewById(R.id.notebookcover);
		final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);
		ArrayAdapter<CharSequence> typeadapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_styles,
						android.R.layout.simple_spinner_item);
		typeadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nbbgstyle.setAdapter(typeadapter);

		ArrayAdapter<CharSequence> coloradapter = ArrayAdapter
				.createFromResource(this, R.array.notebook_cover_color,
						android.R.layout.simple_spinner_item);
		coloradapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		notebookcovercolor.setAdapter(coloradapter);

		notebookcovercolor
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@SuppressLint("ResourceAsColor")
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Log.e("TAG",
								(String) notebookcovercolor.getSelectedItem());

						if (notebookcovercolor.getSelectedItemId() == 1) {
							notebookcoverimg.setBackgroundColor(R.color.White);

						}
						if (notebookcovercolor.getSelectedItemId() == 2) {
							notebookcoverimg.setBackgroundColor(R.color.Grey);

						}
						if (notebookcovercolor.getSelectedItemId() == 3) {
							notebookcoverimg.setBackgroundColor(R.color.Blue);
							notebookcoverimg
									.setImageResource(R.drawable.transparent);

						}
						if (notebookcovercolor.getSelectedItemId() == 4) {
							notebookcoverimg
									.setBackgroundColor(R.color.Dark_Blue);
							notebookcoverimg
									.setImageResource(R.drawable.notebook);

						}
						if (notebookcovercolor.getSelectedItemId() == 5) {
							notebookcoverimg.setBackgroundColor(R.color.Purple);

						}
						if (notebookcovercolor.getSelectedItemId() == 6) {
							notebookcoverimg
									.setBackgroundColor(R.color.Dark_Purple);

						}
						if (notebookcovercolor.getSelectedItemId() == 7) {
							notebookcoverimg.setBackgroundColor(R.color.Green);

						}
						if (notebookcovercolor.getSelectedItemId() == 8) {
							notebookcoverimg
									.setBackgroundColor(R.color.Dark_Green);

						}
						if (notebookcovercolor.getSelectedItemId() == 9) {
							notebookcoverimg.setBackgroundColor(R.color.Orange);

						}
						if (notebookcovercolor.getSelectedItemId() == 10) {
							notebookcoverimg
									.setBackgroundColor(R.color.Dark_Orange);

						}
						if (notebookcovercolor.getSelectedItemId() == 11) {
							notebookcoverimg.setBackgroundColor(R.color.Red);

						}
						if (notebookcovercolor.getSelectedItemId() == 12) {
							notebookcoverimg
									.setBackgroundColor(R.color.Dark_Red);

						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});

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
			if (actualNotebookName.length() > 8)
				actualNotebookName = actualNotebookName.substring(0, 5) + "...";

			if (actualNotebookName.length() == 0) {
				Toast toast = Toast.makeText(this,
						R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
				toast.show();
			}

			else {
				super.onBackPressed();
				final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);

				Shelf.sveske.get(Shelf.ukupniSveskaCounter - 1).setText(
						actualNotebookName);
				selectedcolor = notebookcovercolor.getSelectedItemId();

				File notebooksRoot = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + tmpann);
				if (!notebooksRoot.exists()) {
					notebooksRoot.mkdirs();
				}

				File xmlFile = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + tmpann + "/notebook.xml");

				if (!xmlFile.exists()) {
					try {
						xmlFile.createNewFile();
						xmlFile.setWritable(true);

						try {
							FileWriter out = new FileWriter(xmlFile);
							out.write("notebook:name=" + actualNotebookName
							// + " notebook:covercolor="
							// + notebookcovercolor.getSelectedItem()
									+ "/>");
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return true;
		default:
			return false;

		}

	}
}
