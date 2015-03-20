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
import com.hmkcode.thierry_sleutels.Helpers.ClientHelper;
import com.hmkcode.thierry_sleutels.Models.InformatieSlotenBeknoptModel;
import com.hmkcode.thierry_sleutels.Models.SlotenLijstModel;
import com.hmkcode.thierry_sleutels.Models.Settings;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    //Vars
    TextView bisInfo,slotenTag,slotenInfo,headerText;
    Button nextButton;
    Spinner spinner1;

    Typeface customFont,fontAwesome;

    // Instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel slotenLijstModel = SlotenLijstModel.getInstance();
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
        slotenTag = (TextView) findViewById(R.id.SlotenTextView);
        slotenInfo = (TextView) findViewById(R.id.SlotenInfoShort);
        headerText = (TextView) findViewById(R.id.headerText);

        nextButton = (Button) findViewById(R.id.nextButton);
        spinner1 = (Spinner) findViewById(R.id.spinner);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        customFont = Typeface.createFromAsset(getAssets(), "fonts/customfont.ttf");
        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        headerText.setTypeface(customFont);

        nextButton.setTypeface(fontAwesome);
        nextButton.setText(R.string.fa_right);

        // Inladen van de sloten Lijst
        ArrayList<String> list;
        list = slotenLijstModel.getSlotenLijst();

        // Menu spinner vullen met opgehaalde sloten lijst
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
            setSelectedSloten();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(Pos,pos);
            editor.commit();
        }

        slotenLijstModel.setSelectedSloten(pos);
        setSelectedSloten();
    }

    public void setSelectedSloten(){

        if (settingsData.getisOnline() == true) {
            // Ophalen van de beknopte informatie
            getSlotenInfoShort();
        }
        else{
            // Hardcoded short info array
            String hardCoded[] = new String[3];
            hardCoded[0] = "Eenvoudig, snel en goedkoop uw bezit achter slot en grendel!";
            hardCoded[1] = "Digitale beveiliging voor 100% zekerheid!";
            hardCoded[2] = "Authenticatie en authorizatie op persoonlijk niveau!";

            informatieSlotenBeknoptModel.setShortInfoSlotenHardCoded(hardCoded[slotenLijstModel.getSelectedSloten()]);
        }

        // Vullen van textveld welke een kopje is van de beknopte beschrijving
        slotenTag.setText(slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten()));
        slotenInfo.setText(informatieSlotenBeknoptModel.getShortInfoSloten());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // Functie om de beknopte informatie op te halen en het model te vullen
    public void getSlotenInfoShort() {
        //aanmaken van een nieuw jsonobject
        JSONObject infoObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            infoObject.put("informatiebeknopt",(slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten())));

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
