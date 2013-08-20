package primjer.crtanje;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;

public class QuickAction implements OnTouchListener {
	private GridView gv;
	private View triggerView;
	private boolean boja = false, debljina = false; // s ovim �emo varijablama vidjet jesu li se boja ili debljina mijenjali
	private PopupWindow prozor;
	protected final WindowManager upravljacProzora;
	private DialogAdapter dialog;

	public String[] imena = { "Size", "Colour", "Eraser" };
	
	public boolean isBojaMijenjana(){
		return boja;
	}
	
	public boolean isDebljinaMijenjana(){
		return debljina;
	}

	public QuickAction(View triggerView) {
		// Ovaj triggerView nam služi da znamo iz kojeg Viewa je ovaj konstruktor pozvan. To nam treba jer ćemo par put koristit kontekst tog View-a
		this.triggerView = triggerView;

		// Treba kreirat GridView (samo preko jave, da se ne gomilaju xml fajlovi bezveze). U njega se kasnije dodaju gumbi
		gv = new GridView(triggerView.getContext());

		// Ovo nam služi za kreiranje tzv. dialoga koji iskače kad nam zatreba
		dialog = new DialogAdapter(triggerView.getContext());

		// Prvo treba postavit grid, a zatim dodat elemente u mrežu. To radimo s ove dvije metode:
		postaviGrid();
		dodajUMrezu();

		// Ovaj prozor nam služi da u njega umetnemo grid. To je taj tzv. QuickAction prozor. On "lebdi" iznad View-a
		prozor = new PopupWindow(triggerView.getContext());

		//Naravno, mora bit touchable prozor inače je beskoristan
		prozor.setTouchable(true);

		// Ovo nisam siguran čemu služi :(
		prozor.setTouchInterceptor(this);

		// Ovo nam je potrebno da bi popup mogao biti prikazan. Treba tražit tzv. System Service
		upravljacProzora = (WindowManager) triggerView.getContext()
				.getSystemService(Context.WINDOW_SERVICE);

		// Sada se dodaje nešto u taj popup. Argument mora biti View ili neko njegovo dijete. Mogli smo stavit tako button, sliku, textview, layout itd... Nama je trebao gridview u kojem se nalaze buttoni		
		prozor.setContentView(gv);

		// Naravno, potrebno je definirati visinu i širinu prozora
		prozor.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		prozor.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		// Malo failam, al eto, ponovo sam napisao touchable xD
		prozor.setTouchable(true);

		// Treba biti i omogućeno da se može određeni element u prozoru fokusirat. Kad ne bi bilo tog, mogli bi klikati po prozoru, al ne i unutar grida
		prozor.setFocusable(true);

		// Omogućeno je i da se dira izvan prozora, jer kad se onda izvan prozora dira, prozor nestane
		prozor.setOutsideTouchable(true);
	}
	
	public int getDebljina(){
		return dialog.dobijDebljinu();
	}
	
	// Naziv metode govori sve. Postavljaju se parametri za grid...
	private void postaviGrid() {
		gv.setId(696969);
		// Širina i visina grida (wrap content na obje)
		gv.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		// Pozadinska boja
		gv.setBackgroundColor(color.background_light);

		// Broj kolona
		gv.setNumColumns(3);

		// Širina pojedinog stupca
		gv.setColumnWidth(GridView.AUTO_FIT);

		// Količina "praznog mjesta" vertikalno i horizontalno
		gv.setVerticalSpacing(5);
		gv.setHorizontalSpacing(5);

		// Širenje grida namješteno je da se širi po stupcima
		gv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gv.setGravity(Gravity.CENTER);
	}

