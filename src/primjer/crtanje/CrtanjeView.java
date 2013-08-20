package primjer.crtanje;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CrtanjeView extends View {

	public static Paint boja;
	public static mojaPutanja putanja;
	private Paint krugBoja;
	private Path krugPutanja;
	public static int LONG_PRESS_TIME = 500;
	public static ArrayList<mojaPutanja> paths = new ArrayList<mojaPutanja>();
	public static ArrayList<mojaPutanja> undonePaths = new ArrayList<mojaPutanja>();
	public static Bitmap MyBitmap;
	private Canvas mCanvas;

	// Varijabla za custom klasu QuickAction
	QuickAction qa;

	final Handler _handler = new Handler();
	Runnable _longPressed = new Runnable() {
		public void run() {

			// Poziva se metoda za prikaz QuickAction-a. Točnije, prikaže se
			// grid s 1 redom i 3 stupca, a u svakom stupcu je po jedan button
			qa.pokazi();

			Log.i("hepek", "Pritisnuto je dugo");
		}
	};

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		MyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(MyBitmap);
	}

	public void dodajFunkcija() {
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
			MyBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ocistiFunkcija() {
		Log.d("hepek", "pozvano");
		for (mojaPutanja p : paths) {
			p.reset();
		}
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		postInvalidate();
	}

	private void inicijalizacija(Context k) {
		// // Inicijalizacija varijabli poput boje, boje kruga, putanje i sl.
		// stvari //////

		boja = new Paint();
		krugBoja = new Paint();
		krugPutanja = new Path();
		// Inicijalizacija qa varijable. Prosljeđuje joj se trenutni View
		qa = new QuickAction(this);

	}

	private void postaviKist() {

		// ///////// Postavljanje kista //////////

		boja.setAntiAlias(true);
		boja.setColor(Color.BLUE);
		// Da boja bude kist:
		boja.setStyle(Paint.Style.STROKE);
		// Da kist bude okruglog oblika:
		boja.setStrokeJoin(Paint.Join.ROUND);
		// Debljina kista
		boja.setStrokeWidth(5f);
		putanja = new mojaPutanja(new Paint(boja));
		paths.add(putanja);

		// ///////// Postavljanje kruga oko kista //////////
		krugBoja.setAntiAlias(true);
		krugBoja.setColor(Color.CYAN);
		krugBoja.setStyle(Paint.Style.STROKE);
		krugBoja.setStrokeJoin(Paint.Join.MITER);
		krugBoja.setStrokeWidth(4f);
	}

	public CrtanjeView(Context k, AttributeSet set) {
		super(k, set);
		inicijalizacija(k);
		postaviKist();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (mojaPutanja p : paths) {
			canvas.drawPath(p, p.bojaPutanje);
			mCanvas.drawPath(p, p.bojaPutanje);
			canvas.drawPath(krugPutanja, krugBoja);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		float tackaX = e.getX();
		float tackaY = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			putanja.moveTo(tackaX, tackaY);
			_handler.postDelayed(_longPressed, LONG_PRESS_TIME);
			return true;

		case MotionEvent.ACTION_MOVE:

			putanja.lineTo(tackaX, tackaY);
			krugPutanja.reset();
			krugPutanja.addCircle(tackaX, tackaY, 25, Path.Direction.CW);
			_handler.removeCallbacks(_longPressed);
			break;

		case MotionEvent.ACTION_UP:
			CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			CrtanjeView.paths.add(CrtanjeView.putanja);
			krugPutanja.reset();
			_handler.removeCallbacks(_longPressed);
			break;

		default:
			return false;
		}

		postInvalidate();
		return true;
	}

}