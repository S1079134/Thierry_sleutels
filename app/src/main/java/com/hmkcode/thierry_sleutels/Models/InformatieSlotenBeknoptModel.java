package com.hmkcode.thierry_sleutels.Models;

/**
 * Created by Thierry on 17-3-2015.
 */
public class InformatieSlotenBeknoptModel {


    //singleton pattern
    private static InformatieSlotenBeknoptModel _instance;

    public static InformatieSlotenBeknoptModel getInstance() {
        if (_instance == null)
            _instance = new InformatieSlotenBeknoptModel();

        return _instance;
    }
    private String shortInfoSloten;

    public String getShortInfoSloten() {
        return shortInfoSloten;
    }

    public void setShortInfoSloten(String shortInfoSloten) {
        this.shortInfoSloten = shortInfoSloten;
    }

    public void setShortInfoSlotenHardCoded(String string){

        this.shortInfoSloten = string;

    }
}


