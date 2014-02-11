//Klasa CrtanjeView, samo ime ka�e - custom view u kom se odvija svo crtanje

package hive.apps.notebooks;

import java.io.File;
import java.io.FileOutputStream;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Vector;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("ResourceAsColor")
public class CrtanjeView extends View {

	private Bitmap kursor;
	public static Paint boja;
	public static mojaPutanja putanja;
	private Paint krugBoja;
	private Path krugPutanja;
	public static Vector<Bitmap> sviZaCrtat;
	public static Vector<Pair<Integer, Integer>> pozicije;
	public static Vector<Boolean> jeLiRijec;
	public static int LONG_PRESS_TIME = 500;
	public static ArrayList<mojaPutanja> paths = new ArrayList<mojaPutanja>();
	public static ArrayList<mojaPutanja> drawingPaths = new ArrayList<mojaPutanja>();
	public static ArrayList<mojaPutanja> undonePaths = new ArrayList<mojaPutanja>();
	public static Bitmap MyBitmap;
	public static int odvojiZaKursor = 0;
	public static int enterZaKursor = 0;
	public Button bEnter;
	public Button bSpace;
	public Button bUndo;
	public static int visinaLinije = 44; // ukupna visina linije sa gornjom
											// marginom
	public static int marginaLinijeGore = 5; // koliko iznad linije treba
												// ostaviti prostora za prosli
												// red
	public static int visinaPraznogIznadLinije = 35; // visina praznine iznad
														// PRVE linije
	public static int marginaLinijeLijevo = 10; // kolko je odvojena svaka
												// linija lijevo od ekrana
	public static int marginaLinijeDesno = 10; // kolko je odvojena svaka linija
												// desno od ekrana
	public static int sirinaRazmaka = 5; // koliko piksela odvojiti svaku rijec
											// od prosle rijeci
	public static int trenutnaLinija = 0; // na kojoj smo trenutno liniji. Ovo
											// je 3 zbog testiranja, treba biti
											// u pocetku 0
	public static int trenutnaSirinaLinije = 50; // dokle piksela smo dosli
													// trenutno u trenutnoj
													// liniji
	private static boolean vecStavljao; // da li je ovo prva rijec koju
										// stavljamo u red. Mislim da nema
										// potrebe za ovim, al eto, za svaki
										// slucaj
	private Canvas mCanvas;
	static Canvas dCanvas;
	public static Bitmap drawingBitmap;
	public static Boolean writing = true;
	private Color plavaBojaOlovke, crvenaBojaOlovke;
	static int ekranSirina;
	static int ekranVisina;
	static float x1 = 0;
	static float x2 = 0;
	static float y1 = 0;
	static float y2 = 0;
	static int odvoji = 25; // koliko ces odvojit kad kliknes Space
	private Glavna g = new Glavna();
	private Runnable runnable;
	final Handler _handler = new Handler();
	public static Path eraser;

