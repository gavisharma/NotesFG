package com.example.foram.notesfg;

public class Subject {

    public String s_name;
    public int s_id;

    public Subject(){
        this.s_name = "";
        this.s_id = 0;
    }

    public int getS_id() {
        return this.s_id;
    }

    public String getS_name() {
        return this.s_name;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }
}