	private void dodajUMrezu(){

		// Postavlja se adapter s elementima View-a (u našem slučaju gumbima) koji se dodaje u grid. Tu trebamo kao argument stavit kontekst View-a na kojem se grid nalazi. Ovdje nam zato treba onaj triggerView
		gv.setAdapter(new ButtonAdapter(triggerView.getContext()));

		// Sad se treba postavljati click listener za gumbe u gridu.
		gv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int pozicija, long id){
				switch (v.getId()){
				case 0:
					// Ako je odabran gumb na 0. poziciji, onda se prikazuje dialog za debljinu
					dialog.postaviDialogZaDebljinu();
					debljina = true;
					break;
				case 1:
					// Ako je odabran na 1. poziciji, onda se prikazuje dialog za boju
					dialog.postaviDialogZaBoju();
					boja = true;
					break;
				case 2:
					// Ako je odabran button na 2. poziciji, onda se dobija gumica
					CrtanjeView.boja.setMaskFilter(null);
					CrtanjeView.boja.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
					CrtanjeView.boja.setAlpha(0xFF);//transperent color
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        CrtanjeView.paths.add(CrtanjeView.putanja);
					prozor.dismiss();
			        break;
				}
				
			}
		});
	}

	// U slučaju da se klikne izvan grida, grid se zatvara
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			this.prozor.dismiss();
			return true;
		}
		return false;
	}


	// Pokazuje se grid
	public void pokazi() {
		int[] lokacija = new int[2];
		triggerView.getLocationOnScreen(lokacija);
		// Ovaj dio nisam siguran što radi, al grid ne radi bez njega. Možda se ovdje definira gdje će se grid pojavit :/ ????????
		prozor.showAtLocation(triggerView, Gravity.FILL,
				lokacija[0] + 50, lokacija[1] + (triggerView.getHeight() / 2));
	}


	// Unutarnja klasa koja služi da ubacimo buttone u grid. To se nažalost ne može radit tako direktno, već se mora preko adaptera (bar je tako bilo u svim tutorijalima na koje sam ja naišao)
	public class ButtonAdapter extends BaseAdapter {

		private Context kontekst;

		public ButtonAdapter(Context c) {
			kontekst = c;
		}

		// Ove sve metode ispod su bile obavezne za uključit jer se extendao BaseAdapter. Ustvari nam treba samo getView i EVENTUALNO getCount, al ove dvije su nam beskorisne. Ali opet se moralo uključit :D
		@Override
		public int getCount() {
			return imena.length;
		}

		@Override
		public Object getItem(int pozicija) {
			return pozicija;
		}

		@Override
		public long getItemId(int pozicija) {
			return pozicija;
		}

		// Ovdje se dodaju buttoni u grid
		@Override
		public View getView(int pozicija, View convertView, ViewGroup parent) {
			Button gumb;
			// Nisam siguran čemu služi ovaj convertView, al ugl. stalno je ovaj if zadovoljen koliko sam skužio... :/
			if (convertView == null) {
				// Naravno, kreira se novi button
				gumb = new Button(kontekst);

				// Dodaju mu se parametri (širina i visina) za unutar grida
				gumb.setLayoutParams(new GridView.LayoutParams(130, 70));

				// Dodaje mu se i padding, da ne bude sve nabijeno
				gumb.setPadding(4, 4, 4, 4);

				// Širina i visina konkretno za pojedini button
				gumb.setWidth(LayoutParams.WRAP_CONTENT);
				gumb.setHeight(LayoutParams.WRAP_CONTENT);

				// Ovog puta ne smiju biti ni focusable ni clickable. Malo neintuitivno, al inače ne radi :/
				gumb.setFocusable(false);
				gumb.setClickable(false);
			} else {
				gumb = (Button) convertView;
			}

			// Treba se postavit ID za gumb. Trebat će nam kasnije
			gumb.setId(pozicija);

			// Naravno, treba i tekst gumba postavit i boju
			gumb.setText(imena[pozicija]);
			gumb.setTextColor(Color.WHITE);

			// Opet vidim da sam napravio duplikat :D
			gumb.setId(pozicija);

			// Budući da je metoda tipa View, treba joj vratiti View element (u ovom slučaju gumb)
			return gumb;
		}

	}
}
