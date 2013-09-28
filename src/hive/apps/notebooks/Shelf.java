package hive.apps.notebooks;
 
import java.io.File;
import java.io.IOException;
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
import android.widget.Toast;
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
        public static ArrayList<String> imenaSveski = new ArrayList<String>();
        private File notebooksRoot;
        private File[] brojSveskica;
        private String foldernoIme;
        private int brojSveskiZaLoadati;
        private int identifikacija;
        private Boolean isNeededToLoad=false;
       
        public void dodajPolicu() {
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
                       
                        if(isNeededToLoad)
                        {
                                sveska.setText(foldernoIme);
                                isNeededToLoad=false;
                        }
                        else
                                sveska.setText(""+ukupniSveskaCounter);
                       
                        sveska.setId(ukupniSveskaCounter);
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
       
        private void loadajSveske()
        {
                brojSveskica = new File(Environment.getExternalStorageDirectory()
                                + "/HIVE/Notebooks/").listFiles();
               
                for(File infile: brojSveskica)
                {
                        if(infile.isDirectory())
                        {
                                foldernoIme=infile.getName();
                                if(foldernoIme.length()>8)
                                foldernoIme=foldernoIme.substring(0, 5) + "...";
                               
                                isNeededToLoad=true;
                                dodajSvesku();
                        }
                }
        }
       
        private void obrisiSvesku()
        {
                String imeSveskeZaBrisanje="";
                for(int i=0; i<ukupniSveskaCounter; i++)
                {
                        if(sveske.get(i).getId()==identifikacija)
                        {
                                imeSveskeZaBrisanje=sveske.get(i).getText().toString();
                               
                                Toast toast = Toast.makeText(this,
                                "Deleting " + imeSveskeZaBrisanje, Toast.LENGTH_LONG);
                                        toast.show();
                               
                                break;
                        }
                }
               
                brojSveskica = new File(Environment.getExternalStorageDirectory()
                                + "/HIVE/Notebooks/").listFiles();
               
                for(File infile: brojSveskica)
                {
                        if(infile.isDirectory() && infile.getName().toString().equals(imeSveskeZaBrisanje))
                        {
                                infile.delete();
                        }
                }
               
        }
       
        private void inicijaliziraj()
        {
                ShelfHolder=(LinearLayout)findViewById(R.id.ShelfHolder);
                policaCounter=0;
                sveskaCounter=0;
                policaNaKojojSeNalazimo=0;
                ukupniSveskaCounter=0;
                sveske.clear();
                police.clear();
                ShelfHolder.removeAllViews();
               
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
        protected void onResume() {
                // TODO Auto-generated method stub
                super.onResume();
                inicijaliziraj();
               
                for(int i=0; i<5; i++)
                        dodajPolicu();
               
                if(brojSveskiZaLoadati!=0)
                        loadajSveske();
        }
 
        @Override
        public boolean onLongClick(View arg0) {
                // TODO Auto-generated method stub
                identifikacija=arg0.getId();
                try
                {
	                obrisiSvesku();
	                Intent intent = getIntent();
	                finish();
	                startActivity(intent);
                }
                catch (Exception e) 
                {
                	Toast.makeText(this, "Error: Notebook cannot be deleted", Toast.LENGTH_SHORT);
                }
                return false;
        }      
 
}