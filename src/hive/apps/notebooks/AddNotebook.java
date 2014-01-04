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
		//actionBar.setDisplayShowTitleEnabled(false);
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
//			if (actualNotebookName.length() > 10)
//				actualNotebookName = actualNotebookName.substring(0, 5) + "...";

			if (actualNotebookName.length() == 0) {
				Toast toast = Toast.makeText(this,
						R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
				toast.show();
			}

			else {
				super.onBackPressed();
				final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);
				final Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);
//				Shelf.sveske.get(Shelf.ukupniSveskaCounter - 1).setText(
//						actualNotebookName);
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
							out.write("<notebook>\n <name>"
									+ actualNotebookName + "</name>\n"
									+ "<covercolor>"
									+ notebookcovercolor.getSelectedItem()
									+ "</covercolor>\n" + "<style>"
									+ nbbgstyle.getSelectedItem()
									+ "</style>\n" + "</notebook>");
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