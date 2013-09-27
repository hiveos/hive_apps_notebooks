package hive.apps.notebooks;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import hive.apps.notebooks.R.drawable;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Shelf extends Activity implements OnClickListener, OnLongClickListener{

	private LinearLayout ShelfHolder;
	private LayoutParams params;
	private LayoutParams sveskaParams;
	private LinearLayout polica;
	private Button sveska;
	private int policaCounter;
	private int policaNaKojojSeNalazimo;
	private int sveskaCounter;
	public static int ukupniSveskaCounter;
	private Boolean popunjenePocetnePolice=false;
	private ArrayList<LinearLayout> police = new ArrayList<LinearLayout>();
	public static ArrayList<Button> sveske = new ArrayList<Button>();
	private File notebooksRoot;
	private File[] brojSveskica;
	private String foldernoIme;
	private int brojSveskiZaLoadati;
	
	public void dodajPolicu() {
		ShelfHolder=(LinearLayout)findViewById(R.id.ShelfHolder);
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		polica.setBackgroundResource(R.drawable.shelf);
		police.add(polica);
		policaCounter++;
		params= new LayoutParams(LayoutParams.MATCH_PARENT, 210);
		polica.setLayoutParams(params);
		ShelfHolder.addView(polica);
	}
	
	public void dodajSvesku(){
		
		if(sveskaCounter<4)
		{
			sveska = new Button(this);
			sveska.setBackgroundResource(R.drawable.notebook_test);
			sveska.setOnClickListener(this);
			sveska.setOnLongClickListener(this);
			sveska.setGravity(Gravity.CENTER_HORIZONTAL);
			sveska.setText("Notebook"+ukupniSveskaCounter);
			sveske.add(sveska);
			sveskaCounter++;
			ukupniSveskaCounter++;
			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			sveskaParams.leftMargin=50;
			sveskaParams.bottomMargin=6;
			police.get(policaNaKojojSeNalazimo).addView(sveske.get(ukupniSveskaCounter-1),sveskaParams);
		}
		else if(sveskaCounter>=4)
		{
			if(ukupniSveskaCounter>=20)
				dodajPolicu();
			
			policaNaKojojSeNalazimo++;
			sveskaCounter=0;
			dodajSvesku();
		}
		
	}
	
	private void dodajSveskuIzFoldera()
	{
		if(sveskaCounter<4)
		{
			sveska = new Button(this);
			sveska.setBackgroundResource(R.drawable.notebook_test);
			sveska.setOnClickListener(this);
			sveska.setOnLongClickListener(this);
			sveska.setGravity(Gravity.CENTER_HORIZONTAL);
			sveska.setText(foldernoIme);
			sveske.add(sveska);
			sveskaCounter++;
			ukupniSveskaCounter++;
			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			sveskaParams.leftMargin=50;
			sveskaParams.bottomMargin=6;
			police.get(policaNaKojojSeNalazimo).addView(sveske.get(ukupniSveskaCounter-1),sveskaParams);
		}
		else if(sveskaCounter>=4)
		{
			if(ukupniSveskaCounter>=20)
				dodajPolicu();
			
			policaNaKojojSeNalazimo++;
			sveskaCounter=0;
			dodajSveskuIzFoldera();
		}
	}
	
	private void loadajSveske()
	{
		brojSveskica = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/").listFiles();
		
		for(File infile: brojSveskica)
		{
			if(infile.isDirectory())
			{
				foldernoIme=infile.getName();
				dodajSveskuIzFoldera();
			}
		}
	}
	
	private void inicijaliziraj()
	{
		policaCounter=0;
		sveskaCounter=0;
		policaNaKojojSeNalazimo=0;
		
		notebooksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/");
		if (!notebooksRoot.exists()) {
			notebooksRoot.mkdirs();
		}
		
		brojSveskiZaLoadati= new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/").listFiles().length;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		inicijaliziraj();
		
		for(int i=0; i<5; i++)
			dodajPolicu();
		
		if(brojSveskiZaLoadati!=0)
			loadajSveske();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent gotoNotebookInt = new Intent(this, Glavna.class);
        startActivity(gotoNotebookInt);
		
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}	

}
