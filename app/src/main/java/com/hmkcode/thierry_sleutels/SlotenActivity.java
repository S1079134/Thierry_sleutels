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
import android.widget.Button;
import android.widget.TextView;

import com.hmkcode.thierry_sleutels.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import com.hmkcode.thierry_sleutels.Helpers.ClientHelper;
import com.hmkcode.thierry_sleutels.Models.InformatieSlotenModel;
import com.hmkcode.thierry_sleutels.Models.SlotenLijstModel;
import com.hmkcode.thierry_sleutels.Models.Settings;


public class SlotenActivity extends Activity  {

    // variablen
    TextView slotenTag,slotenInfo,headerText;

    // instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel slotenLijstModel = SlotenLijstModel.getInstance();
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

        slotenTag = (TextView) findViewById(R.id.slotenTextViewsloten);
        slotenInfo = (TextView) findViewById(R.id.SlotenInfoLong);
        headerText = (TextView) findViewById(R.id.slotenhead);
        backButton = (Button) findViewById(R.id.backButtonsloten);
        nextButton = (Button) findViewById(R.id.nextButtonsloten);

        headerText.setTypeface(customFont);
        backButton.setTypeface(fontAwesome);
        nextButton.setTypeface(fontAwesome);

        backButton.setText(R.string.fa_left);
        nextButton.setText(R.string.fa_right);


        headerText.setTypeface(customFont);

        // controleert of er live verbinding is met de server, zo niet dan worden de gegevens uit de app gehaald. LET OP data kan verouderd zijn.
        if(settingsData.getisOnline() == true){
            getSlotenInfoLong();
        }
        else{
            // Hardcoded  info array
            String hardCoded[] = new String[3];
            hardCoded[0] = "De meest eenvoudige vorm van beveiliging zijn sloten die per sleutel geopend kunnen worden. Dit is een relatief goedkoop systeem maar brengt enige veiligheidsproblemen met zich mee. Voor beveiliging van ruimtes die weinig waardevolle zaken bevatten is dit systeem een uitstekende uitkomst.";
            hardCoded[1] = "RFID authenticatie is met de huidige techniek snel en gemakkelijk te implementeren om elke ruimte af te sluiten. Dankzij de digitalisering kunnen gebruikers getraceerd worden en bestaat de mogelijkheid een geschiedenis van ruimtegebruik aan te leggen. Dit resulteert in verhoogde beveiliging." ;
            hardCoded[2] = "Biometrische authenticatie en authorizatie is de meest geavanceerde vorm van beveiliging. Dit systeem werkt nagenoeg feilloos en garandeerd een zeer hoge veiligheid van uw waardevolle bezittingen. Biometrische authorizatie kan op verschillende niveaus worden toegepast, van vingerafdruk tot irisscan.";

            informatieSlotenModel.setInfoSlotenHardCoded(hardCoded[slotenLijstModel.getSelectedSloten()]);
        }

        slotenInfo.setText(informatieSlotenModel.getinfoSloten());
        slotenTag.setText(slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten()));
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

    // Haalt  informatie op en vult model met info
    public void getSlotenInfoLong() {
        //nieuw  jsonobject aanmaken
        JSONObject infoObject = new JSONObject();

        try {
            //verzenden van het jsonobject informatie
            infoObject.put("informatie",(slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten())));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reactie = "string";
        {
            //verbinding maken met de server
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

    //hiermee gaat men een pagina terug of verder
    public void nextPage(View view){

        Intent intent = new Intent(this, AanvraagActivity.class);
        startActivity(intent);
        finish();
    }

    //hiermee gaat men een pagina terug of verder
    public void prevPage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // laad vorige pagina
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
