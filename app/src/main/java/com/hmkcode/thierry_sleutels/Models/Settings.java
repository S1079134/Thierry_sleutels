package com.hmkcode.thierry_sleutels.Models;

/**
 * Created by Thierry on 17-3-2015.
 */
public class Settings {


    //singleton pattern
    private static Settings _instance;
    public static Settings getInstance()
    {
        if( _instance == null )
            _instance = new Settings();

        return _instance;
    }

    private boolean online = true;
    private String ip4Adress = "192.168.178.12";

    public String getIp4Adress()
    {
        return ip4Adress;
    }

    public void setIp4Adress(String ip4Adress) {
        this.ip4Adress = ip4Adress;
    }

    public boolean getisOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
