package hive.apps.notebooks;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Shelf extends Activity implements OnClickListener {
	
	public LinearLayout ll;
	public LayoutParams lp;
	public static Button someButton;
	public int notebookCounter;
	public Context kontekst;
	AddNotebook dodajSveskuObjekt;
	
	public void dodajSvesku(){
		someButton = new Button(this);
		someButton.setText("Notebook"+notebookCounter);
		notebookCounter++;
		ll = (LinearLayout)findViewById(R.id.shelfId);
		lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.addView(someButton,lp);
		someButton.setText(dodajSveskuObjekt.actualNotebookName);
		someButton.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		dodajSveskuObjekt = new AddNotebook();
		notebookCounter=0;
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

	}

	// ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.shelf, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Gledanje na �ta je korisnik kliknuo u ActionBaru

		switch (item.getItemId()) {
		case R.id.action_addnotebook:
			dodajSvesku();
			Intent AddNotebook = new Intent(this, AddNotebook.class);
			startActivity(AddNotebook);
			return true;
		default:
			return false;

		}

	}

	// Static code to show second activity (Notebook Edit)
	public void gotoNotebook(View View) {
		Intent gotoNotebookInt = new Intent(this, Glavna.class);
		startActivity(gotoNotebookInt);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent gotoNotebookInt = new Intent(this, Glavna.class);
		startActivity(gotoNotebookInt);
		}
		
	}

