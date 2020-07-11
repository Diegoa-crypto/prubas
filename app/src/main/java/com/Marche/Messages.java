package com.Marche;

public class Messages
{
    public String data, time, type, message, from, para, pname,pimage;
    private boolean visto;

    public Messages(){

    }

    public Messages(String data, String time, String type, String message, String from,String para, String pimage,boolean visto ) {
        this.pimage=pimage;
        this.pname=pname;
        this.data = data;
        this.para = para;
        this.time = time;
        this.type = type;
        this.message = message;
        this.from = from;
        this.visto = visto;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
