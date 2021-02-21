package com.castup.onlinenotes.Models;

public class InfoNotes {

    private String  title  ,subtitle , description , notedate , nameimage , pathvoice , namevoice , tag , refnote ,sender , fromsender ,pathimage , weburl , f , s , t ;

    public InfoNotes() {

    }

    public InfoNotes(String title, String subtitle, String description, String notedate, String nameimage, String pathvoice, String namevoice, String tag, String refnote, String sender, String fromsender, String pathimage, String weburl, String f, String s, String t) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.notedate = notedate;
        this.nameimage = nameimage;
        this.pathvoice = pathvoice;
        this.namevoice = namevoice;
        this.tag = tag;
        this.refnote = refnote;
        this.sender = sender;
        this.fromsender = fromsender;
        this.pathimage = pathimage;
        this.weburl = weburl;
        this.f = f;
        this.s = s;
        this.t = t;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotedate() {
        return notedate;
    }

    public void setNotedate(String notedate) {
        this.notedate = notedate;
    }

    public String getNameimage() {
        return nameimage;
    }

    public void setNameimage(String nameimage) {
        this.nameimage = nameimage;
    }

    public String getPathvoice() {
        return pathvoice;
    }

    public void setPathvoice(String pathvoice) {
        this.pathvoice = pathvoice;
    }

    public String getNamevoice() {
        return namevoice;
    }

    public void setNamevoice(String namevoice) {
        this.namevoice = namevoice;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRefnote() {
        return refnote;
    }

    public void setRefnote(String refnote) {
        this.refnote = refnote;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFromsender() {
        return fromsender;
    }

    public void setFromsender(String fromsender) {
        this.fromsender = fromsender;
    }

    public String getPathimage() {
        return pathimage;
    }

    public void setPathimage(String pathimage) {
        this.pathimage = pathimage;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

}
