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
import android.graphics.Rect;
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
	public static int visinaLinije = 50; // ukupna visina linije sa gornjom marginom
    public static int marginaLinijeGore = 5; // koliko iznad linije treba ostaviti prostora za prosli red
    public static int visinaPraznogIznadLinije = 30; // visina praznine iznad PRVE linije
    public static int marginaLinijeLijevo = 10; // kolko je odvojena svaka linija lijevo od ekrana
    public static int marginaLinijeDesno = 10; // kolko je odvojena svaka linija desno od ekrana
    public static int sirinaRazmaka = 5; // koliko piksela odvojiti svaku rijec od prosle rijeci
    private int trenutnaLinija = 0; // na kojoj smo trenutno liniji. Ovo je 3 zbog testiranja, treba biti u pocetku 0
    private int trenutnaSirinaLinije = 0; // dokle piksela smo dosli trenutno u trenutnoj liniji
    private boolean vecStavljao; // da li je ovo prva rijec koju stavljamo u red. Mislim da nema potrebe za ovim, al eto, za svaki slucaj
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
	
	// Scale se radi na nacin sto znamo kolika visina nam treba NA koju cemo scale-ovati
//  a sirinu prilagodjavamo
	
	private Bitmap kopiraj(Rect r)
	{
		Bitmap bmp = Bitmap.createBitmap(r.right-r.left, r.bottom-r.top, Bitmap.Config.ARGB_8888);
		for (int i=0;i<bmp.getWidth();i++)
		{
			for (int j=0;j<bmp.getHeight();j++)
			{
				bmp.setPixel(i, j, MyBitmap.getPixel(i+r.left, j+r.top));
			}
		}
		return bmp;
	}

	public void dodajFunkcija() {
		Rect r = pronadziRec(MyBitmap);
		Bitmap crop = kopiraj(r);
		int tmp = trenutnaSirinaLinije;
		 double scale=(double)crop.getHeight()/(double)(visinaLinije-marginaLinijeGore); // Koliko scale-ovati sirinu za datu visinu. Visina rijeci treba biti visinaLinije - marginaLinijeGore
         //Bitmap bmp=Bitmap.createBitmap(MyBitmap);
         Bitmap bmp = Bitmap.createScaledBitmap(crop, (int)((double)crop.getWidth()/scale), visinaLinije-marginaLinijeGore, true); // Scaleovati na potrebne dimenzije, a sirina se dijeljenjem sa scale dobije tacna sirina za datu visinu (tj, width/height ratio se ne mijenja)
         int sirinaLinije=this.getWidth()-marginaLinijeLijevo-marginaLinijeDesno; // olaksica da nam kaze kolika je ukupna linija gdje se pise
         if ( bmp.getWidth()+trenutnaSirinaLinije+sirinaRazmaka > sirinaLinije && vecStavljao) // ako bi stavili ovu rijec u trenutnu liniju, da li bi prekoracili trenutni red
         {
                 //predji u novi red ako bi prekoracili
                 trenutnaLinija++;
                 trenutnaSirinaLinije=bmp.getWidth();
                 tmp=0;
                 vecStavljao=false;
         } else
                 {
                         //ako ne bi, onda postavi rijec nakon zadnje rijeci
                         trenutnaSirinaLinije+=bmp.getWidth()+sirinaRazmaka;
                         vecStavljao=true;
                 }
         pozicije.add(Pair.create(tmp, trenutnaLinija*visinaLinije+visinaPraznogIznadLinije)); // dodati poziciju za trenutnu rijec
         sviZaCrtat.add(bmp); // dodati sliku trenutne rijeci
   //      rectovi.add(pronadziRec(bmp));
         System.out.println("dodao");
         ocistiFunkcija();
	}

	private Rect pronadziRec(Bitmap image) {
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;

		    for(int x = 0; x < image.getWidth(); x++)
		    {
		        for(int y = 0; y < image.getHeight(); y++)
		        {
		            if(image.getPixel(x, y) != Color.TRANSPARENT)
		            {
		                System.out.println("X1 is: " + x);
		                x1 = x;
		                break;
		            }
		        }

		        if(x1 != 0)
		            break;

		    }

		    for(int x = image.getWidth()-1; x > 0; x--)
		    {
		        for(int y = 0; y < image.getHeight(); y++)
		        {
		            if(image.getPixel(x, y) != Color.TRANSPARENT)
		            {
		                System.out.println("X2 is: " + x);
		                x2 = x;
		                break;
		            }
		        }

		        if(x2 != 0)
		            break;

		    }

		    for(int y = 0; y < image.getHeight(); y++)
		    {
		        for(int x = 0; x < image.getWidth(); x++)
		        {
		            if(image.getPixel(x, y) != Color.TRANSPARENT)
		            {
		                System.out.println("Y1 is: " + y);
		                y1 = y;
		                break;
		            }
		        }

		        if(y1 != 0)
		            break;

		    }

		    for(int y = image.getHeight()-1; y > 0; y--)
		    {
		        for(int x = 0; x < image.getWidth(); x++)
		        {
		            if(image.getPixel(x, y) != Color.TRANSPARENT)
		            {
		                System.out.println("Y2 is: " + y);
		                y2 = y;
		                break;
		            }
		        }

		        if(y2 != 0)
		            break;

		    }
		    Rect rect = new Rect(x1,y1,x2,y2);
		return rect;
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
		//rectovi = new Vector<Rect>();
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
                //mCanvas.drawBitmap(sviZaCrtat.elementAt(i), pozicije.elementAt(i).first, pozicije.elementAt(i).second,nist); // crtati sve rijeci
               // Rect kocka = new Rect(0,0,pozicije.elementAt(i).first,pozicije.elementAt(i).second);
                canvas.drawBitmap(sviZaCrtat.elementAt(i),pozicije.elementAt(i).first,pozicije.elementAt(i).second,nist); // crtati sve rijeci sto imamo
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