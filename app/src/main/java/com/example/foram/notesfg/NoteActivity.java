package com.example.foram.notesfg;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener{

    EditText note_title;
    TextView note_content;
    Button imageButton, audioButton, mapButton;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    int subjectID, maxNoteID;

    private static final String TAG = "NoteActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        if (isServicesOK()){
            init();
        }
        note_title = findViewById(R.id.note_title);
        note_content = findViewById(R.id.note_content);
        imageButton = findViewById(R.id.imageButton);
        audioButton = findViewById(R.id.audioButton);
        mapButton = findViewById(R.id.mapButton);
        imageButton.setOnClickListener(this);
        audioButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        subjectID = getIntent().getIntExtra("SubjectID", 1);
        dbHelper = new DBHelper(this);
        maxNoteID = getMaxID();
        Log.v("Max Note ID: ", String.valueOf(maxNoteID));
        if (isServicesOK()){
            init();
        }
    }

    private void init(){}{

    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NoteActivity.this);
        if (available == ConnectionResult.SUCCESS){
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
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
                note.content = String.valueOf(note_content.getText());
                saveNote(note);
                Intent homeActivity = new Intent(this, HomeActivity.class);
                startActivity(homeActivity);

                break;
        }
        return true;
    }

    public void saveNote(Note note){
        try{
            ContentValues cv = new ContentValues();
            cv.put("ID", note.id);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mapButton:{
                //Map Stuff
                Intent mapIntent = new Intent(NoteActivity.this, MapActivity.class);
                startActivity(mapIntent);
            } break;
            case R.id.audioButton:{
                //Audio Stuff
            } break;
            case R.id.imageButton:{
                //Image Stuff
            } break;
            default:{
                //Do nothing
            }
        }
    }

}