package com.example.foram.notesfg;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note {

    public long dateTime, id, longitude, latitude, subID;
    public String title, content, image;

    public Note(Note note){
        this.id         = note.id;
        this.image      = note.image;
        this.subID      = note.subID;
        this.title      = note.title;
        this.content    = note.content;
        this.dateTime   = note.dateTime;
        this.latitude   = note.latitude;
        this.longitude  = note.longitude;
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
