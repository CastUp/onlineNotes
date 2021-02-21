package com.castup.onlinenotes.Models;

public class InfoTags {

    private String  F , S , T , NAMETAGS , REF;


    public InfoTags() {
    }

    public InfoTags(String f, String s, String t, String NAMETAGS, String REF) {
        F = f;
        S = s;
        T = t;
        this.NAMETAGS = NAMETAGS;
        this.REF = REF;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getNAMETAGS() {
        return NAMETAGS;
    }

    public void setNAMETAGS(String NAMETAGS) {
        this.NAMETAGS = NAMETAGS;
    }

    public String getREF() {
        return REF;
    }

    public void setREF(String REF) {
        this.REF = REF;
    }
}
