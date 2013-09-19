package hive.apps.notebooks;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Shelf extends Activity {

	public LinearLayout ll;
	public LayoutParams lp;
	public static Button someButton;
	public int notebookCounter;
	public Context kontekst;
	AddNotebook dodajSveskuObjekt;

	public void dodajSvesku() {

		/*
		 * View LastChild = null; LinearLayout ShelfHolder = (LinearLayout)
		 * findViewById(R.id.ShelfHolder); LastChild =
		 * ShelfHolder.getChildAt(ShelfHolder.getChildCount());
		 * 
		 * // Checks whether there are any rows already present. If none is
		 * present // then creates one and puts the new notebook in it. if
		 * (ShelfHolder.getChildCount() == 0) {
		 * 
		 * LinearLayout newShelf = new LinearLayout(this); Log.e("add", "Set");
		 * ShelfHolder.addView(newShelf, new LayoutParams(
		 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); Log.e("add",
		 * "Add"); newShelf.setBackgroundResource(R.drawable.shelf);
		 * Log.e("add", "Set drawable");
		 * 
		 * ImageView newNotebook = new ImageView(this); Log.e("add",
		 * "Set imageview"); ((ViewGroup) LastChild).addView(newNotebook);
		 * Log.e("add", "Added View");
		 * 
		 * newNotebook
		 * .setBackgroundResource(R.drawable.empty_notebook_cover_blue_skewed);
		 * Log.e("add", "Set Drawable");
		 * 
		 * 
		 * }
		 * 
		 * // Again checks for present rows/shelfs. In case there are some it //
		 * proceeds with finding the last child item (last shelf) and checking
		 * // for number of items in it. If the number is less than 4 adds the
		 * new // notebook to the last child. If the last row if full it creates
		 * a new // shelf to put the notebook in. else if
		 * (ShelfHolder.getChildCount() > 0) {
		 * 
		 * if (((ViewGroup) LastChild).getChildCount() >= 4) {
		 * 
		 * LinearLayout newShelf = new LinearLayout(this);
		 * ShelfHolder.addView(newShelf, new LayoutParams(
		 * LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		 * newShelf.setBackgroundResource(R.drawable.shelf);
		 * 
		 * ImageView newNotebook = new ImageView(this); ((ViewGroup)
		 * LastChild).addView(newNotebook); newNotebook
		 * .setBackgroundResource(R.drawable.empty_notebook_cover_blue_skewed);
		 * 
		 * } else if (((ViewGroup) LastChild).getChildCount() < 4) {
		 * 
		 * ImageView newNotebook = new ImageView(this); ((ViewGroup)
		 * LastChild).addView(newNotebook); newNotebook
		 * .setBackgroundResource(R.drawable.empty_notebook_cover_blue_skewed);
		 * 
		 * } }
		 * 
		 * /* someButton.setText("Notebook" + notebookCounter);
		 * notebookCounter++; ll = (LinearLayout) findViewById(R.id.shelfId); lp
		 * = new LayoutParams(LayoutParams.MATCH_PARENT,
		 * LayoutParams.WRAP_CONTENT); ll.addView(someButton, lp);
		 * someButton.setText(dodajSveskuObjekt.actualNotebookName);
		 * someButton.setOnClickListener(this);
		 */
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		dodajSveskuObjekt = new AddNotebook();
		notebookCounter = 0;

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		Button GotoSampleNotebook = (Button) findViewById(R.id.sample_button_go);
		GotoSampleNotebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoNotebook(v);
			}
		});
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
			dodajSvesku();
			Intent AddNotebook = new Intent(this, AddNotebook.class);
			startActivity(AddNotebook);
			return true;
		default:
			return false;

		}

	}

	public void gotoNotebook(View View) {
		Intent gotoNotebookInt = new Intent(this, Glavna.class);
		startActivity(gotoNotebookInt);
	}
	

}
