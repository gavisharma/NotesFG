package com.example.foram.notesfg;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Note {

    public long id, subID;
    public float longitude, latitude;
    public String title, content, image, creationDate;

    public Note(){
        this.id         = 1;
        this.image      = "";
        this.subID      = 1;
        this.title      = "";
        this.content    = "";
        this.creationDate = "";
        this.latitude   = 11.988989f;
        this.longitude  = 78.283792f;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return this.creationDate;
    }

    public void setDateTime(String dateTime) {
        this.creationDate = dateTime;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public long getSubID() { return subID; }

    public void setSubID(long subID) { this.subID = subID; }

    public float getLongitude() { return longitude; }

    public void setLongitude(float longitude) { this.longitude = longitude; }

    public float getLatitude() { return latitude; }

    public void setLatitude(float latitude) { this.latitude = latitude; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }
}
