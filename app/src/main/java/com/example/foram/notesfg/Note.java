package com.example.foram.notesfg;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note {

    public long dateTime, id, subID;
    public float longitude, latitude;
    public String title, content, image;

    public Note(){
        this.id         = 1;
        this.image      = "";
        this.subID      = 1;
        this.title      = "";
        this.content    = "";
        this.dateTime   = 111111;
        this.latitude   = 11.988989f;
        this.longitude  = 78.283792f;
    }

    public long getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
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

    public  String dateTimeFormat(Context context){
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yy HH:mm:ss", context.getResources().getConfiguration().locale);
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date(this.dateTime));
    }
}
