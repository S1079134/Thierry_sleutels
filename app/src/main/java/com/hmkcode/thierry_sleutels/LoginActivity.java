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



/**
 * Created by Thierry Schouten on 3/16/2015.
 * IMTPMD HSLEIDEN
 */
public class LoginActivity extends Activity {

    // vars
    EditText ipEdit;
    TextView textViewIP,loginHead;
    Button loginButton;
    Typeface customFont;

    // Instances
    Settings settingsData = Settings.getInstance();
    SlotenLijstModel SlotenLijstModel = SlotenLijstModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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

            SlotenLijstModel.setSlotenHardCoded();
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

    // Sloten opvragen
    public void getSloten() {
        //aanmaken van een nieuw jsonobject
        JSONObject SlotenObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            SlotenObject.put("Slotenlijst","");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String reactie = "string";
        {
            //servercommunicator proberen te verbinden met de server
            try {
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, SlotenObject.toString()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            JSONArray Sloten = null;
            try {
                Sloten = new JSONArray(reactie);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Clear array
            SlotenLijstModel.clearSloten();

            // Vullen array
            for (int i = 0 ; i < Sloten.length(); i++)
            {
                try {
                    JSONObject value = Sloten.getJSONObject(i);

                    String valueString = value.getString("naam");
                    SlotenLijstModel.addSloten(valueString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean getOnlineOffline() {
        //aanmaken van een nieuw jsonobject
        JSONObject SlotenObject = new JSONObject();

        try {
            //verzenden van het jsonobject
            SlotenObject.put("Slotenlijst","");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        {
            //servercommunicator proberen te verbinden met de server
            try {
                String reactie;
                reactie = new ClientHelper(this, settingsData.getIp4Adress(), 4444, SlotenObject.toString()).execute().get();

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

        ArrayList<String> Sloten = new ArrayList<String>();
        Sloten.add("Sleutel slotsystemen");
        Sloten.add("RFID slotsystemen");
        Sloten.add("Biometrische slotsystemen");

        SlotenLijstModel.clearSloten();
        SlotenLijstModel.setSlotenLijst(Sloten);
    }

}


