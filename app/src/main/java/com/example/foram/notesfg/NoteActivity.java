package com.example.foram.notesfg;

import android.content.ContentValues;
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

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    EditText note_title;
    TextView content_text;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    int subjectID, maxNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        note_title = findViewById(R.id.note_title);
        content_text = findViewById(R.id.note_content);
        subjectID = getIntent().getIntExtra("SubjectID", 1);
        dbHelper = new DBHelper(this);
        maxNoteID = getMaxID();
        Log.v("MAx Note ID: ", String.valueOf(maxNoteID));
    }

    public int getMaxNoteID(){
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();
            String columns[] = {"ID"};

            Cursor cursor = sqLiteDatabase.query("NOTES",columns,
                    "MAX",null,null, null, null);

            while (cursor.moveToNext()){
                maxNoteID = cursor.getInt(cursor.getColumnIndex("ID")) + 1;
//                Toast.makeText(this, userData,Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Log.e("RegisterActivity",e.getMessage());
        }finally {
            sqLiteDatabase.close();
        }
        return maxNoteID;

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

//    public void insertNote(Note note){
//        try{
//            String SUBJECT_TABLE_DATA = "INSERT INTO NOTES VALUES(1, 'C'), (2, 'C++'), (3, 'JAVA'), (4, 'SWIFT'), (5, 'PHP'), (6, 'JavaScript'), (7, 'ObjectiveC'), (8, 'ORACLE'), (9, 'Python'), (10, 'CSS')";
//
//            Log.v("DBHelper", SUBJECT_TABLE_DATA );
//
//            db.execSQL(SUBJECT_TABLE_DATA);
//
//        } catch (Exception e) {
//
//            Log.e("DBHelper", e.getMessage());
//        }
//    }

    public void saveNote(Note note){
        try{
            ContentValues cv = new ContentValues();
            cv.put("ID",note.id);
            cv.put("TITLE", note.title);
            cv.put("IMAGE", note.image);
            cv.put("SUB_ID", note.subID);
            cv.put("CONTENT", note.content);
            cv.put("DATETIME",note.dateTime);
            cv.put("LATITUDE", note.latitude);
            cv.put("LONGITUDE", note.longitude);

            sqLiteDatabase = dbHelper.getWritableDatabase();
            sqLiteDatabase.insert("NOTES", null, cv);

            Log.v("Note Creation","Note Saved");

        }catch(Exception e){
            Log.e("Note Creation", e.getMessage());
        }finally {
            sqLiteDatabase.close();
        }
    }
}