	public static int sirinaLinije;	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		ekranSirina = w;
		ekranVisina = h;
		x1 = ekranSirina;
		x2 = 0;
		y1 = ekranVisina;
		y2 = 0;
		MyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Glavna.bitmapZaCanvas = Bitmap.createBitmap(w, h,
				Bitmap.Config.ARGB_8888);
		drawingBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Glavna.cZaSpremanje = new Canvas(Glavna.bitmapZaCanvas);
		Glavna.cZaSpremanje.drawColor(Color.WHITE);
		Glavna.cZaSpremanje.drawBitmap(Glavna.tekstura, 0, 0, null);
		mCanvas = new Canvas(MyBitmap);
		dCanvas = new Canvas(drawingBitmap);
	}

	// Scale se radi na nacin sto znamo kolika visina nam treba NA koju cemo
	// scale-ovati
	// a sirinu prilagodjavamo

	private Bitmap kopiraj(Rect r) {
		Bitmap bmp = Bitmap.createBitmap(r.right - r.left, r.bottom - r.top,
				Bitmap.Config.ARGB_8888);
		for (int i = 0; i < bmp.getWidth(); i++) {
			for (int j = 0; j < bmp.getHeight(); j++) {
				bmp.setPixel(i, j, MyBitmap.getPixel(i + r.left, j + r.top));
			}
		}
		return bmp;
	}

	public void dodajFunkcija() {
		odvojiZaKursor = 0;
		enterZaKursor = 0;

		if (x1 == ekranSirina && x2 == 0 && y1 == ekranVisina && y2 == 0)
			return;
		Rect r;
		if (!Glavna.toggleGuides) {
			r = pronadjiRec(MyBitmap);
		} else {
			r = new Rect();
			int[] loc = new int[2];
			Glavna.guideLines.getLocationOnScreen(loc);
			r.left = (int) x1;
			r.right = (int) x2;
			r.top = (int) y1;// Glavna.guideLines.getTop()-((Glavna.guideLines.getBottom()-Glavna.guideLines.getTop())/2);
			r.bottom = Glavna.guideLines.getBottom()
					- ((Glavna.guideLines.getBottom() - Glavna.guideLines
							.getTop()) / 2) + 100;
			// r.top = loc[1];
			// r.left = loc[0];
			// r.bottom = loc[1] + Glavna.guideLines.getHeight();
			// r.right = loc[0] + Glavna.guideLines.getWidth();
		}
		Bitmap crop = kopiraj(r);
		if (!Glavna.toggleGuides) {
			double scale = (double) crop.getHeight()
					/ (double) (visinaLinije - marginaLinijeGore); // Koliko
																	// scale-ovati
																	// sirinu za
																	// datu
																	// visinu.
																	// Visina
																	// rijeci
																	// treba
																	// biti
																	// visinaLinije
																	// -
																	// marginaLinijeGore
			// Bitmap bmp=Bitmap.createBitmap(MyBitmap);
			Bitmap bmp = Bitmap.createScaledBitmap(crop,
					(int) ((double) crop.getWidth() / scale), visinaLinije
							- marginaLinijeGore, true); // Scaleovati na
														// potrebne
														// dimenzije, a sirina
														// se
														// dijeljenjem sa scale
														// dobije tacna sirina
														// za
														// datu visinu (tj,
														// width/height ratio se
														// ne
														// mijenja)
			dodajScalovano(bmp, Glavna.toggleGuides);
			ocistiFunkcija();
		} else {
			double scale = (double) crop.getHeight()
					/ (double) (64 - marginaLinijeGore); // Koliko
															// scale-ovati
															// sirinu za
															// datu visinu.
															// Visina rijeci
															// treba biti
															// visinaLinije
															// -
															// marginaLinijeGore
			// Bitmap bmp=Bitmap.createBitmap(MyBitmap);
			Bitmap bmp = Bitmap.createScaledBitmap(crop,
					(int) ((double) crop.getWidth() / scale),
					64 - marginaLinijeGore, true); // Scaleovati na potrebne
													// dimenzije, a sirina se
													// dijeljenjem sa scale
													// dobije tacna sirina za
													// datu visinu (tj,
													// width/height ratio se ne
													// mijenja)
			dodajScalovano(bmp, Glavna.toggleGuides);
			ocistiFunkcija();
		}

	}

	public void dodajScalovano(Bitmap bmp, Boolean jeLiOvoRijec) {
		int tmp = trenutnaSirinaLinije;
		if (bmp.getWidth() + trenutnaSirinaLinije + sirinaRazmaka > sirinaLinije
				&& vecStavljao) // ako bi stavili ovu rijec u trenutnu liniju,
								// da li bi prekoracili trenutni red
		{
			// predji u novi red ako bi prekoracili
			trenutnaLinija++;

			trenutnaSirinaLinije = bmp.getWidth() + 50;
			tmp = 50;
			vecStavljao = false;
		} else {
			// ako ne bi, onda postavi rijec nakon zadnje rijeci
			trenutnaSirinaLinije += bmp.getWidth() + sirinaRazmaka;
			vecStavljao = true;
		}
		pozicije.add(Pair.create(tmp, trenutnaLinija * visinaLinije
				+ visinaPraznogIznadLinije)); // dodati poziciju za trenutnu
												// rijec
		sviZaCrtat.add(bmp); // dodati sliku trenutne rijeci
		jeLiRijec.add(jeLiOvoRijec);
		Space();
		System.out.println("dodao");
	}

	private Rect pronadjiRec(Bitmap image) {

		Rect rect = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
		return rect;
	}

	public void ocistiFunkcija() {
		// Klikom na erase button, poziva se ocistiFunkcija koja brise trenutne
		// pathove na
		// canvasu

		x1 = ekranSirina;
		x2 = 0;
		y1 = ekranVisina;
		y2 = 0;
		Log.d("hepek", "pozvano");
		for (mojaPutanja p : paths) {
			p.reset();
		}
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		postInvalidate();
	}

	private void inicijalizacija(Context k) {
		// Inicijalizacija varijabli poput boje, boje kruga, putanje i sl.
		// stvari
		boja = new Paint();
		krugBoja = new Paint();
		krugPutanja = new Path();
		sviZaCrtat = new Vector<Bitmap>();
		// rectovi = new Vector<Rect>();
		pozicije = new Vector<Pair<Integer, Integer>>();
		jeLiRijec = new Vector<Boolean>();

		kursor = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.kursor);
		runnable = new Runnable() {
			@Override
			public void run() {
				dodajFunkcija();
			}
		};

	}

	private void postaviKist() {

		// ///////// Postavljanje kista //////////

		boja.setAntiAlias(true);
		boja.setColor(Color.parseColor("#5a8cd0"));
		// Da boja bude kist:
		boja.setStyle(Paint.Style.STROKE);
		// Da kist bude okruglog oblika:
		boja.setStrokeJoin(Paint.Join.ROUND);
		// Debljina kista
		boja.setStrokeWidth(6f);
		putanja = new mojaPutanja(new Paint(boja));
		if (!Glavna.drawingMode)
			paths.add(putanja);
		if (Glavna.drawingMode)
			drawingPaths.add(putanja);

		// //////// Postavljanje kruga oko kista //////////
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
		// Iscrtavanje putanja
		canvas.drawBitmap(Glavna.tekstura, 0, 0, null);
		
		if (!Glavna.LoadaniDrawing.isRecycled()) {
			canvas.drawBitmap(Glavna.LoadaniDrawing, 0, 0, null);
			dCanvas.drawBitmap(Glavna.LoadaniDrawing, 0, 0, null);
			Glavna.cZaSpremanje.drawBitmap(Glavna.LoadaniDrawing, 0, 0, null);
		}
		
		Log.d("Writing Paths size: ", paths.size() + "");
		Log.d("Drawing Paths size: ", drawingPaths.size() + "");

		for (mojaPutanja dp : drawingPaths) {
			canvas.drawPath(dp, dp.bojaPutanje);
			Glavna.cZaSpremanje.drawPath(dp, dp.bojaPutanje);
			dCanvas.drawPath(dp, dp.bojaPutanje);
		}

		for (mojaPutanja p : paths) {
			canvas.drawPath(p, p.bojaPutanje);
			mCanvas.drawPath(p, p.bojaPutanje);
			canvas.drawPath(krugPutanja, krugBoja);
		}

		for (int i = 0; i < sviZaCrtat.size(); i++) {
			if (!jeLiRijec.elementAt(i))
				canvas.drawBitmap(sviZaCrtat.elementAt(i),
						pozicije.elementAt(i).first,
						pozicije.elementAt(i).second, boja); // crtati sve
																// rijeci sto
																// imamo
			else
				canvas.drawBitmap(sviZaCrtat.elementAt(i),
						pozicije.elementAt(i).first,
						pozicije.elementAt(i).second + 5, // Koliko propada
						boja); // crtati sve rijeci sto imamo
			Glavna.cZaSpremanje.drawBitmap(sviZaCrtat.elementAt(i),
					pozicije.elementAt(i).first, pozicije.elementAt(i).second,
					boja);
		}
		int visinaKursora = 0;
		if (!pozicije.isEmpty()) {
			visinaKursora = pozicije.lastElement().first + odvojiZaKursor
					+ sviZaCrtat.lastElement().getWidth();
		}
		if (enterZaKursor != 0) {
			visinaKursora = 50;
		}
		if (!pozicije.isEmpty()) {
			canvas.drawBitmap(kursor, visinaKursora,
					pozicije.lastElement().second
							+ (enterZaKursor * visinaLinije), boja);
		} else
			canvas.drawBitmap(kursor, 50, (visinaLinije / 2) + 5, boja);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		sirinaLinije = this.getWidth() - marginaLinijeLijevo
				- marginaLinijeDesno; // olaksica da nam kaze kolika je ukupna
										// linija gdje se pise

		float tackaX = e.getX();
		float tackaY = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!Glavna.drawingMode)
			_handler.removeCallbacks(runnable);
			if (!Glavna.drawingMode) {
			if (x1 > tackaX - boja.getStrokeWidth()) {
				if (tackaX - boja.getStrokeWidth() >= 0)
					x1 = tackaX - boja.getStrokeWidth();
				else
					x1 = 0;
			}
			if (x2 < tackaX + boja.getStrokeWidth()) {
				if (tackaX + boja.getStrokeWidth() <= ekranSirina)
					x2 = tackaX + boja.getStrokeWidth();
				else
					x2 = ekranSirina;
			}
			if (y1 > tackaY - boja.getStrokeWidth()) {
				if (tackaY - boja.getStrokeWidth() >= 0)
					y1 = tackaY - boja.getStrokeWidth();
				else
					y1 = 0;
			}
			if (y2 < tackaY + boja.getStrokeWidth()) {
				if (tackaY + boja.getStrokeWidth() <= ekranVisina)
					y2 = tackaY + boja.getStrokeWidth();
				else
					y2 = ekranVisina;
			}
			}
			putanja.moveTo(tackaX, tackaY);
			return true;

		case MotionEvent.ACTION_MOVE:
			if (!Glavna.drawingMode) {
			if (x1 > tackaX - boja.getStrokeWidth()) {
				if (tackaX - boja.getStrokeWidth() >= 0)
					x1 = tackaX - boja.getStrokeWidth();
				else
					x1 = 0;
			}
			if (x2 < tackaX + boja.getStrokeWidth()) {
				if (tackaX + boja.getStrokeWidth() <= ekranSirina)
					x2 = tackaX + boja.getStrokeWidth();
				else
					x2 = ekranSirina;
			}
			if (y1 > tackaY - boja.getStrokeWidth()) {
				if (tackaY - boja.getStrokeWidth() >= 0)
					y1 = tackaY - boja.getStrokeWidth();
				else
					y1 = 0;
			}
			if (y2 < tackaY + boja.getStrokeWidth()) {
				if (tackaY + boja.getStrokeWidth() <= ekranVisina)
					y2 = tackaY + boja.getStrokeWidth();
				else
					y2 = ekranVisina;
			}
			}
			putanja.lineTo(tackaX, tackaY);
			krugPutanja.reset();
			krugPutanja.addCircle(tackaX, tackaY, 25, Path.Direction.CW);
			// _handler.removeCallbacks(_longPressed);
			break;

		case MotionEvent.ACTION_UP:
			if (!Glavna.drawingMode) {
			if (x1 > tackaX - boja.getStrokeWidth()) {
				if (tackaX - boja.getStrokeWidth() >= 0)
					x1 = tackaX - boja.getStrokeWidth();
				else
					x1 = 0;
			}
			if (x2 < tackaX + boja.getStrokeWidth()) {
				if (tackaX + boja.getStrokeWidth() <= ekranSirina)
					x2 = tackaX + boja.getStrokeWidth();
				else
					x2 = ekranSirina;
			}
			if (y1 > tackaY - boja.getStrokeWidth()) {
				if (tackaY - boja.getStrokeWidth() >= 0)
					y1 = tackaY - boja.getStrokeWidth();
				else
					y1 = 0;
			}
			if (y2 < tackaY + boja.getStrokeWidth()) {
				if (tackaY + boja.getStrokeWidth() <= ekranVisina)
					y2 = tackaY + boja.getStrokeWidth();
				else
					y2 = ekranVisina;
			}
			}
			CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			if (!Glavna.drawingMode)
				paths.add(putanja);
			else
				drawingPaths.add(putanja);
			krugPutanja.reset();
			if (!Glavna.drawingMode)
			_handler.postDelayed(runnable, 500);
			// _handler.removeCallbacks(_longPressed);

			break;

		default:
			return false;
		}

		postInvalidate();
		return true;
	}

	public void Refresh() {
		postInvalidate();
	}

	public void Space() {
		if (odvoji + trenutnaSirinaLinije + sirinaRazmaka > ekranSirina
				- marginaLinijeLijevo - marginaLinijeDesno) {
			trenutnaLinija++;
			trenutnaSirinaLinije = marginaLinijeLijevo;
			odvojiZaKursor = 50;
			enterZaKursor += 1;
		} else {
			trenutnaSirinaLinije += odvoji;
			odvojiZaKursor += 25;
		}
		postInvalidate();
	}

	public void Enter() {
		trenutnaLinija++;
		trenutnaSirinaLinije = 50;
		odvojiZaKursor = 0;
		enterZaKursor += 1;
		ocistiFunkcija();
	}

	public void Undo() {
		odvojiZaKursor = 50;
		enterZaKursor = 0;
		if (sviZaCrtat.isEmpty())
			return;
		sviZaCrtat.remove(sviZaCrtat.lastElement());
		pozicije.remove(pozicije.lastElement());
		jeLiRijec.remove(jeLiRijec.lastElement());
		if (!pozicije.isEmpty()) {
			trenutnaSirinaLinije = pozicije.lastElement().first
					+ sviZaCrtat.lastElement().getWidth() + sirinaRazmaka;
			trenutnaLinija = (pozicije.lastElement().second - visinaPraznogIznadLinije)
					/ visinaLinije;
			// trenutnaLinija*visinaLinije+visinaPraznogIznadLinije
		} else {
			trenutnaSirinaLinije = 50;
			trenutnaLinija = 0;
		}
		Glavna.cZaSpremanje.drawColor(Color.WHITE);
		Glavna.cZaSpremanje.drawBitmap(Glavna.tekstura, 0, 0, null);

		ocistiFunkcija();
		if(sviZaCrtat.size()>0) Space();
	}

	public static void tari() {
		odvojiZaKursor = 50;
		enterZaKursor = 0;
		// sviZaCrtat.remove(sviZaCrtat.lastElement());
		// pozicije.remove(pozicije.lastElement());
		if (!pozicije.isEmpty()) {
			trenutnaSirinaLinije = pozicije.lastElement().first
					+ sviZaCrtat.lastElement().getWidth() + sirinaRazmaka;
			trenutnaLinija = (pozicije.lastElement().second - visinaPraznogIznadLinije)
					/ visinaLinije;
			// trenutnaLinija*visinaLinije+visinaPraznogIznadLinije
		} else {
			trenutnaSirinaLinije = 50;
			trenutnaLinija = 0;
		}
		x1 = ekranSirina;
		x2 = 0;
		y1 = ekranVisina;
		y2 = 0;
		Log.d("hepek", "pozvano");
		for (mojaPutanja p : paths) {
			p.reset();
		}
	}

}