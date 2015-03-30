package com.hmkcode.thierry_sleutels.Models;

/**
 * Created by Thierry on 17-3-2015.
 */
public class InformatieSlotenModel {
    //singleton pattern
    private static InformatieSlotenModel _instance;

    public static InformatieSlotenModel getInstance() {
        if (_instance == null)
            _instance = new InformatieSlotenModel();

        return _instance;
    }
    private String infoSloten;

    public String getinfoSloten() {
        return infoSloten;
    }

    public void setinfoSloten(String infoSloten) {
        this.infoSloten = infoSloten;
    }

    public void setInfoSlotenHardCoded(String string){

        this.infoSloten = string;
    }
}