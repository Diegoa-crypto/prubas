package com.Marche.Perfil;

public class Usuarios
{
    private String fName, fTelefono, email, image;

    public Usuarios(){

    }

    public Usuarios(String fName, String fTelefono, String email, String image, String address) {
        this.fName = fName;
        this.fTelefono = fTelefono;
        this.email = email;
        this.image = image;
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


}