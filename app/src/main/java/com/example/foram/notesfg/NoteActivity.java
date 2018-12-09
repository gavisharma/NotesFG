package com.example.foram.notesfg;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.TextView;

public class NoteActivity extends AppCompatActivity {

    EditText note_title;
    TextView content_text;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        note_title = findViewById(R.id.note_title);
        content_text = findViewById(R.id.note_content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_note, menu);
        return true;
    }

    public void saveNote(Note note){
        try{
            ContentValues cv = new ContentValues();
            cv.put("ID",note.id);
            cv.put("TITLE", note.title);
            cv.put("IMAGE", note.image);
            cv.put("CONTENT", note.content);
            cv.put("DATETIME",note.dateTime);
            cv.put("LATITUDE", note.latitude);
            cv.put("LONGITUDE", note.longitude);

            sqLiteDatabase = dbHelper.getWritableDatabase();
            sqLiteDatabase.insert("NOTE", null, cv);

            Log.v("Note Creation","Note Saved");

        }catch(Exception e){
            Log.e("Note Creation", e.getMessage());
        }finally {
            sqLiteDatabase.close();
        }
    }
}