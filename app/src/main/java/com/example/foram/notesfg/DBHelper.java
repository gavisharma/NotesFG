package com.example.foram.notesfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "fg_Notes";
    final static String SUBJECT = "Subjects";
    final static String NOTE = "Notes";
    //C

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

            String NOTE_TABLE = "CREATE TABLE " + NOTE + " (ID NUMBER(10) PRIMARY KEY, CONTENT VARCHAR(2000), CREATIONDATE VARCHAR(40), TITLE VARCHAR(100), LATITUDE NUMBER(10), LONGITUDE NUMBER(10), IMAGE BLOB(200), SUB_ID NUMBER(10))";

            Log.v("DBHelper", NOTE_TABLE);

            db.execSQL(NOTE_TABLE);


        } catch (Exception e) {

            Log.e("DBHelper", e.getMessage());
        }

    }

    public long insertImage(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        byte[] buffer = out.toByteArray();

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;

        long id = 0;

        try{
            values = new ContentValues();
            values.put("img", buffer);

            id = db.insert(NOTE, null, values);
            db.setTransactionSuccessful();
            Log.i("Image..", "Inserted..");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return id;
    }

    public Bitmap getBitmap(int id){
        Bitmap bitmap = null;
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        try{
            String selectQuery = "SELECT * FROM " + NOTE + " WHERE id = " + id;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex(IMAGE));
                    bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

                }
                }
            db.setTransactionSuccessful();
            }catch (Exception e){

        } finally {
            db.endTransaction();
            db.close();
        }
        return bitmap;
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

}
