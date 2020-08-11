package com.Marche.Perfil;

public class Chatlist {
    public String id;
    public String deleted;

    public Chatlist(){

    }

    public Chatlist(String id, String deleted) {
        this.deleted=deleted;
        this.id = id;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
