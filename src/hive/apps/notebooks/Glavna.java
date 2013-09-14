package hive.apps.notebooks;

import java.io.File;
import java.io.FileOutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class Glavna extends Activity implements OnClickListener {

	CrtanjeView cv;
	RelativeLayout ll;
	QuickAction qa;
	Button enterButton, spaceButton, undoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fullscreen();
		setContentView(R.layout.glavna);
		ll = (RelativeLayout) findViewById(R.id.vGlavni);
		cv = (CrtanjeView) findViewById(R.id.view1);
		enterButton=(Button)findViewById(R.id.bEnter);
		spaceButton=(Button)findViewById(R.id.bSpace);
		undoButton=(Button)findViewById(R.id.bUndo);
		enterButton.setOnClickListener(this);
		spaceButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		// cv.setOnTouchListener(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		

	}

	// ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setTitle("Notebook Name");
		getMenuInflater().inflate(R.menu.crtanje, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Gledanje na �ta je korisnik kliknuo u ActionBaru

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
			return true;
		case R.id.action_text:
			// Tu moram dodati text settings, poput mijenjanja boje, veli�ine i
			// sl.
			return true;
		default:
			return false;

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// Opet spremanje na sd

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.bEnter:
				cv.Enter();
				break;
			case R.id.bSpace:
				cv.Space();
				break;
			case R.id.bUndo:
				cv.Undo();
				break;
		
		}
		
	}
}