package com.hmkcode.thierry_sleutels;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.hmkcode.thierry_sleutels.Helpers.ClientHelper;
import com.hmkcode.thierry_sleutels.Models.InformatieSlotenModel;
import com.hmkcode.thierry_sleutels.Models.SlotenLijstModel;
import com.hmkcode.thierry_sleutels.Models.Settings;

/**
 * Created by Thierry Schouten on 3/17/2015.
 * IMTPMD
 * */
public class LoginActivity extends Activity {

    // vars
    EditText ipEdit;
    TextView textViewIP,loginHead;
    Button loginButton;
    Typeface customFont;

    // Instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel slotenLijstModel = SlotenLijstModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_scherm);
        // Setup
        ipEdit = (EditText)findViewById(R.id.editTextIP);
        textViewIP = (TextView) findViewById(R.id.textViewIP);
        loginHead  = (TextView) findViewById(R.id.loginHead);
        loginButton = (Button) findViewById(R.id.loginButton);

        customFont = Typeface.createFromAsset(getAssets(), "fonts/customfont.ttf");
        loginHead.setTypeface(customFont);
        ipEdit.setText(settingsData.getIp4Adress());
    }

    public void login(View view){

        // Proberen een connectie te maken het het ingevoerde IP adres
        String ip= String.valueOf(ipEdit.getText());
        settingsData.setIp4Adress(ip);

        settingsData.setOnline(true);

        // Boolean zetten adv een connectie of niet
        boolean connectionAvailable = getOnlineOffline();
        if(connectionAvailable == false){

            slotenLijstModel.setSlotenHardCoded();
            noConnectionMessage();
        }

        else if (connectionAvailable == true){

            getSloten();
            connectionMessage();
        }

        // next page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Services opvragen
    public void getSloten() {
        //aanmaken van een nieuw jsonobject
        JSONObject slotenObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            slotenObject.put("slotenlijst","");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reactie = "string";
        {
            //servercommunicator proberen te verbinden met de server
            try {
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, slotenObject.toString()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            JSONArray sloten = null;
            try {
                sloten = new JSONArray(reactie);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Clear array
            slotenLijstModel.clearSloten();

            // Vullen array
            for (int i = 0 ; i < sloten.length(); i++)
            {
                try {
                    JSONObject value = sloten.getJSONObject(i);

                    String valueString = value.getString("naam");
                    slotenLijstModel.addSloten(valueString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean getOnlineOffline() {
        //aanmaken van een nieuw jsonobject
        JSONObject slotenObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            slotenObject.put("slotenlijst","");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        {
            //servercommunicator proberen te verbinden met de server
            try {
                String reactie;
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, slotenObject.toString()).execute().get();

            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (ExecutionException e) {
                e.printStackTrace();

            }

        }

        return settingsData.getisOnline();
    }

    // Error msg
    public void noConnectionMessage(){

        Toast.makeText(this, R.string.errorConnection,
                Toast.LENGTH_LONG).show();
    }

    // Welkom msg
    public void connectionMessage(){

        Toast.makeText(this, R.string.connection,
                Toast.LENGTH_LONG).show();
    }


    public void setSlotenHardCoded(){

        ArrayList<String> sloten = new ArrayList<String>();
        sloten.add("Sleutel slotsystemen");
        sloten.add("RFID slotsystemen");
        sloten.add("Biometrische slotsystemen");

        slotenLijstModel.clearSloten();
        slotenLijstModel.setSlotenLijst(sloten);
    }

}


