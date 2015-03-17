package com.hmkcode.thierry_sleutels;

/**
 * Created by Thierry on 17-3-2015.
 */
import android.app.Activity;
import android.content.Intent;
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
import java.util.List;
import java.util.concurrent.ExecutionException;




public class SlotenActivity extends Activity  {

    // vars
    TextView SlotenTag,SlotenInfo,headerText;

    // instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel SlotenLijstModel = SlotenLijstModel.getInstance();
    InformatieSlotenModel informatieSlotenModel = InformatieSlotenModel.getInstance();

    Typeface customFont,fontAwesome;
    Button backButton,nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sloten);

        // setup
        customFont = Typeface.createFromAsset(getAssets(), "fonts/customfont.ttf");
        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");

        SlotenTag = (TextView) findViewById(R.id.SlotenTextViewSloten);
        SlotenInfo = (TextView) findViewById(R.id.SlotenInfoLong);
        headerText = (TextView) findViewById(R.id.Slotenhead);
        backButton = (Button) findViewById(R.id.backButtonSloten);
        nextButton = (Button) findViewById(R.id.nextButtonSloten);

        headerText.setTypeface(customFont);
        backButton.setTypeface(fontAwesome);
        nextButton.setTypeface(fontAwesome);

        backButton.setText(R.string.fa_left);
        nextButton.setText(R.string.fa_right);


        headerText.setTypeface(customFont);

        // Checken of er verbinding is, anders hardcoded data laden
        if(settingsData.getisOnline() == true){
            getSlotenInfoLong();
        }
        else{
            // Hardcoded  info array
            String hardCoded[] = new String[3];
            hardCoded[0] ="Iedereen vindt het niets meer dan normaal dat als u de WC doorspoelt het water netjes het riool instroomt. Maar wat als dit nu eens niet gebeurt? U spoelt het toilet door maar het water komt omhoog in de WC pot of het water blijft staan? Neem ook hiervoor met spoed contact op met onze loodgieters!";
            hardCoded[1] = "Reparatie nodig aan uw dak? Veelal hoeft het dak niet te worden vervangen maar kan deze gewoon nog worden gerepareerd. In sommige gevallen is het echter goedkoper om het geheel te vervangen. In beide situaties brengen we graag advies en een vrijblijvende offerte uit." ;
            hardCoded[2] = "Is uw prinses gevangen genomen door een plaag Goombas? Kunt u maar niet het juiste kasteel vinden? Wij zijn gespecialiseerd in het vinden van gevangen genomen prinsessen. Dankzij onze uitgebreide ervaring van het riolering netwerk kunnen wij snel ter plaatse zijn.";

            informatieSlotenModel.setInfoSlotenHardCoded(hardCoded[SlotenLijstModel.getSelectedSloten()]);
        }

        SlotenInfo.setText(informatieSlotenModel.getinfoSloten());
        SlotenTag.setText(SlotenLijstModel.getSlotenLijst().get(SlotenLijstModel.getSelectedSloten()));
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

    // Functie om de  informatie op te halen en het model te vullen
    public void getSlotenInfoLong() {
        //aanmaken van een nieuw jsonobject
        JSONObject infoObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            infoObject.put("informatie",(SlotenLijstModel.getSlotenLijst().get(SlotenLijstModel.getSelectedSloten())));

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
                JSONObject info = new JSONObject(reactie);

                String infoString = info.getString("informatie");

                informatieSlotenModel.setinfoSloten(infoString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // functie om met de button verder te gaan naar de volgende pagina.
    public void nextPage(View view){

        Intent intent = new Intent(this, AanvraagActivity.class);
        startActivity(intent);
        finish();
    }

    // functie om met de button verder te gaan naar de vorige pagina.
    public void prevPage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Load prev page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}