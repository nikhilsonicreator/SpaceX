package com.entwickler.spacex.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PersonRoom {

    @PrimaryKey(autoGenerate =  true)
    int personId;
    String name;
    String agency;
    String wikipedia;
    String image;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    byte[] img;
    String status;

    public PersonRoom(String name, String agency, String wikipedia,String image,byte[] img, String status) {
        this.name = name;
        this.agency = agency;
        this.wikipedia = wikipedia;
        this.image = image;
        this.img = img;
        this.status = status;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
