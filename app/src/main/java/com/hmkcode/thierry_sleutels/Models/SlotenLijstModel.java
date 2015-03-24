package com.hmkcode.thierry_sleutels.Models;

import java.util.ArrayList;

/**
 * Created by Thierry on 17-3-2015.
 */
public class SlotenLijstModel {

    //singleton pattern
    private static SlotenLijstModel _instance;

    public static SlotenLijstModel getInstance() {
        if (_instance == null)
            _instance = new SlotenLijstModel();

        return _instance;
    }

    private ArrayList<String> slotenLijst = new ArrayList<String>();

    private int selectedSloten;

    public void setSlotenLijst(ArrayList<String> slotenLijst) {
        this.slotenLijst = slotenLijst;
    }

    public ArrayList<String> getSlotenLijst() {
        return slotenLijst;
    }

    public void addSloten(String string){
        slotenLijst.add(string);
    }

    public void clearSloten(){
        slotenLijst.clear();
    }

    public void setSlotenHardCoded(){
        slotenLijst.clear();
        slotenLijst.add("Sleutel slotsystemen");
        slotenLijst.add("RFID slotsystemen");
        slotenLijst.add("Biometrische slotsystemen");    }

    public int getSelectedSloten() {
        return selectedSloten;
    }

    public void setSelectedSloten(int selectedSloten) {
        this.selectedSloten = selectedSloten;
    }
}