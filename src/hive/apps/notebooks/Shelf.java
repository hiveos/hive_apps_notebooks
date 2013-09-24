package hive.apps.notebooks;

import java.util.Vector;

import hive.apps.notebooks.R.drawable;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Shelf extends Activity implements OnClickListener{

	private LinearLayout ShelfHolder;
	private LayoutParams params;
	private LayoutParams sveskaParams;
	private LinearLayout polica;
	private ImageButton sveska;
	private Vector<LinearLayout>police;
	private Vector<Button>sveske;
	private int policaCounter;
	private int sveskaCounter;
	private Boolean popunjenePocetnePolice=false;
	
	public void dodajPolicu() {
		ShelfHolder=(LinearLayout)findViewById(R.id.ShelfHolder);
		polica = new LinearLayout(this);
		polica.setOrientation(LinearLayout.HORIZONTAL);
		polica.setBackgroundResource(R.drawable.shelf);
		policaCounter++;
		params= new LayoutParams(LayoutParams.MATCH_PARENT, 210);
		polica.setLayoutParams(params);
		ShelfHolder.addView(polica);
	}
	
	public void dodajSvesku(){
		if(sveskaCounter<4)
		{
			sveska = new ImageButton(this);
			//sveska.setPadding(50, 40, 40, 40);
			sveska.setImageResource(R.drawable.notebook_shelf);
			sveska.setBackgroundColor(getResources().getColor(R.color.Dark_Orange));
			sveska.setOnClickListener(this);
			sveskaCounter++;
			sveskaParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			sveskaParams.leftMargin=50;
			sveskaParams.bottomMargin=6;
			polica.addView(sveska,sveskaParams);
		}
		else
		{
			dodajPolicu();
			sveskaCounter=0;
			dodajSvesku();
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf);
		policaCounter=0;
		sveskaCounter=0;
		dodajPolicu();
		
		
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

}
