package com.example.foram.notesfg;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
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
    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    private final int CAPTURE_COOLER_PHOTO = 104;
    Bitmap resizeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

//        imageButton = findViewById(R.id.image);

        note_title = findViewById(R.id.note_title);
        content_text = findViewById(R.id.note_content);
        viewType = getIntent().getStringExtra("ViewType");
        dbHelper = new DBHelper(this);
        if (viewType.equalsIgnoreCase("displayNote")){
            displayNoteID = Integer.parseInt(getIntent().getStringExtra("Note_ID"));
            selectedNote = getNoteFromDatabaseForID(displayNoteID);
            note_title.setText(String.valueOf(selectedNote.title));
            content_text.setText(String.valueOf(selectedNote.content));
        } else if (viewType.equalsIgnoreCase("createNote")){
            subjectID = Integer.parseInt(getIntent().getStringExtra("SubjectID"));
            maxNoteID = getMaxID();
            Log.v("Max Note ID: ", String.valueOf(maxNoteID));
        }


//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Setting permission to access camera and gallary
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                        ActivityCompat.requestPermissions(NoteActivity.this, new String[]{Manifest.permission.CAMERA},1);
//                    }
//                }
//
//                if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    ActivityCompat.requestPermissions(NoteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
//
//                } else {
//                        takePicture();
//                }
//
//            }
//        });


    }

//    public void takePicture(){
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,CAPTURE_COOLER_PHOTO);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == RESULT_OK){
//            switch (requestCode) {
//
//                case CAPTURE_COOLER_PHOTO:
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
////                    int imageWidth = 100;
////                    int imageHeight = 80;
//                    saveImageToGallary(bitmap);
//                    imageButton.setImageBitmap(bitmap);
//                    break;
//
//            }
//        }
//    }

//
//    private void saveImageToGallary(Bitmap bitmap){
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/saveImages");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 1000;
//        n = generator.nextInt(n);
//        String imageName = "Image-" + n + ".jpg";
//        File file = new File(myDir,imageName);
//        if(file.exists()) file.delete();
//        try{
//
//            FileOutputStream outputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
//            String esizeImagePath = file.getAbsolutePath();
//            outputStream.flush();
//            outputStream.close();
////            saveToSharedPreferences("coolerImagePath",resizeImagePath,NoteActivity.this);
////            saveToSharedPreferences("coolerImageName",coolName,ImagesAndLast.this);
////            txvCoolerPicPath.setText(imageName);
//            Toast.makeText(NoteActivity.this,"Photo resized and saved",Toast.LENGTH_LONG).show();
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(NoteActivity.this,"Exception thrown",Toast.LENGTH_LONG).show();
//        }
//
////        String uriAfterResize = resizeImagePath;
////        if(uriAfterResize != null  &&  uriAfterResize.isEmpty()){
////            new uploadPictureToServer(uriAfterResize).execute(Uri.parse(uriAfterResize));
////
////        }
//    }

    @Override
    public void onStart(){
        super.onStart();

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