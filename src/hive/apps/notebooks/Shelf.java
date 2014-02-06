package hive.apps.notebooks;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Shelf extends Activity implements OnClickListener,
		OnLongClickListener {

	private LinearLayout ShelfHolder;
	private LayoutParams params;
	private LayoutParams sveskaParams;
	private LayoutParams sveskaTitleParams;
	private LinearLayout polica;
	private RelativeLayout sveska;
	private ImageView sveskaCover;
	private TextView sveskaTitle;
	private LinearLayout emptyspace;
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

	ArrayList<String> fileNamesWithExtentions = new ArrayList<String>();

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
			polica.setPadding(0, 0, 0, 70);
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

	@SuppressLint("ResourceAsColor")
	public void dodajSvesku() {

		if (sveskaCounter < 4) {
			sveskaCover = new ImageView(this);
			sveskaTitle = new TextView(this);

			sveska = new RelativeLayout(this);
			sveska.setBackgroundResource(R.drawable.notebook_bg);
			sveska.setGravity(Gravity.BOTTOM);

			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			sveskaTitleParams = new LayoutParams(192, LayoutParams.WRAP_CONTENT);

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
				File fajlic = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + foldernoIme
								+ "/notebook.xml");
				citanjeXMLaObjekt = new CitanjeXMLa(fajlic);
				sveskaTitle.setText(citanjeXMLaObjekt.getIme());
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
				sveskaTitle.setText("" + ukupniSveskaCounter);

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
				fileNamesWithExtentions.add(infile.getName().toString());
				dodajSvesku();
			}
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
		actionBar.setIcon(null);
		actionBar.setDisplayUseLogoEnabled(false);
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

		File fajlic = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/"
				+ fileNamesWithExtentions.get(arg0.getId()) + "/notebook.xml");
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
				showToast("Edit functionality is yet to be implemented. Stay tuned :P"); // No need to localize this as it is temporary

				mode.finish();
			}
				return true;

			case R.id.action_deletenotebook: {
				try {
					obrisiSvesku(selectednotebook.getId());
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
}