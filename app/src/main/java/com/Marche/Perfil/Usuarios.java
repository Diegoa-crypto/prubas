package com.Marche.Perfil;

public class Usuarios
{
    private String fName, fTelefono, email, image, userID, date, status;

    public Usuarios(){

    }


    public Usuarios(String fName, String fTelefono, String email, String image, String userID,String date,String status) {
        this.date = date;
        this.fName = fName;
        this.fTelefono = fTelefono;
        this.email = email;
        this.image = image;
        this.userID = userID;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfTelefono() {
        return fTelefono;
    }

    public void setfTelefono(String fTelefono) {
        this.fTelefono = fTelefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
