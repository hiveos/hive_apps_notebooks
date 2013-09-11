package hive.apps.notebooks;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddNotebook extends Activity {

	Button doneButton;
	Shelf shelfObject;
	EditText notebookName;
	TextView notebookNameDisplay;
	public String actualNotebookName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnotebook);
		notebookName = (EditText) findViewById(R.id.notebookNameId);
		notebookNameDisplay = (TextView) findViewById(R.id.notebook_name_on_cover);
		shelfObject = new Shelf();

		notebookName.addTextChangedListener(textWatcher);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.notebook_styles,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nbbgstyle.setAdapter(adapter);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (notebookName.getText().length() <= 11) {
				notebookNameDisplay.setText(notebookName.getText());
			}

			if (notebookName.getText().length() > 11) {
				String namecontainer;
				namecontainer = notebookName.getText().toString();
				notebookNameDisplay.setText(namecontainer.substring(0, 11)
						+ "...");

			}

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addnotebook, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_savenotebook:

			if (notebookName.getText().toString() == null) {
				Toast toast = Toast.makeText(this,
						R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
				toast.show();
			} else {

				super.onBackPressed();
				Shelf.someButton.setText(actualNotebookName);
				Shelf.someButton.setId(shelfObject.notebookCounter);
			}
			return true;
		default:
			return false;

		}

	}

}
