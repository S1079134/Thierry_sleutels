package com.hmkcode.thierry_sleutels;
/**
 * Created by Thierry Schouten on 3/12/2015.
 * IMTPMD
 * */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hmkcode.thierry_sleutels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;
import com.hmkcode.thierry_sleutels.Helpers.ClientHelper;
import com.hmkcode.thierry_sleutels.Models.InformatieSlotenBeknoptModel;
import com.hmkcode.thierry_sleutels.Models.InformatieSlotenModel;
import com.hmkcode.thierry_sleutels.Models.SlotenLijstModel;
import com.hmkcode.thierry_sleutels.Models.Settings;
import com.hmkcode.thierry_sleutels.Models.UserGegevensModel;


public class AanvraagActivity extends Activity  {

    // Vars
    TextView slotenTag,slotenInfo,headerText;
    EditText naam,adres,tel,mail;
    Button backButton;
    // Font awesome
    Typeface fontAwesome,customFont;
    // Get instances van de models
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel slotenLijstModel = SlotenLijstModel.getInstance();
    InformatieSlotenModel informatieSlotenModel = InformatieSlotenModel.getInstance();
    InformatieSlotenBeknoptModel informatieSlotenBeknoptModel = InformatieSlotenBeknoptModel.getInstance();
    UserGegevensModel userGegevensModel = UserGegevensModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aanvraag);

        // setup
        fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        customFont = Typeface.createFromAsset(getAssets(), "fonts/customfont.ttf");

        slotenTag = (TextView) findViewById(R.id.SlotenTextViewSlotenAanvraag);
        slotenInfo = (TextView) findViewById(R.id.slotenInfoAanvraag);
        headerText = (TextView) findViewById(R.id.headerTextAanvraag);

        headerText.setTypeface(customFont);

        naam = (EditText) findViewById(R.id.naamEdit);
        adres = (EditText) findViewById(R.id.adresEdit);
        tel = (EditText) findViewById(R.id.telEdit);
        mail = (EditText) findViewById(R.id.mailEdit);
        // set font awesome
        backButton = (Button) findViewById(R.id.backButtonAanvraag);
        backButton.setTypeface(fontAwesome);
        backButton.setText(R.string.fa_left);
        // Toon geselecteerde service
        slotenTag.setText(slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten()));
        // Tonen van beknopte info
        slotenInfo.setText(informatieSlotenBeknoptModel.getShortInfoSloten());
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

    // Get user gegevens uit de textfields met minimaal 3 karakter per veld
    public boolean getUserGegevens()
    {
        boolean compleet = true;
        if (naam.getText().length() > 3){
            userGegevensModel.setUserNaam(String.valueOf(naam.getText()));
            compleet = true;
        }
        else{
            Toast.makeText(this,"Voer uw Naam in",
                    Toast.LENGTH_SHORT).show();
            compleet = false;
        }

        if (adres.getText().length() > 3){
            userGegevensModel.setUserAdres(String.valueOf(adres.getText()));
            compleet = true;
        }
        else{
            Toast.makeText(this,"Voer uw Adres in",
                    Toast.LENGTH_SHORT).show();
            compleet = false;

        }

        if (tel.getText().length() > 3){
            userGegevensModel.setUserTel(String.valueOf(tel.getText()));
            compleet = true;
        }
        else{
            Toast.makeText(this,"Voer uw tel in",
                    Toast.LENGTH_SHORT).show();
            compleet = false;

        }

        if (mail.getText().length() > 3){
            userGegevensModel.setUserMail(String.valueOf(mail.getText()));
            compleet = true;
        }
        else{
            Toast.makeText(this,"Voer uw Email in",
                    Toast.LENGTH_SHORT).show();
            compleet = false;

        }
        return compleet;
    }

    // functie om met de button verder te gaan naar de volgende pagina en om de aanvraag naar server te verzenden mits er een connectie is
    public void aanvraag(View view){


        if (settingsData.getisOnline() == true && getUserGegevens() == true)
        {
            // Verzend de aanvraag
            getAanvraag();
        }
        // melding connectie probleem
        else{
            Toast.makeText(this,R.string.errorConnectionAanvraag,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Functie om de aanvraag te verwerken
    public void getAanvraag() {
        //aanmaken van een nieuw jsonobject
        JSONObject sendObject = new JSONObject();
        // Opbouwen van de te verzenden json array
        JSONArray koperInfoArray = new JSONArray();
        // Voeg de service naam toe
        JSONObject slotenNameObject = new JSONObject();
        try {
            slotenNameObject.put("slotnaam",slotenLijstModel.getSlotenLijst().get(slotenLijstModel.getSelectedSloten()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        koperInfoArray.put(slotenNameObject);

        // Voeg de koper data toe
        JSONObject koperObject = new JSONObject();
        try {
            koperObject.put("kopernaam",userGegevensModel.getUserNaam());
            koperObject.put("koperadres",userGegevensModel.getUserAdres());
            koperObject.put("kopertelnr",userGegevensModel.getUserTel());
            koperObject.put("koperemail",userGegevensModel.getUserMail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        koperInfoArray.put(koperObject);


        try {
            //verzenden van het jsonobject
            sendObject.put("aanvraag",koperInfoArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reactie = "string";
        {
            //servercommunicator proberen te verbinden met de server
            try {
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, sendObject.toString()).execute().get();

                aanvraagVoltooid(reactie);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }







    // Afhandelen van een succesvolle verzending
    public void aanvraagVoltooid(String string){
                Toast.makeText(this,R.string.aanvraagOntvangen,
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    // functie om met de button verder te gaan naar de vorige pagina.
    public void prevPage(View view){
        Intent intent = new Intent(this, SlotenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Load prev page
        Intent intent = new Intent(this, SlotenActivity.class);
        startActivity(intent);
        finish();
    }

}
