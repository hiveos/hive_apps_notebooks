package hive.apps.notebooks;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class Glavna extends Activity implements OnClickListener,
		OnColorChangedListener {

	private Menu menu;

	CrtanjeView cv;
	RelativeLayout ll;
	ImageView guideLines;
	Button insertButton;
	ImageButton enterButton, spaceButton, undoButton;
	ImageButton leftPageButton, rightPageButton;
	public static String stil;
	public static String imeSveske;
	Vector<byte[]> niz = new Vector();
	public int stranica = 1;
	TextView stranicaGdjeSmo;
	static Bitmap tekstura;
	Bitmap tmpTekstura;
	Boolean toggleGuides = true;
	public int color;

	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fullscreen();
		setContentView(R.layout.glavna);
		guideLines = (ImageView) findViewById(R.id.guide);
		leftPageButton = (ImageButton) findViewById(R.id.bLeft);
		rightPageButton = (ImageButton) findViewById(R.id.bRight);
		ll = (RelativeLayout) findViewById(R.id.vGlavni);
		if (stil.equals("Grid")) {
			tmpTekstura = BitmapFactory.decodeResource(getResources(),
					R.drawable.texture_grid);
			tekstura = tmpTekstura.createScaledBitmap(tmpTekstura, 800, 1100,
					false);
			guideLines.setVisibility(View.VISIBLE);
		}
		if (stil.equals("Lines")) {
			tmpTekstura = BitmapFactory.decodeResource(getResources(),
					R.drawable.texture);
			tekstura = tmpTekstura.createScaledBitmap(tmpTekstura, 800, 1100,
					false);
			guideLines.setVisibility(View.VISIBLE);
		}
		if (stil.equals("Plain")) {
			ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
			guideLines.setVisibility(View.GONE);
		}
		stranicaGdjeSmo = (TextView) findViewById(R.id.stranicaNaKojojSeNalazimo);
		cv = (CrtanjeView) findViewById(R.id.view1);
		enterButton = (ImageButton) findViewById(R.id.bEnter);
		spaceButton = (ImageButton) findViewById(R.id.bSpace);
		undoButton = (ImageButton) findViewById(R.id.bUndo);
		insertButton = (Button) findViewById(R.id.insert);
		enterButton.setOnClickListener(this);
		spaceButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		leftPageButton.setOnClickListener(this);
		rightPageButton.setOnClickListener(this);
		ucitajLokacije();

		insertButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cv.dodajFunkcija();
			}
		});

		final SeekBar sizeBar = (SeekBar) findViewById(R.id.sbDebljina);
		sizeBar.setProgress(20);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				CrtanjeView.boja.setStrokeWidth(sizeBar.getProgress());
				getActionBar().setTitle(imeSveske);
				updateSetings();
				menu.findItem(R.id.action_brush).setIcon(
						R.drawable.ic_brush_settings);
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.brush_settings);
				menu.findItem(R.id.action_brush).setIcon(
						R.drawable.ic_brush_settings_selected);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);

		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		updateSetings();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ucitajLokacije();
	}

	String uzmiEkstenziju(String element) {
		String filenameArray[] = element.split("\\.");
		String ekstenzija = filenameArray[filenameArray.length - 1];
		return ekstenzija;
	}

	public void ucitajLokacije() {
		File fileSaRijecima = new File(
				Environment.getExternalStorageDirectory() + "/HIVE/Notebooks/"
						+ imeSveske + "/page" + stranica + "/locations.txt");
		CrtanjeView.pozicije.clear();
		CrtanjeView.sviZaCrtat.clear();
		try {
			Scanner rd = new Scanner(fileSaRijecima);
			while (true) {
				if (!rd.hasNextInt()) {
					rd.close();
					break;
				}
				int id = rd.nextInt();
				int x = rd.nextInt();
				int y = rd.nextInt();
				if (id == -1) {
					CrtanjeView.trenutnaLinija = x;
					CrtanjeView.trenutnaSirinaLinije = y;
					continue;
				}

				if (id >= CrtanjeView.pozicije.size()) {
					for (int i = CrtanjeView.pozicije.size(); i <= id; i++) {
						CrtanjeView.pozicije.add(new Pair<Integer, Integer>(0,
								0));
						CrtanjeView.sviZaCrtat.add(Bitmap.createBitmap(1, 1,
								Bitmap.Config.ARGB_8888));
					}
				}
				CrtanjeView.pozicije.set(id, new Pair<Integer, Integer>(x, y));
				Bitmap rijecZaUnijeti = BitmapFactory.decodeFile(new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/Notebooks/" + imeSveske + "/page"
								+ stranica + "/img" + id + ".png")
						.getAbsolutePath());
				CrtanjeView.sviZaCrtat.set(id, rijecZaUnijeti);
				// CrtanjeView.dodajScalovano(rijecZaUnijeti);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		this.menu = menu;
		setTitle(imeSveske);
		getMenuInflater().inflate(R.menu.crtanje, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		MenuItem brushSettingsItem = menu.findItem(R.id.action_brush);

		switch (item.getItemId()) {
		case R.id.action_brush:
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				closeDrawer();
				updateSetings();
				brushSettingsItem.setIcon(R.drawable.ic_brush_settings);
			} else {
				openDrawer();
				brushSettingsItem
						.setIcon(R.drawable.ic_brush_settings_selected);
			}
			return true;
		case R.id.action_clear:
			cv.ocistiFunkcija();
			return true;
		case R.id.action_guide:
			if (toggleGuides) {
				toggleGuides = false;
				guideLines.setVisibility(View.GONE);
			} else {
				toggleGuides = true;
				guideLines.setVisibility(View.VISIBLE);
			}
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

		File pagedir = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/" + imeSveske + "/page" + stranica);

		File lokacije = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Notebooks/" + imeSveske + "/page" + stranica
				+ "/locations.txt");

		if (!pagedir.exists()) {
			pagedir.mkdirs();
		}

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
			for (int i = 0; i < CrtanjeView.pozicije.size(); i++) {
				fw.append(i + " " + CrtanjeView.pozicije.get(i).first + " "
						+ CrtanjeView.pozicije.get(i).second + "\n");
				File data = new File(Environment.getExternalStorageDirectory()
						+ "/HIVE/Notebooks/" + imeSveske + "/page" + stranica
						+ "/img" + i + ".png");
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
										+ "/page" + stranica + "/img" + i
										+ ".png"));
				Log.d("i :", i + "");
				bos.write(niz.get(i));
				bos.flush();
				bos.close();
			}
			fw.append("-1 " + CrtanjeView.trenutnaLinija + " "
					+ CrtanjeView.trenutnaSirinaLinije + "\n");
			fw.flush();
			fw.close();
			fw = null;
			lokacije = null;

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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bEnter:
			cv.Enter();
			break;
		case R.id.bSpace:
			cv.Space();
			break;
		case R.id.bUndo:
			cv.Undo();
			break;
		case R.id.bLeft:
			if (stranica == 1)
				break;
			spremiRijeci();
			cv.sviZaCrtat.clear();
			CrtanjeView.pozicije.clear();
			niz.clear();
			if (stranica > 1)
				stranica--;
			stranicaGdjeSmo.setText("" + stranica);
			cv.Refresh();
			ucitajLokacije();
			break;
		case R.id.bRight:
			spremiRijeci();
			cv.sviZaCrtat.clear();
			CrtanjeView.pozicije.clear();
			niz.clear();
			stranica++;
			stranicaGdjeSmo.setText("" + stranica);
			cv.Refresh();
			ucitajLokacije();
			if (CrtanjeView.pozicije.isEmpty()) {
				CrtanjeView.tari();
			}
			break;

		}

	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	public void openDrawer() {
		mDrawerLayout.openDrawer(Gravity.START);
	}

	public void updateSetings() {
		SeekBar sizeBar = (SeekBar) findViewById(R.id.sbDebljina);
		color = picker.getColor();
		picker.setOldCenterColor(color);
		CrtanjeView.boja.setColor(color);
		CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
		CrtanjeView.paths.add(CrtanjeView.putanja);
		CrtanjeView.boja.setStrokeWidth(sizeBar.getProgress());
	}

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub

	}
}