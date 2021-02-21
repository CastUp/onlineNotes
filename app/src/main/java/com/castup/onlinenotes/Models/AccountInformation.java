package com.castup.onlinenotes.Models;

public class AccountInformation {

    private String NAME , UID , EMAIL , PHOTO ;

    public AccountInformation() {
    }

    public AccountInformation(String NAME, String UID, String EMAIL, String PHOTO) {
        this.NAME = NAME;
        this.UID = UID;
        this.EMAIL = EMAIL;
        this.PHOTO = PHOTO;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPHOTO() {
        return PHOTO;
    }

    public void setPHOTO(String PHOTO) {
        this.PHOTO = PHOTO;
    }
}
