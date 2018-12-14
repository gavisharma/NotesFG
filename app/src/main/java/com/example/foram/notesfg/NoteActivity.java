package com.example.foram.notesfg;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import static com.example.foram.notesfg.DBHelper.NOTE;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    EditText note_title;
    TextView note_content;
    Button imageButton, audioButton, mapButton;
    ImageView imageView;

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    int subjectID, maxNoteID, displayNoteID;
    String viewType;
    Note selectedNote;
    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    private final int CAPTURE_COOLER_PHOTO = 104;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap resizeImage;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mapButton    = findViewById(R.id.mapButton);
        imageView    = findViewById(R.id.capturedImage);
        note_title   = findViewById(R.id.note_title);
        imageButton  = findViewById(R.id.imageButton);
        audioButton  = findViewById(R.id.audioButton);
        note_content = findViewById(R.id.note_content);
        imageButton.setOnClickListener(this);
        audioButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);

        viewType = getIntent().getStringExtra("ViewType");
        if (viewType.equalsIgnoreCase("displayNote")){
            displayNoteID = Integer.parseInt(getIntent().getStringExtra("Note_ID"));
            selectedNote = getNoteFromDatabaseForID(displayNoteID);
            note_title.setText(String.valueOf(selectedNote.title));
            note_content.setText(String.valueOf(selectedNote.content));
        } else if (viewType.equalsIgnoreCase("createNote")){
            subjectID = Integer.parseInt(getIntent().getStringExtra("SubjectID"));
            maxNoteID = getMaxID();
            Log.v("Max Note ID: ", String.valueOf(maxNoteID));
        }
    }


//    public void takePicture(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent,CAPTURE_COOLER_PHOTO);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            switch (requestCode) {
//                case CAPTURE_COOLER_PHOTO:
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
////                    int imageWidth = 100;
////                    int imageHeight = 80;
////                    saveImageToGallary(bitmap);
////                    imageButton.setImageBitmap(bitmap);
//                    break;
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
                Note note = new Note();
                note.id = maxNoteID;
                note.title = String.valueOf(note_title.getText());
                note.content = String.valueOf(note_content.getText());
                saveNote(note);
//                Intent homeActivity = new Intent(this, HomeActivity.class);
//                startActivity(homeActivity);
            } break;
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
            while (cursor.moveToNext()){
                note.id = noteID;
                note.title = cursor.getString(cursor.getColumnIndex("TITLE"));
                note.content = cursor.getString(cursor.getColumnIndex("CONTENT"));
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
                //Setting permission to access camera and gallary
                getCameraPermission();
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


    //Gallery and Camera related methods
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

}