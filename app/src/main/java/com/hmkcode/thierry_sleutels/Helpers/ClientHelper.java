package com.hmkcode.thierry_sleutels.Helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hmkcode.thierry_sleutels.Models.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * Created by Thierry Schouten on 03/12/2015.
 * IMTPMD
 */
public class ClientHelper  extends AsyncTask<Void, Void, String>
{

    private String message;
    private String ip;
    private String reactie = null;
    private int port;
    private Context context;

    Settings settingsData = Settings.getInstance();

    public ClientHelper(Context context, String ip, int port, String message )
    {
        // gegevens welke de communicator nodig heeft.

        this.context = context;
        this.message = message;
        this.ip = ip;
        this.port = port;
    }

    //Verzend gegevens naar de server toe
    private void sendMessage( String message, Socket serverSocket )
    {
        OutputStreamWriter outputStreamWriter = null;

        try
        {
            outputStreamWriter = new OutputStreamWriter(serverSocket.getOutputStream());
        }

        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        if( outputStreamWriter != null )
        {

            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            PrintWriter writer = new PrintWriter( bufferedWriter, true );

            writer.println(message);
        }
    }


    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            Socket serverSocket = new Socket();
            serverSocket.connect( new InetSocketAddress( this.ip, this.port ), 4000 );

            //zend  bericht naar de server
            this.sendMessage(message, serverSocket);

            InputStream input;

            //Hierdoor krijg je een reactie van de server
            try
            {
                input = serverSocket.getInputStream();
                BufferedReader reactieStreamReader = new BufferedReader(new InputStreamReader(input));

                StringBuilder stringBouwer = new StringBuilder();

                int i =0;

                String line;

                while ((line = reactieStreamReader.readLine()) != null)
                {

                    if(i <= 0){

                    }
                    else{
                        stringBouwer.append(line);
                    }
                    i++;
                }
                reactieStreamReader.close();

                this.reactie = stringBouwer.toString();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        catch( UnknownHostException e )
        {
            Log.v("debug", "can't find host");
            // Boolean of gebruiker online of offline is
            settingsData.setOnline(false);
        }

        catch( SocketTimeoutException e )
        {
            Log.v("debug", "time-out");
            // Boolean of gebruiker online of offline is
            settingsData.setOnline(false);
        }

        catch (IOException e)
        {
            e.printStackTrace();
            settingsData.setOnline(false);
        }
        return reactie;
    }
}
