package com.abdelfattahrabou.contacts_app;

public class Contact {
    private int id;
    private String imei;
    private String name;
    private String telephone;

    public Contact() {

    }

    public int getId() {
        return id;
    }

    public String getImei() {
        return imei;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Contact(int id, String imei, String name, String telephone) {
        this.id = id;
        this.imei = imei;
        this.name = name;
        this.telephone = telephone;
    }
}
