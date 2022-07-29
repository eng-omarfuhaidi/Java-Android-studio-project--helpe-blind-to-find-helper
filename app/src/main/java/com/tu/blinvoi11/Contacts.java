package com.tu.blinvoi11;

public class Contacts
{
    String name,Image,status,uid;

    public Contacts() {
    }

    public Contacts(String name, String image, String status, String uid) {
        this.name = name;
        Image = image;
        this.status = status;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return Image;
    }

    public String getStatus() {
        return status;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
