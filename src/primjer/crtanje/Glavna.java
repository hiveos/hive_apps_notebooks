<<<<<<< HEAD
package primjer.crtanje;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Glavna extends Activity{

	CrtanjeView cv;
	EditText eT;
	RelativeLayout ll;
	QuickAction qa;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fullscreen();
		setContentView(R.layout.glavna);
		ll = (RelativeLayout) findViewById(R.id.vGlavni);
		cv = (CrtanjeView) findViewById(R.id.view1);
		eT = (EditText) findViewById(R.id.editText1);

		// cv.setOnTouchListener(this);

	}

	//ActionBar
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		setTitle("Notebook Name");
		getMenuInflater().inflate(R.menu.crtanje, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
	//Gledanje na šta je korisnik kliknuo u ActionBaru

        switch (item.getItemId()) {
            case R.id.action_brush:
            	cv.otvoriMenu();
                return true;
            case R.id.action_clear:
            	cv.ocistiFunkcija();
                return true;
            case R.id.action_insert:
            	cv.dodajFunkcija();
            	return true;
            case R.id.action_mode:
            	if(eT.isEnabled()){
            		eT.setEnabled(false);
            		cv.bringToFront();
            	}
            	else{
            		eT.setEnabled(true);
            		eT.bringToFront();
            	}
            	return true;
            case R.id.action_text:
            	//Tu moram dodati text settings, poput mijenjanja boje, velièine i sl.
            	return true;
            default:
                return false;

        }

    }

	@Override
	protected void onPause () {
		super.onPause();
	}

	@Override
	protected void onStop () {
		// TODO Auto-generated method stub
		//Opet spremanje na sd

		File notebooksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/");

		if (!notebooksRoot.exists()) {
			notebooksRoot.mkdirs();
		}

		int pageCounter = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/").listFiles().length;
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/" + pageCounter + ".png");
		FileOutputStream ostream;
		try {
			file.createNewFile();
			ostream = new FileOutputStream(file);
			CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (mojaPutanja p : CrtanjeView.paths) {
			p.reset();
		}

		super.onStop();
	}
=======
package primjer.crtanje;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Glavna extends Activity{

	CrtanjeView cv;
	LinearLayout ll;
	QuickAction qa;

	/*
	 * private void fullscreen () {
	 * requestWindowFeature(Window.FEATURE_NO_TITLE);
	 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	 * WindowManager.LayoutParams.FLAG_FULLSCREEN); }
	 */

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fullscreen();
		setContentView(R.layout.glavna);
		ll = (LinearLayout) findViewById(R.id.vGlavni);

		cv = (CrtanjeView) findViewById(R.id.view1);

		// cv.setOnTouchListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		setTitle("Notebook Name");
		getMenuInflater().inflate(R.menu.crtanje, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_brush:
            	cv.otvoriMenu();
                return true;
            case R.id.action_clear:
            	cv.ocistiFunkcija();
                return true;
            case R.id.action_insert:
            	cv.dodajFunkcija();
            default:
                return false;

        }

    }

	@Override
	protected void onPause () {
		super.onPause();
	}

	@Override
	protected void onStop () {
		// TODO Auto-generated method stub

		File notebooksRoot = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/");

		if (!notebooksRoot.exists()) {
			notebooksRoot.mkdirs();
		}

		int pageCounter = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/").listFiles().length;
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/drawings/Notebook1/" + pageCounter + ".png");
		FileOutputStream ostream;
		try {
			file.createNewFile();
			ostream = new FileOutputStream(file);
			CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (mojaPutanja p : CrtanjeView.paths) {
			p.reset();
		}

		super.onStop();
	}
>>>>>>> ba9ff29a6c730159019047e797853fdefcc14dd0
}