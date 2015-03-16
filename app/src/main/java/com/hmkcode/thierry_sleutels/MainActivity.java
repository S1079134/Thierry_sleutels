package com.hmkcode.thierry_sleutels;

/**
 * Created by Thierry Schouten on 3/16/2015.
 * IMTPMD HSLEIDEN
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;



public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    //Vars
    TextView bisInfo,SlotenTag,SlotenInfo,headerText;
    Button nextButton;
    Spinner spinner1;

    Typeface customFont,fontAwesome;

    // Instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel SlotenLijstModel = SlotenLijstModel.getInstance();
    InformatieSlotenBeknoptModel informatieSlotenBeknoptModel = InformatieSlotenBeknoptModel.getInstance();

    // Opgeslaan mogelijk maken
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefsss" ;
    public static final String Pos = "posKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setup
        bisInfo = (TextView) findViewById(R.id.bisInfoTextView);
        SlotenTag = (TextView) findViewById(R.id.SlotenTextView);
        SlotenInfo = (TextView) findViewById(R.id.SlotensInfoShort);
        headerText = (TextView) findViewById(R.id.headerText);

        nextButton = (Button) findViewById(R.id.nextButton);
        spinner1 = (Spinner) findViewById(R.id.spinner);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        customFont = Typeface.createFromAsset(getAssets(), "fonts/customfont.ttf");
        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        headerText.setTypeface(customFont);

        nextButton.setTypeface(fontAwesome);
        nextButton.setText(R.string.fa_right);

        // Inladen van de Sloten Lijst
        ArrayList<String> list;
        list = SlotenLijstModel.getSlotensLijst();

        // Menu spinner vullen met opgehaalde Slotens lijst
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, R.layout.spinneritem, list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        // Spinner item selection Listener
        addListenerOnSpinnerItemSelection();
    }
    // Add spinner data
    public void addListenerOnSpinnerItemSelection() {
        spinner1.setOnItemSelectedListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int i =0;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        int savedPosition = sharedpreferences.getInt(Pos,0);

// Functie wordt automatisch 1 keer aangeroepen bij eht starten van de activity, vandaar de eerste keer afvangen en de sharedpref laden
        if (i<1){
            i++;
            spinner1.setSelection(savedPosition);
            pos = savedPosition;
        }
        // Gebeurd na de eerste keer laden
        else{
            setSelectedSlotens();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(Pos,pos);
            editor.commit();
        }

        SlotenLijstModel.setSelectedSloten(pos);
        setSelectedSlotens();
    }

    public void setSelectedSlotens(){

        if (settingsData.getisOnline() == true) {
            // Ophalen van de beknopte informatie
            getSlotensInfoShort();
        }
        else{
            // Hardcoded short info array
            String hardCoded[] = new String[3];
            hardCoded[0] = "Uw toiletproblemen in mum van tijd verholpen!";
            hardCoded[1] = "Valt alles in het water? Wij helpen u uit de brand!";
            hardCoded[2] = "Wij vinden het juiste kasteel voor u!";

            informatieSlotenBeknoptModel.setShortInfoSlotenHardCoded(hardCoded[SlotenLijstModel.getSelectedSloten()]);
        }

        // Vullen van textveld welke een kopje is van de beknopte beschrijving
        SlotenTag.setText(SlotenLijstModel.getSlotensLijst().get(SlotenLijstModel.getSelectedSloten()));
        SlotenInfo.setText(informatieSlotenBeknoptModel.getShortInfoSloten());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Functie om de beknopte informatie op te halen en het model te vullen
    public void getSlotensInfoShort() {
        //aanmaken van een nieuw jsonobject
        JSONObject infoObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            infoObject.put("informatiebeknopt",(SlotenLijstModel.getSlotensLijst().get(SlotenLijstModel.getSelectedSloten())));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reactie = "string";
        {
            //servercommunicator proberen te verbinden met de server
            try {
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, infoObject.toString()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try {
                JSONObject shortInfo = new JSONObject(reactie);

                String shortInfoString = shortInfo.getString("informatiebeknopt");

                informatieSlotenBeknoptModel.setShortInfoSloten(shortInfoString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // functie om met de button verder te gaan naar de volgende pagina.
    public void goToSlotenPage(View view){

        // Load next page
        Intent intent = new Intent(this, SlotenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Load next page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
