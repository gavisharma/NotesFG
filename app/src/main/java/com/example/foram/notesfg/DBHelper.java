package com.example.foram.notesfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "fg_Notes";
    final static String SUBJECT = "Subjects";
    final static String NOTE = "Notes";

    public static final String ID        = "ID";
    public static final String TITLE     = "TITLE";
    public static final String IMAGE     = "IMAGE";
    public static final String CONTENT   = "CONTENT";
    public static final String DATETIME  = "DATETIME";
    public static final String LATITUDE  = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //"Subject" table is created-----------------------------------------------------------------------------------------------------

        try{
            String SUBJECT_TABLE = "CREATE TABLE " + SUBJECT + " (ID NUMBER(10) PRIMARY KEY, NAME VARCHAR(100))";

            Log.v("DBHelper", SUBJECT_TABLE );

            db.execSQL(SUBJECT_TABLE);

        } catch (Exception e) {

            Log.e("DBHelper", e.getMessage());
        }


        //"Subject" table is populated-----------------------------------------------------------------------------------------------------

        try{
            String SUBJECT_TABLE_DATA = "INSERT INTO " + SUBJECT + " VALUES(1, 'C'), (2, 'C++'), (3, 'JAVA'), (4, 'SWIFT'), (5, 'PHP'), (6, 'JavaScript'), (7, 'ObjectiveC'), (8, 'ORACLE'), (9, 'Python'), (10, 'CSS')";

            Log.v("DBHelper", SUBJECT_TABLE_DATA );

            db.execSQL(SUBJECT_TABLE_DATA);

        } catch (Exception e) {

            Log.e("DBHelper", e.getMessage());
        }


        //"Note" table is created--------------------------------------------------------------------------------------------------------

        try{

            String NOTE_TABLE = "CREATE TABLE " + NOTE + " (ID NUMBER(10) PRIMARY KEY, CONTENT VARCHAR(2000), DATETIME NUMBER(40), TITLE VARCHAR(100), LATITUDE NUMBER(10), LONGITUDE NUMBER(10), IMAGE VARCHAR(20), SUB_ID NUMBER(10))";

            Log.v("DBHelper", NOTE_TABLE);

            db.execSQL(NOTE_TABLE);


        } catch (Exception e) {

            Log.e("DBHelper", e.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL("DROP TABLE IF EXISTS " + SUBJECT);
            db.execSQL("DROP TABLE IF EXISTS " + NOTE);

            onCreate(db);
        } catch (Exception e) {

            Log.e("DBHelper", e.getMessage());
        }

    }

    public void insertNote(String note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put();
    }
}
