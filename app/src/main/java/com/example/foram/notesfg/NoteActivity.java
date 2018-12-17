package com.example.foram.notesfg;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.sax.RootElement;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import static com.example.foram.notesfg.DBHelper.NOTE;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    EditText note_title, audioLabel;
    TextView note_content;
    Button imageButton, audioButton, mapButton, playButton, stopButton;
    ImageView imageView;

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    int subjectID, maxNoteID, displayNoteID;
    String viewType;
    Note selectedNote;
    Boolean imageSelected, isRecording;
    private static String ROOT_DIR = Environment.getExternalStorageDirectory().toString() + "/saveImages";
    private static String AUDI_DIR = Environment.getExternalStorageDirectory().toString() + "/saveRecording";

    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    private final int CAPTURE_COOLER_PHOTO = 104;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    final int REQUEST_PERM_CODE = 1000;

    Bitmap resizeImage;

    String mCurrentPhotoPath;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private String audio_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        isRecording = false;
        mapButton    = findViewById(R.id.mapButton);
        imageView    = findViewById(R.id.capturedImage);
        note_title   = findViewById(R.id.note_title);
        imageButton  = findViewById(R.id.imageButton);
        audioButton  = findViewById(R.id.audioButton);
        note_content = findViewById(R.id.note_content);
        playButton = findViewById(R.id.play);
        stopButton = findViewById(R.id.stop);
        audioLabel = findViewById(R.id.audioLabel);
        playButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        audioButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        playButton.setText("Record");

        dbHelper = new DBHelper(this);

        viewType = getIntent().getStringExtra("ViewType");
        if (viewType.equalsIgnoreCase("displayNote")){
            displayNoteID = Integer.parseInt(getIntent().getStringExtra("Note_ID"));
            selectedNote = getNoteFromDatabaseForID(displayNoteID);
            note_title.setText(String.valueOf(selectedNote.title));
            note_content.setText(String.valueOf(selectedNote.content));
            imageView.setImageBitmap(getBitmapImage(selectedNote.image));
        } else if (viewType.equalsIgnoreCase("createNote")){
            subjectID = Integer.parseInt(getIntent().getStringExtra("SubjectID"));
            maxNoteID = getMaxID();
            Log.v("Max Note ID: ", String.valueOf(maxNoteID));
        }
    }

    private int getMaxID(){
        int mx = -1;
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
            case R.id.save: {
                if (selectedNote == null){
                    selectedNote = new Note();
                }
                if(viewType.equalsIgnoreCase("displayNote")){
                    selectedNote.id = displayNoteID;
                } else {
                    selectedNote.id = maxNoteID;
                }
                selectedNote.title = String.valueOf(note_title.getText());
                selectedNote.content = String.valueOf(note_content.getText());
                saveNote(selectedNote);
//                Intent homeActivity = new Intent(this, HomeActivity.class);
//                startActivity(homeActivity);
            } break;
        }
        return true;
    }

    public Note getNoteFromDatabaseForID(int noteID){
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();
            String columns[] = {"ID", "CONTENT", "TITLE", "IMAGE"};
            String userData[] = {String.valueOf(noteID)};
            Note note = new Note();
            Cursor cursor = sqLiteDatabase.query("NOTES", columns, "ID = ?", userData, null, null, null);
            while (cursor.moveToNext()){
                note.id = noteID;
                note.title = cursor.getString(cursor.getColumnIndex("TITLE"));
                note.content = cursor.getString(cursor.getColumnIndex("CONTENT"));
                note.image = cursor.getString(cursor.getColumnIndex("IMAGE"));
            }
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
            Log.v("SaveImageName",note.image);
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
            finish();
//            Intent homeActivity = new Intent(this, HomeActivity.class);
//            startActivity(homeActivity);
        }
    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage_result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_storage = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_storage == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERM_CODE);
    }

    @Override
    public void onClick(View view) {
        if (selectedNote == null){
            selectedNote = new Note();
        }
        switch (view.getId()){
            case R.id.mapButton:{
                //Map Stuff
                Intent mapIntent = new Intent(NoteActivity.this, MapActivity.class);
                startActivity(mapIntent);
            } break;
            case R.id.audioButton:{
                //Audio Stuff
                imageSelected = false;
                String title = "Open Audio";
                CharSequence[] itemlist ={"Record an audio",
                        "Pick from Storage",
                        "Open from File"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon_app);
                builder.setTitle("Select an option");
                builder.setItems(itemlist, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {// Record Audio
                                //Do the recording stuff here
                                if(checkPermissionFromDevice()) {
                                    isRecording = true;
                                    long audioID = 0;
                                    if (viewType.equalsIgnoreCase("displayNote")) {
                                        audioID = selectedNote.id;
                                    } else {
                                        audioID = maxNoteID;
                                    }
                                    String audioName = "Audio-" + audioID + ".3gp";
                                    audio_path = AUDI_DIR + "/" +audioName;
                                    playButton.setText("Record");
                                    setUpMediaRecorder(audio_path);
                                } else {
                                    requestPermission();

                                }
                            } break;
                            case 1: {// Choose Existing Audio
                                // Do Pick Audio task here
                                isRecording = false;
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("audio/*"); // specify "audio/mp3" to filter only mp3 files
                                startActivityForResult(Intent.createChooser(intent,"Select Audio"), 2);
                            }
                            break;
                            case 2:// Choose Existing File
                                // Do Pick file here
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setCancelable(true);
                alert.show();

            } break;
            case R.id.imageButton:{
                //Image Stuff
                //Setting permission to access camera and gallary
//                getCameraPermission();
                imageSelected = true;
                String title = "Open Photo";
                CharSequence[] itemlist ={"Take a Photo",
                        "Pick from Gallery",
                        "Open from File"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.drawable.icon_app);
                builder.setTitle("Select an option");
                builder.setItems(itemlist, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// Take Photo
                                // Do Take Photo task here
                                getCameraPermission();
                                break;
                            case 1: {// Choose Existing Photo
                                // Do Pick Photo task here
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 2);
                            }
                                break;
                            case 2:// Choose Existing File
                                // Do Pick file here
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setCancelable(true);
                alert.show();
            } break;
            case R.id.play:{
                if (isRecording){
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(NoteActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer = new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(audio_path);
                        mediaPlayer.prepare();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    Toast.makeText(NoteActivity.this,"PLaying...",Toast.LENGTH_SHORT).show();
                }
            } break;
            case R.id.stop:{
                if (isRecording){
                    mediaRecorder.stop();
                    playButton.setText("Play");
                    isRecording = false;
                    saveAudioToGallary();
                } else {
                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setUpMediaRecorder(audio_path);
                    }
                }
            } break;
            default:{
                //Do nothing
            }
        }
    }

    private void getCameraPermission(){
        String []cameraPermissions = {Manifest.permission.CAMERA};
        String []storagePermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NoteActivity.this, cameraPermissions, REQUEST_IMAGE_CAPTURE);
        } if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NoteActivity.this, storagePermissions, REQUEST_PERM_WRITE_STORAGE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_COOLER_PHOTO);
    }

    private void setUpMediaRecorder(String filePath) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED){
            if (requestCode == CAPTURE_COOLER_PHOTO && imageSelected) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                saveImageToGallary(bitmap);
                imageView.setImageBitmap(bitmap);
            }
            if (requestCode == 2 && resultCode == RESULT_OK && data != null && imageSelected) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    saveImageToGallary(bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            }
            if(requestCode == 1 && resultCode == RESULT_OK && !imageSelected){
                Uri audio = data.getData(); //declared above Uri audio;
                Log.d("media", "onActivityResult: "+audio);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(new FileInputStream(new File(audio.getPath())).getFD());
                } catch (IOException e) {
                    Log.v("Audio Exception", e.getLocalizedMessage());
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });

                mediaPlayer.prepareAsync();
            }
        }
    }

    private void saveAudioToGallary(){
        Log.v("Root: ", AUDI_DIR);
        File myDir = new File(AUDI_DIR);
        myDir.mkdirs();
        long audioID = 0;
        if (viewType.equalsIgnoreCase("displayNote")){
            audioID = selectedNote.id;
        } else {
            audioID = maxNoteID;
        }
        String audioName = "Audio-" + audioID + ".3gp";
        audioLabel.setText(audioName);
        File file = new File(myDir, audioName);
        if(file.exists()) file.delete();
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            String resizeImagePath = file.getAbsolutePath();
            Log.v("ImagePath", resizeImagePath);
            Log.v("ImageName", audioName);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(NoteActivity.this,"Audio saved",Toast.LENGTH_LONG).show();
            selectedNote.image = audioName;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(NoteActivity.this,"Exception thrown",Toast.LENGTH_LONG).show();
        }
    }

    private void saveImageToGallary(Bitmap bitmap){
        Log.v("Root: ", ROOT_DIR);
        File myDir = new File(ROOT_DIR);
        myDir.mkdirs();
        long imageID = 0;
        if (viewType.equalsIgnoreCase("displayNote")){
            imageID = selectedNote.id;
        } else {
            imageID = maxNoteID;
        }
        String imageName = "Image-" + imageID + ".jpg";
        File file = new File(myDir,imageName);
        if(file.exists()) file.delete();
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            String resizeImagePath = file.getAbsolutePath();
            Log.v("ImagePath", resizeImagePath);
            Log.v("ImageName", imageName);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(NoteActivity.this,"Photo resized and saved",Toast.LENGTH_LONG).show();
            selectedNote.image = imageName;
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(NoteActivity.this,"Exception thrown",Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getBitmapImage(String imageName){
        Bitmap bmp = BitmapFactory.decodeFile(ROOT_DIR + "/" + imageName);
        Log.v("ReadingImagePath", ROOT_DIR + "/" + imageName);
        Log.v("ReadingImageName", imageName);
        return bmp;
    }
}