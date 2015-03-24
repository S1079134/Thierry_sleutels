package com.hmkcode.thierry_sleutels.Models;

/**
 * Created by Thierry on 17-3-2015.
 */
public class UserGegevensModel {


    //singleton pattern
    private static UserGegevensModel _instance;

    public static UserGegevensModel getInstance() {
        if (_instance == null)
            _instance = new UserGegevensModel();

        return _instance;
    }
    private String userNaam;
    private String userAdres;
    private String userTel;
    private String userMail;

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserAdres() {
        return userAdres;
    }

    public void setUserAdres(String userAdres) {
        this.userAdres = userAdres;
    }

    public String getUserNaam() {
        return userNaam;
    }

    public void setUserNaam(String userNaam) {
        this.userNaam = userNaam;
    }



}
