package hive.apps.notebooks;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class Shelf extends Activity implements OnClickListener,
		OnLongClickListener {

	private LinearLayout ShelfHolder;
	private LayoutParams params;
	private LayoutParams sveskaParams;
	private LinearLayout polica;
	private Button sveska;
	private LinearLayout emptyspace;
	private int policaCounter;
	private int policaNaKojojSeNalazimo;
	private int sveskaCounter;
	public static int ukupniSveskaCounter;
	private Boolean popunjenePocetnePolice = false;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	public static ArrayList<Button> sveske = new ArrayList<Button>();
	public static ArrayList<String> imenaSveski = new ArrayList<String>();
	private File notebooksRoot;
	private File[] brojSveskica;
	private String foldernoIme;
	private int brojSveskiZaLoadati;
	private int identifikacija;
	private Boolean isNeededToLoad = false;
	public CitanjeXMLa citanjeXMLaObjekt;
	public String stilOdSveske;

	public static final String SHELF_STYLE = "shelfstyle";

	private String defValue = "";

	protected Object mActionMode;

	View selectednotebook;

	public void dodajPolicu() {
		emptyspace = (LinearLayout) findViewById(R.id.space);
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
			params.topMargin = 55;
			params.leftMargin = 50;
			params.rightMargin = 50;
			polica.setBackgroundResource(R.drawable.shelf_simple);
		}
		if (getShelfStyle().equals("wooden")) {
			params.topMargin = 0;
			params.leftMargin = 0;
			params.rightMargin = 0;
			polica.setPadding(50, 20, 50, 0);
			polica.setBackgroundResource(R.drawable.shelf_wooden);
			emptyspace.setBackgroundResource(R.drawable.shelf_wooden_empty);
		}

		ShelfHolder.addView(polica);
	}

	public void dodajSvesku() {

		if (sveskaCounter < 4) {
			sveska = new Button(this);
			sveska.setBackgroundResource(R.drawable.shelf_dark_green);
			sveska.setOnClickListener(this);
			sveska.setOnLongClickListener(this);
			sveska.setTextColor(getResources().getColor(R.color.dark_gray));
			sveska.setPadding(30, 0, 0, 10);
			sveska.setGravity(Gravity.BOTTOM);

			setNotebookCover("nocolor", AddNotebook.selectedcolor);

			if (isNeededToLoad) {
				File fajlic = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + foldernoIme
								+ "/notebook.xml");
				citanjeXMLaObjekt = new CitanjeXMLa(fajlic);
				sveska.setText(citanjeXMLaObjekt.getIme());
				if (citanjeXMLaObjekt.getBoja().equals("White"))
					setNotebookCover("White", 1);
				if (citanjeXMLaObjekt.getBoja().equals("Grey"))
					setNotebookCover("Grey", 2);
				if (citanjeXMLaObjekt.getBoja().equals("Blue"))
					setNotebookCover("Blue", 3);
				if (citanjeXMLaObjekt.getBoja().equals("Dark Blue"))
					setNotebookCover("Dark Blue", 4);
				if (citanjeXMLaObjekt.getBoja().equals("Purple"))
					setNotebookCover("Purple", 5);
				if (citanjeXMLaObjekt.getBoja().equals("Dark Purple"))
					setNotebookCover("Dark Purple", 6);
				if (citanjeXMLaObjekt.getBoja().equals("Green"))
					setNotebookCover("Green", 7);
				if (citanjeXMLaObjekt.getBoja().equals("Dark Green"))
					setNotebookCover("Dark Green", 8);
				if (citanjeXMLaObjekt.getBoja().equals("Orange"))
					setNotebookCover("Orange", 9);
				if (citanjeXMLaObjekt.getBoja().equals("Dark Orange"))
					setNotebookCover("Dark Orange", 10);
				if (citanjeXMLaObjekt.getBoja().equals("Red"))
					setNotebookCover("Red", 11);
				if (citanjeXMLaObjekt.getBoja().equals("Dark Red"))
					setNotebookCover("Dark Red", 12);

				isNeededToLoad = false;

			} else
				sveska.setText("" + ukupniSveskaCounter);

			sveska.setId(ukupniSveskaCounter);
			sveske.add(sveska);
			sveskaCounter++;
			ukupniSveskaCounter++;
			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			if (getShelfStyle().equals("simple")) {
			}
			if (getShelfStyle().equals("wooden")) {

			}
			sveskaParams.leftMargin = 25;
			sveskaParams.bottomMargin = 0;
			sveskaParams.topMargin = 0;
			police.get(policaNaKojojSeNalazimo).addView(
					sveske.get(ukupniSveskaCounter - 1), sveskaParams);
		} else if (sveskaCounter >= 4) {
			if (ukupniSveskaCounter >= 20)
				dodajPolicu();

			policaNaKojojSeNalazimo++;
			sveskaCounter = 0;
			dodajSvesku();
		}
	}

	private void loadajSveske() {
		brojSveskica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/").listFiles();

		for (File infile : brojSveskica) {
			if (infile.isDirectory()) {
				foldernoIme = infile.getName();
				isNeededToLoad = true;
				dodajSvesku();
			}
		}
	}

	private void obrisiSvesku() {
		String imeSveskeZaBrisanje = "";
		for (int i = 0; i < ukupniSveskaCounter; i++) {
			if (sveske.get(i).getId() == identifikacija) {
				imeSveskeZaBrisanje = sveske.get(i).getText().toString();

				Toast toast = Toast.makeText(this, "Deleting "
						+ imeSveskeZaBrisanje, Toast.LENGTH_LONG);
				toast.show();

				break;
			}
		}

		brojSveskica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/").listFiles();

		for (File infile : brojSveskica) {
			if (infile.isDirectory()
					&& infile.getName().toString().equals(imeSveskeZaBrisanje)) {
				String[] children = infile.list();
				for (int i = 0; i < children.length; i++) {
					new File(infile, children[i]).delete();
				}
				infile.delete();
			}
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

		brojSveskiZaLoadati = new File(
				Environment.getExternalStorageDirectory() + "/HIVE/Notebooks/")
				.listFiles().length;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setTitle(R.string.title_notebooks);

		
		// checkSharedPrefs();

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

	public void showToast(String message) {

		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);

		toast.show();

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Button sveskica = (Button) arg0;
		File fajlic = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/" + sveskica.getText().toString()
				+ "/notebook.xml");
		citanjeXMLaObjekt = new CitanjeXMLa(fajlic);
		stilOdSveske = citanjeXMLaObjekt.getStil();
		Glavna.stil = stilOdSveske;
		Glavna.imeSveske = citanjeXMLaObjekt.getIme();
		Intent gotoNotebookInt = new Intent(this, Glavna.class);
		startActivity(gotoNotebookInt);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inicijaliziraj();

		for (int i = 0; i < 5; i++)
			dodajPolicu();

		if (brojSveskiZaLoadati != 0)
			loadajSveske();
	}

	@Override
	public boolean onLongClick(View arg0) {

		identifikacija = arg0.getId();
		selectednotebook = arg0;
		mActionMode = this.startActionMode(mActionModeCallback);
		//arg0.setSelected(true);

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
				showToast("Edit functionality is yet to be implemented. Stay tuned :P");

				mode.finish();
			}
				return true;

			case R.id.action_deletenotebook: {
				try {
					obrisiSvesku();
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					mode.finish();
				} catch (Exception e) {
					Toast.makeText(Shelf.this,
							R.string.error_deleting_notebook,
							Toast.LENGTH_SHORT).show();
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

		if (getShelfStyle().equals("no")) {
			if (color.equals("White") || colorint == 1)
				sveska.setBackgroundResource(R.drawable.notebook_white);
			if (color.equals("Grey") || colorint == 2)
				sveska.setBackgroundResource(R.drawable.notebook_gray);
			if (color.equals("Blue") || colorint == 13)
				sveska.setBackgroundResource(R.drawable.notebook_blue);
			if (color.equals("Dark Blue") || colorint == 4)
				sveska.setBackgroundResource(R.drawable.notebook_dark_blue);
			if (color.equals("Purple") || colorint == 5)
				sveska.setBackgroundResource(R.drawable.notebook_purple);
			if (color.equals("Dark Purple") || colorint == 6)
				sveska.setBackgroundResource(R.drawable.notebook_dark_purple);
			if (color.equals("Green") || colorint == 7)
				sveska.setBackgroundResource(R.drawable.notebook_green);
			if (color.equals("Dark Green") || colorint == 8)
				sveska.setBackgroundResource(R.drawable.notebook_dark_green);
			if (color.equals("Orange") || colorint == 9)
				sveska.setBackgroundResource(R.drawable.notebook_orange);
			if (color.equals("Dark Orange") || colorint == 10)
				sveska.setBackgroundResource(R.drawable.notebook_dark_orange);
			if (color.equals("Red") || colorint == 11)
				sveska.setBackgroundResource(R.drawable.notebook_red);
			if (color.equals("Dark Red") || colorint == 12)
				sveska.setBackgroundResource(R.drawable.notebook_dark_red);
		} else {
			if (color.equals("White") || colorint == 1)
				sveska.setBackgroundResource(R.drawable.notebook_white_no_shadow);
			if (color.equals("Grey") || colorint == 2)
				sveska.setBackgroundResource(R.drawable.notebook_gray_no_shadow);
			if (color.equals("Blue") || colorint == 3)
				sveska.setBackgroundResource(R.drawable.notebook_blue_no_shadow);
			if (color.equals("Dark Blue") || colorint == 4)
				sveska.setBackgroundResource(R.drawable.notebook_dark_blue_no_shadow);
			if (color.equals("Purple") || colorint == 5)
				sveska.setBackgroundResource(R.drawable.notebook_purple_no_shadow);
			if (color.equals("Dark Purple") || colorint == 6)
				sveska.setBackgroundResource(R.drawable.notebook_dark_purple_no_shadow);
			if (color.equals("Green") || colorint == 7)
				sveska.setBackgroundResource(R.drawable.notebook_green_no_shadow);
			if (color.equals("Dark Green") || colorint == 8)
				sveska.setBackgroundResource(R.drawable.notebook_dark_green_no_shadow);
			if (color.equals("Orange") || colorint == 9)
				sveska.setBackgroundResource(R.drawable.notebook_orange_no_shadow);
			if (color.equals("Dark Orange") || colorint == 10)
				sveska.setBackgroundResource(R.drawable.notebook_dark_orange_no_shadow);
			if (color.equals("Red") || colorint == 11)
				sveska.setBackgroundResource(R.drawable.notebook_red_no_shadow);
			if (color.equals("Dark Red") || colorint == 12)
				sveska.setBackgroundResource(R.drawable.notebook_dark_red_no_shadow);
		}
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
}