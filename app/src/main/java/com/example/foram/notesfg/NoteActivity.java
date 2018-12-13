package com.example.foram.notesfg;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.foram.notesfg.DBHelper.NOTE;

public class NoteActivity extends AppCompatActivity {



    EditText note_title;
    TextView content_text;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    int subjectID, maxNoteID, displayNoteID;
    String viewType;
    Note selectedNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        note_title = findViewById(R.id.note_title);
        content_text = findViewById(R.id.note_content);
        subjectID = getIntent().getIntExtra("SubjectID", 1);
        displayNoteID = Integer.parseInt(getIntent().getStringExtra("Note_ID"));
//        displayNoteID = getIntent().getIntExtra("Note_ID", 1);
        viewType = getIntent().getStringExtra("ViewType");
        dbHelper = new DBHelper(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        if (viewType.equalsIgnoreCase("displayNote")){
            selectedNote = getNoteFromDatabaseForID(displayNoteID);
            note_title.setText(String.valueOf(selectedNote.title));
            content_text.setText(String.valueOf(selectedNote.content));
        } else if (viewType.equalsIgnoreCase("createNote")){
            maxNoteID = getMaxID();
            Log.v("Max Note ID: ", String.valueOf(maxNoteID));
        }
    }

    private int getMaxID(){
        int mx=-1;
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT MAX(ID) FROM NOTES", null);
            if (cursor != null)
                if(cursor.moveToFirst()) {
                    mx = cursor.getInt(0) + 1;
                }
            return mx;
        }
        catch(Exception e){
            return -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_note, menu);
        return true;
    }

    //SaveNote

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Note note = new Note();
                note.id = maxNoteID;
                note.title = String.valueOf(note_title.getText());
                note.content = String.valueOf(content_text.getText());
                saveNote(note);
//                Intent homeActivity = new Intent(this, HomeActivity.class);
//                startActivity(homeActivity);

                break;
        }
        return true;
    }




    public Note getNoteFromDatabaseForID(int noteID){
            try{
                sqLiteDatabase = dbHelper.getReadableDatabase();
                String columns[] = {"ID", "CONTENT", "TITLE"};
                String userData[] = {String.valueOf(noteID)};
                Note note = new Note();
                Cursor cursor = sqLiteDatabase.query("NOTES", columns, "ID = ?", userData, null, null, null);
//                Cursor cursor = sqLiteDatabase.query("NOTES", columns,"ID = ?", userData,null,null,null);
                while (cursor.moveToNext()){
                    note.id = noteID;
                    note.title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    note.content = cursor.getString(cursor.getColumnIndex("CONTENT"));
//                    note.longitude = cursor.getFloat(cursor.getColumnIndex("LONGITUDE"));
//                    note.latitude = cursor.getFloat(cursor.getColumnIndex("LATITUDE"));
//                    note.image = cursor.getString(cursor.getColumnIndex("IMAGE"));
//                    note.creationDate = cursor.getString(cursor.getColumnIndex("CREATIONDATE"));
                }
//                "CREATIONDATE", "TITLE", "LATITUDE", "LONGITUDE", "IMAGE", "SUB_ID"

//                if(cursor != null){
//                    if (cursor.getCount() > 0){
//                        note.id = noteID;
//                        note.title = cursor.getString(cursor.getColumnIndex("TITLE"));
//                        note.content = cursor.getString(cursor.getColumnIndex("CONTENT"));
//                        return note;
//                    }
//                }
                return note;


            }catch(Exception e){
                Log.e("LoginActivity", e.getMessage());
                return null;
            }finally {
                sqLiteDatabase.close();
            }
    }


    public  String dateTimeFormat(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a", Locale.CANADA);
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    //saveNote main loop
    public void saveNote(Note note){
//        if (viewType.equalsIgnoreCase("displayNote")){
//
//            try{
//
//                String DELETE_NOTE = "DELETE FROM NOTES WHERE ID = ";
//
//            }catch (Exception e){
//
//            }
//
//
//            note.id = displayNoteID;
//        }
        try{
            ContentValues cv = new ContentValues();
            cv.put("ID", note.id);
            cv.put("TITLE", note.title);
            cv.put("IMAGE", note.image);
            cv.put("SUB_ID", note.subID);
            cv.put("CONTENT", note.content);
            cv.put("CREATIONDATE", dateTimeFormat());
            cv.put("LATITUDE", note.latitude);
            cv.put("LONGITUDE", note.longitude);

            sqLiteDatabase = dbHelper.getWritableDatabase();
            if(viewType.equalsIgnoreCase("displayNote")){
                String userData[] = {String.valueOf(displayNoteID)};
                sqLiteDatabase.update("NOTES", cv, "ID = ?", userData);
                Log.v("Note Update","Note updated");
            } else{
                sqLiteDatabase.insert("NOTES", null, cv);
                Log.v("Note Creation","Note Saved");
            }

        }catch(Exception e){
            Log.e("Note Creation", e.getMessage());
        }finally {
            sqLiteDatabase.close();
        }
    }



}