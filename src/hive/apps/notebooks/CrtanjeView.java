//Klasa CrtanjeView, samo ime ka�e - custom view u kom se odvija svo crtanje

package hive.apps.notebooks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

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
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class CrtanjeView extends View {

	public static Paint boja;
	public static mojaPutanja putanja;
	private Paint krugBoja;
	private Path krugPutanja;
	private Vector<Bitmap> sviZaCrtat;
	private Vector<Pair<Integer, Integer>> pozicije;
	public static int LONG_PRESS_TIME = 500;
	public static ArrayList<mojaPutanja> paths = new ArrayList<mojaPutanja>();
	public static ArrayList<mojaPutanja> undonePaths = new ArrayList<mojaPutanja>();
	public static Bitmap MyBitmap;
	public static int visinaLinije = 20;
	public static int marginaLinijeGore = 5;
	public static int visinaPraznogIznadLinije = 30;
	public static int marginaLinijeLijevo = 10;
	public static int marginaLinijeDesno = 10;
	public static int sirinaRazmaka = 10;
	private int trenutnaLinija = 3;
	private int trenutnaSirinaLinije = 0;
	private boolean vecStavljao;
	private Canvas mCanvas;

	// Varijabla za custom klasu QuickAction
	QuickAction qa;
	
	//Provjera kod long clicka da otvara popup menu
	final Handler _handler = new Handler();
	Runnable _longPressed = new Runnable() {
		public void run() {
			qa.pokazi();
			Log.i("hepek", "Pritisnuto je dugo");
		}
	};
	
	public void otvoriMenu()
	{
		qa.pokazi();
	}
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		MyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(MyBitmap);
	}

	public void dodajFunkcija() {
		//Klikom na insert se sprema slika na sd karticu pod odre�enim imenom (0,1,2,3...)
		double scale=(double)MyBitmap.getHeight()/(double)(visinaLinije-marginaLinijeGore);
		//Bitmap bmp=Bitmap.createBitmap(MyBitmap);
		Bitmap bmp = Bitmap.createScaledBitmap(MyBitmap, (int)((double)MyBitmap.getWidth()/scale), visinaLinije-marginaLinijeGore, false);
		int sirinaLinije=this.getWidth()-marginaLinijeLijevo-marginaLinijeDesno;
		if ( bmp.getWidth()+trenutnaSirinaLinije+sirinaRazmaka > sirinaLinije && vecStavljao)
		{
			//predji u novi red
			trenutnaLinija++;
			trenutnaSirinaLinije=bmp.getWidth();
			vecStavljao=false;
		} else
			{
				trenutnaSirinaLinije+=bmp.getWidth()+sirinaRazmaka;
				vecStavljao=true;
			}
		pozicije.add(Pair.create(trenutnaSirinaLinije+marginaLinijeLijevo, trenutnaLinija*visinaLinije+visinaPraznogIznadLinije));
		sviZaCrtat.add(bmp);
		System.out.println("dodao");
		ocistiFunkcija();
	}

	public void ocistiFunkcija() {
		//Klikom na erase button, poziva se ocistiFunkcija koja brise trenutne pathove na
		//canvasu
		
		Log.d("hepek", "pozvano");
		for (mojaPutanja p : paths) {
			p.reset();
		}
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		postInvalidate();
	}

	private void inicijalizacija(Context k) {
		//Inicijalizacija varijabli poput boje, boje kruga, putanje i sl. stvari

		boja = new Paint();
		krugBoja = new Paint();
		krugPutanja = new Path();
		sviZaCrtat = new Vector<Bitmap>();
		pozicije = new Vector<Pair<Integer,Integer>>();
		// Inicijalizacija qa varijable. Proslje�uje joj se trenutni View
		qa = new QuickAction(this);

	}

	private void postaviKist() {

		/////////// Postavljanje kista //////////

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

		////////// Postavljanje kruga oko kista //////////
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
		
		//Iscrtavanje putanja

		for (mojaPutanja p : paths) {
			canvas.drawPath(p, p.bojaPutanje);
			mCanvas.drawPath(p, p.bojaPutanje);
			canvas.drawPath(krugPutanja, krugBoja);
		}
		
		Paint nist=new Paint(boja);
		for (int i=0;i<sviZaCrtat.size();i++)
		{
			System.out.print(i);
			System.out.print(" ");
			System.out.print(pozicije.elementAt(i).first);
			System.out.print(" ");
			System.out.println(pozicije.elementAt(i).second);
			System.out.print(" ");
			System.out.print(sviZaCrtat.elementAt(i).getWidth());
			System.out.print(" ");
			System.out.println(sviZaCrtat.elementAt(i).getHeight());
			mCanvas.drawBitmap(sviZaCrtat.elementAt(i), pozicije.elementAt(i).first, pozicije.elementAt(i).second,nist);
			canvas.drawBitmap(sviZaCrtat.elementAt(i), pozicije.elementAt(i).first, pozicije.elementAt(i).second,nist);
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