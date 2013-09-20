//Klasa za prikazivanje dialoga za boju i debljinu i jo� neke sitnice :D

package hive.apps.notebooks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DialogAdapter {

	private Context kontekst;
	private SeekBar sb;
	private TextView tv;

	private int debljina = 50;

	RadioGroup rg;

	// U konstruktoru se traži kontekst, jer će nam trebat na par mjesta
	DialogAdapter(Context k) {
		kontekst = k;
	}


	//////// POSTAVLJANJE 1. DIALOGA (ZA DEBLJINU) ////////
	public void postaviDialogZaDebljinu() {
		final Dialog dialog = new Dialog(kontekst);

		// Stavljamo sadržaj u dialog. U ovom slučaju, to je dialogdebljina.xml koji se nalazi u /res/layout/ folderu
		dialog.setContentView(R.layout.dialogdebljina);

		dialog.setTitle("Choose size:");

		Button ok = (Button) dialog.findViewById(R.id.bDebljinaOK);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CrtanjeView.boja.setStrokeWidth((float) debljina);
				CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
		        if(CrtanjeView.writing) CrtanjeView.paths.add(CrtanjeView.putanja);
		        else CrtanjeView.drawingPaths.add(CrtanjeView.putanja);
				dialog.dismiss();
			}
		});

		// Bez ovoga, dialog bi bio postavljen, al ne i prikazan. To sam naučio na teži način :D
		dialog.show();

		tv = (TextView) dialog.findViewById(R.id.tvProgress);
		sb = (SeekBar) dialog.findViewById(R.id.sbDebljina);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Postavljamo u textview trenutnu vrijednost na klizaču
				tv.setText(Integer.toString(progress));
				// Postavljeno je da je debljina jednaka trenutnoj vrijednosti na klizaču
				debljina = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

	}

	//////// POSTAVLJANJE 2. DIALOGA (ZA BOJU) ////////
	public void postaviDialogZaBoju() {
		final Dialog dialog = new Dialog(kontekst);

		// Sada se poziva dialogboja.xml koji se isto nalazi u /res/layout/ folderu
		dialog.setContentView(R.layout.dialogboja);
		dialog.setTitle("Pick a colour");
		
		Button ok = (Button) dialog.findViewById(R.id.bBojaOK);

		final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rgRadio);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				int id = rg.getCheckedRadioButtonId();
				
				switch(id){
				case R.id.rbCrvena:
					CrtanjeView.boja.setColor(Color.parseColor("#960202"));
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        if(CrtanjeView.writing) CrtanjeView.paths.add(CrtanjeView.putanja);
			        else CrtanjeView.drawingPaths.add(CrtanjeView.putanja);
					break;
				case R.id.rbPlava:
					CrtanjeView.boja.setColor(Color.parseColor("#5a8cd0"));
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
					if(CrtanjeView.writing) CrtanjeView.paths.add(CrtanjeView.putanja);
			        else CrtanjeView.drawingPaths.add(CrtanjeView.putanja);
					break;
				case R.id.rbZelena:
					CrtanjeView.boja.setColor(Color.BLACK);
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
					if(CrtanjeView.writing) CrtanjeView.paths.add(CrtanjeView.putanja);
			        else CrtanjeView.drawingPaths.add(CrtanjeView.putanja);
					break;
				}
				dialog.dismiss();
			}
		});

		// Opet prikaz dialoga
		dialog.show();

	}

}
