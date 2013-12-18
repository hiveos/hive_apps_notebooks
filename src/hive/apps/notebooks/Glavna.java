package hive.apps.notebooks;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Glavna extends Activity implements OnClickListener {

	CrtanjeView cv;
	RelativeLayout ll;
	QuickAction qa;
	ImageView guideLines;
	Button enterButton, spaceButton, undoButton;
	public static String stil;
	public static String imeSveske;
	Vector<byte[]> niz = new Vector();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fullscreen();
		setContentView(R.layout.glavna);
		guideLines = (ImageView) findViewById(R.id.guide);

		ll = (RelativeLayout) findViewById(R.id.vGlavni);
		if (stil.equals("Grid")) {
			ll.setBackgroundResource(R.drawable.texture_grid);
			guideLines.setVisibility(View.VISIBLE);
		}
		if (stil.equals("Lines")) {
			ll.setBackgroundResource(R.drawable.texture);
			guideLines.setVisibility(View.VISIBLE);
		}
		if (stil.equals("Plain")) {
			ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
			guideLines.setVisibility(View.GONE);
		}
		cv = (CrtanjeView) findViewById(R.id.view1);
		 enterButton=(Button)findViewById(R.id.bEnter);
		 spaceButton=(Button)findViewById(R.id.bSpace);
		 undoButton=(Button)findViewById(R.id.bUndo);
		enterButton.setOnClickListener(this);
		spaceButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		try {
			ucitajRijeci();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		
	}

	public void ucitajRijeci() throws IOException {

	}

	public byte[] readBytes(InputStream inputStream) throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}

		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public Bitmap getBitmap(byte[] bitmap) {
		return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
	}

	// ActionBar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setTitle(imeSveske);
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
			if (CrtanjeView.writing)
				CrtanjeView.writing = false;
			else
				CrtanjeView.writing = true;
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
		spremiRijeci();

		for (mojaPutanja p : CrtanjeView.paths) {
			p.reset();
		}

		super.onStop();
	}

	public void spremiRijeci() {
		for (int i = 0; i < CrtanjeView.sviZaCrtat.size(); i++) {
			Bitmap bmp = CrtanjeView.sviZaCrtat.get(i);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			niz.add(byteArray);
		}

		File lokacije = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/" + imeSveske + "/locations.txt");

		if (!lokacije.exists()) {
			try {
				lokacije.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {

			FileWriter fw = new FileWriter(lokacije);
			for (int i = 0; i < niz.size(); i++) {
				fw.append(i + " " + CrtanjeView.pozicije.get(i).first + " "
						+ CrtanjeView.pozicije.get(i).second + "\n");
				File data = new File(Environment.getExternalStorageDirectory()
						+ "/HIVE/Notebooks/" + imeSveske + "/img" + i + ".png");
				if (!data.exists()) {
					try {
						data.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(
								Environment.getExternalStorageDirectory()
										+ "/HIVE/Notebooks/" + imeSveske
										+ "/img" + i + ".png"));
				bos.write(niz.get(i));
				bos.flush();
				bos.close();
			}
			fw.flush();
			fw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
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