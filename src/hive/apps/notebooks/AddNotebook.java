package hive.apps.notebooks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddNotebook extends Activity {

	Button doneButton;
    Shelf shelfObject;
    EditText notebookName;
    TextView notebookNameDisplay;
    public static String actualNotebookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.addnotebook);
            notebookName = (EditText) findViewById(R.id.notebookNameId);
            shelfObject = new Shelf();
            final Button notebookcoverimg = (Button)findViewById(R.id.notebookcover);

            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            Spinner nbbgstyle = (Spinner) findViewById(R.id.notebookbgtype);
            ArrayAdapter<CharSequence> typeadapter = ArrayAdapter
                            .createFromResource(this, R.array.notebook_styles,
                                            android.R.layout.simple_spinner_item);
            typeadapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nbbgstyle.setAdapter(typeadapter);

            final Spinner notebookcovercolor = (Spinner) findViewById(R.id.notebookcovercolor);
            ArrayAdapter<CharSequence> coloradapter = ArrayAdapter
                            .createFromResource(this, R.array.notebook_cover_color,
                                            android.R.layout.simple_spinner_item);
            coloradapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            notebookcovercolor.setAdapter(coloradapter);

            
            final Map<String, Integer> White = new HashMap<String, Integer>();
            White.put("White", R.drawable.notebook_white);
            final Map<String, Integer> Grey = new HashMap<String, Integer>();
            
            notebookcovercolor
                            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                    int arg2, long arg3) {

                                            if (notebookcovercolor.getSelectedItem() == "White") {
                                                            notebookcoverimg.setBackgroundResource(White.get("White"));
                                                    Log.e("color", "White Selected");

                                            }

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                            // TODO Auto-generated method stub

                                    }

                            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.addnotebook, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
            case R.id.action_savenotebook:
                   
            		String tmpann;
	        		actualNotebookName=notebookName.getText().toString();
	        		tmpann=actualNotebookName;
	        		if(actualNotebookName.length()>8)
	        			actualNotebookName=actualNotebookName.substring(0, 5) + "...";
	        		
	        		if(actualNotebookName.length()==0)
	        		{
	        			Toast toast = Toast.makeText(this,
	                    R.string.error_empty_notebook_name, Toast.LENGTH_LONG);
	        			toast.show();
	        		}
	        		
	        		else
	        		{
	        			super.onBackPressed();
	                    Shelf.sveske.get(Shelf.ukupniSveskaCounter-1).setText(actualNotebookName);
	                    
	                    File notebooksRoot = new File(Environment.getExternalStorageDirectory()
	            				+ "/HIVE/Notebooks/"+tmpann);
	            		if (!notebooksRoot.exists()) {
	            			notebooksRoot.mkdirs();
	            		}
	        		}                   		
                            
                    return true;
            default:
                    return false;

            }

    }

}
