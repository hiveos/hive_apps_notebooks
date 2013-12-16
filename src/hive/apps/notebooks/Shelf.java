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
import android.view.Gravity;
import android.view.Menu;
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

	public void dodajPolicu() {
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		if (getShelfStyle().equals("simple")) {
			polica.setBackgroundResource(R.drawable.shelf_simple);
		}
		if (getShelfStyle().equals("wooden")) {
			polica.setBackgroundResource(R.drawable.shelf_wooden);
		}
		police.add(polica);
		policaCounter++;
		params = new LayoutParams(LayoutParams.MATCH_PARENT, 270);
		polica.setLayoutParams(params);
		params.topMargin = 55;
		params.leftMargin = 50;
		params.rightMargin = 50;
		polica.setGravity(Gravity.CENTER_HORIZONTAL);
		ShelfHolder.addView(polica);
	}

	public void dodajSvesku() {

		if (sveskaCounter < 4) {
			sveska = new Button(this);
			sveska.setBackgroundResource(R.drawable.shelf_dark_green);
			sveska.setOnClickListener(this);
			sveska.setOnLongClickListener(this);
			sveska.setTextColor(getResources().getColor(R.color.dark_gray));
			sveska.setPadding(40, 0, 0, 20);
			sveska.setGravity(Gravity.BOTTOM);

			setNotebookCover("nocolor");

			if (isNeededToLoad) {
				File fajlic = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + foldernoIme
								+ "/notebook.xml");
				citanjeXMLaObjekt = new CitanjeXMLa(fajlic);
				sveska.setText(citanjeXMLaObjekt.getIme());
				if (citanjeXMLaObjekt.getBoja().equals("White"))
					setNotebookCover("White");
				if (citanjeXMLaObjekt.getBoja().equals("Grey"))
					setNotebookCover("Grey");
				if (citanjeXMLaObjekt.getBoja().equals("Blue"))
					setNotebookCover("Blue");
				if (citanjeXMLaObjekt.getBoja().equals("Dark Blue"))
					setNotebookCover("Dark Blue");
				if (citanjeXMLaObjekt.getBoja().equals("Purple"))
					setNotebookCover("Purple");
				if (citanjeXMLaObjekt.getBoja().equals("Dark Purple"))
					setNotebookCover("Dark Purple");
				if (citanjeXMLaObjekt.getBoja().equals("Green"))
					setNotebookCover("Green");
				if (citanjeXMLaObjekt.getBoja().equals("Dark Green"))
					setNotebookCover("Dark Green");
				if (citanjeXMLaObjekt.getBoja().equals("Orange"))
					setNotebookCover("Orange");
				if (citanjeXMLaObjekt.getBoja().equals("Dark Orange"))
					setNotebookCover("Dark Orange");
				if (citanjeXMLaObjekt.getBoja().equals("Red"))
					setNotebookCover("Red");
				if (citanjeXMLaObjekt.getBoja().equals("Dark Red"))
					setNotebookCover("Dark Red");

				isNeededToLoad = false;

			} else
				sveska.setText("" + ukupniSveskaCounter);

			sveska.setId(ukupniSveskaCounter);
			sveske.add(sveska);
			sveskaCounter++;
			ukupniSveskaCounter++;
			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			sveskaParams.leftMargin = 25;
			sveskaParams.bottomMargin = 5;
			sveskaParams.topMargin = 19;
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
		actionBar.setTitle("NOTEBOOKS");

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
		// TODO Auto-generated method stub
		identifikacija = arg0.getId();
		try {
			obrisiSvesku();
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "Error: Notebook cannot be deleted",
					Toast.LENGTH_SHORT);
		}
		return false;
	}

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

	public void setNotebookCover(String color) {

		if (getShelfStyle().equals("no")) {
			if (color.equals("White") || AddNotebook.selectedcolor == 1)
				sveska.setBackgroundResource(R.drawable.shelf_white);
			if (color.equals("Grey") || AddNotebook.selectedcolor == 2)
				sveska.setBackgroundResource(R.drawable.shelf_gray);
			if (color.equals("Blue") || AddNotebook.selectedcolor == 13)
				sveska.setBackgroundResource(R.drawable.shelf_blue);
			if (color.equals("Dark Blue") || AddNotebook.selectedcolor == 4)
				sveska.setBackgroundResource(R.drawable.shelf_dark_blue);
			if (color.equals("Purple") || AddNotebook.selectedcolor == 5)
				sveska.setBackgroundResource(R.drawable.shelf_purple);
			if (color.equals("Dark Purple") || AddNotebook.selectedcolor == 6)
				sveska.setBackgroundResource(R.drawable.shelf_dark_purple);
			if (color.equals("Green") || AddNotebook.selectedcolor == 7)
				sveska.setBackgroundResource(R.drawable.shelf_green);
			if (color.equals("Dark Green") || AddNotebook.selectedcolor == 8)
				sveska.setBackgroundResource(R.drawable.shelf_dark_green);
			if (color.equals("Orange") || AddNotebook.selectedcolor == 9)
				sveska.setBackgroundResource(R.drawable.shelf_orange);
			if (color.equals("Dark Orange") || AddNotebook.selectedcolor == 10)
				sveska.setBackgroundResource(R.drawable.shelf_dark_orange);
			if (color.equals("Red") || AddNotebook.selectedcolor == 11)
				sveska.setBackgroundResource(R.drawable.shelf_red);
			if (color.equals("Dark Red") || AddNotebook.selectedcolor == 12)
				sveska.setBackgroundResource(R.drawable.shelf_dark_red);
		} else {
			if (color.equals("White") || AddNotebook.selectedcolor == 1)
				sveska.setBackgroundResource(R.drawable.shelf_white_no_shadow);
			if (color.equals("Grey") || AddNotebook.selectedcolor == 2)
				sveska.setBackgroundResource(R.drawable.shelf_gray_no_shadow);
			if (color.equals("Blue") || AddNotebook.selectedcolor == 3)
				sveska.setBackgroundResource(R.drawable.shelf_blue_no_shadow);
			if (color.equals("Dark Blue") || AddNotebook.selectedcolor == 4)
				sveska.setBackgroundResource(R.drawable.shelf_dark_blue_no_shadow);
			if (color.equals("Purple") || AddNotebook.selectedcolor == 5)
				sveska.setBackgroundResource(R.drawable.shelf_purple_no_shadow);
			if (color.equals("Dark Purple") || AddNotebook.selectedcolor == 6)
				sveska.setBackgroundResource(R.drawable.shelf_dark_purple_no_shadow);
			if (color.equals("Green") || AddNotebook.selectedcolor == 7)
				sveska.setBackgroundResource(R.drawable.shelf_green_no_shadow);
			if (color.equals("Dark Green") || AddNotebook.selectedcolor == 8)
				sveska.setBackgroundResource(R.drawable.shelf_dark_green_no_shadow);
			if (color.equals("Orange") || AddNotebook.selectedcolor == 9)
				sveska.setBackgroundResource(R.drawable.shelf_orange_no_shadow);
			if (color.equals("Dark Orange") || AddNotebook.selectedcolor == 10)
				sveska.setBackgroundResource(R.drawable.shelf_dark_orange_no_shadow);
			if (color.equals("Red") || AddNotebook.selectedcolor == 11)
				sveska.setBackgroundResource(R.drawable.shelf_red_no_shadow);
			if (color.equals("Dark Red") || AddNotebook.selectedcolor == 12)
				sveska.setBackgroundResource(R.drawable.shelf_dark_red_no_shadow);
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