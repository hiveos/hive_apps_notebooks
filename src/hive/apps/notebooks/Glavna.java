package hive.apps.notebooks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Glavna extends Activity implements OnClickListener {

	CrtanjeView cv;
	RelativeLayout ll;
	QuickAction qa;
	ImageView guideLines;
	Button enterButton, spaceButton, undoButton;
	public static String stil;
	public static String imeSveske;
	Vector<byte[]>niz=new Vector();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fullscreen();
		setContentView(R.layout.glavna);
		guideLines=(ImageView)findViewById(R.id.guide);
		
		ll = (RelativeLayout) findViewById(R.id.vGlavni);
		if(stil.equals("Grid")){
			ll.setBackgroundResource(R.drawable.texture_grid);
			guideLines.setVisibility(View.VISIBLE);
		}
		if(stil.equals("Lines")){
			ll.setBackgroundResource(R.drawable.texture);
			guideLines.setVisibility(View.VISIBLE);
		}
		if(stil.equals("Plain")){
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
		ucitajLokacije();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ucitajLokacije();
	}



	String uzmiEkstenziju(String element)
	{
		String filenameArray[] = element.split("\\.");
        String ekstenzija = filenameArray[filenameArray.length-1];
		return ekstenzija;
	}
	
	public void ucitajLokacije()
	{
		File fileSaRijecima = new File(Environment.getExternalStorageDirectory()
		        + "/HIVE/Notebooks/"+imeSveske+"/locations.txt");
		CrtanjeView.pozicije.clear();
		CrtanjeView.sviZaCrtat.clear();
		try {
			Scanner rd = new Scanner(fileSaRijecima);
			while (true)
			{
				if (!rd.hasNextInt())
				{
					rd.close();
					break;
				}
				int id = rd.nextInt();
				int x = rd.nextInt();
				int y = rd.nextInt();
				if (id == -1)
				{
					CrtanjeView.trenutnaLinija = x;
					CrtanjeView.trenutnaSirinaLinije = y;
					continue;
				}
					
				if (id >= CrtanjeView.pozicije.size())
				{
					for (int i=CrtanjeView.pozicije.size();i<=id;i++)
					{
						CrtanjeView.pozicije.add(new Pair<Integer, Integer>(0,0));
						CrtanjeView.sviZaCrtat.add(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
					}
				}
				CrtanjeView.pozicije.set(id, new Pair<Integer, Integer>(x,y));
            	Bitmap rijecZaUnijeti = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory()
        		        + "/HIVE/Notebooks/"+imeSveske+"/img"+id+".png").getAbsolutePath());
            	CrtanjeView.sviZaCrtat.set(id, rijecZaUnijeti);
            	//CrtanjeView.dodajScalovano(rijecZaUnijeti);
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

		  // we need to know how may bytes were read to write them to the byteBuffer
		  int len = 0;
		  while ((len = inputStream.read(buffer)) != -1) {
		    byteBuffer.write(buffer, 0, len);
		  }

		  // and then we can return your byte array.
		  return byteBuffer.toByteArray();
		}
	
	public Bitmap getBitmap(byte[] bitmap) {
	    return BitmapFactory.decodeByteArray(bitmap , 0, bitmap.length);
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
			if(CrtanjeView.writing) CrtanjeView.writing=false;
			else CrtanjeView.writing=true;
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
	
	public void spremiRijeci()
	{		
		for(int i=0; i<CrtanjeView.sviZaCrtat.size(); i++)
		{
			Bitmap bmp = CrtanjeView.sviZaCrtat.get(i);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			niz.add(byteArray);
		}
		

		File lokacije = new File(Environment.getExternalStorageDirectory()
                + "/HIVE/Notebooks/"+imeSveske+"/locations.txt");
		
		if(!lokacije.exists()){
			try {
				lokacije.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {

			FileWriter fw = new FileWriter(lokacije);
			for(int i=0; i<niz.size(); i++)
			{
				fw.append(i+" "+CrtanjeView.pozicije.get(i).first+" "+CrtanjeView.pozicije.get(i).second+"\n");
				File data = new File(Environment.getExternalStorageDirectory()
		                + "/HIVE/Notebooks/"+imeSveske+"/img"+i+".png");
				if(!data.exists()){
					try {
						data.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory()
				        + "/HIVE/Notebooks/"+imeSveske+"/img"+i+".png"));
				bos.write(niz.get(i));
				bos.flush();
				bos.close();
			}
			fw.append("-1 "+CrtanjeView.trenutnaLinija+" "+CrtanjeView.trenutnaSirinaLinije+"\n");
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