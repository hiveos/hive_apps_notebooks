package primjer.crtanje;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;


//hehe, some random comment that has to be taken down
public class Glavna extends Activity implements OnClickListener {

	public Button bOcisti;
	public Button bDodaj;
	CrtanjeView cv;
	LinearLayout ll;

	private void fullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fullscreen();

		setContentView(R.layout.glavna);
		ll = (LinearLayout) findViewById(R.id.vGlavni);

		ll.setOnClickListener(this);
		cv = (CrtanjeView) findViewById(R.id.view1);

		//cv.setOnTouchListener(this);

		bOcisti = (Button) findViewById(R.id.bCisti);
		bDodaj = (Button) findViewById(R.id.bUbaci);
		bOcisti.setOnClickListener(this);
		bDodaj.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	

	@Override
	protected void onStop() {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bUbaci:
			cv.dodajFunkcija();
			break;

		case R.id.bCisti:
			cv.ocistiFunkcija();
			break;
		}

	}
}