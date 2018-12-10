package com.example.foram.notesfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;

    GridView subjectGrid;
    private Button proceedButton;
    String viewType;
    Subject selectedSubject;

    ArrayList<Subject> subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        subjectGrid = findViewById(R.id.subjectGrid);
        subjectGrid.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(this);
        viewType = "select";
        selectedSubject = null;
        subjects = populateData();
        setUpGridFor(subjects, viewType, selectedSubject);

    }

    public ArrayList<Subject> populateData(){
        dbHelper = new DBHelper(this);
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();
            String columns[] = {"ID", "NAME"};
            Cursor cursor = sqLiteDatabase.query("SUBJECTS",columns,
                    null,null,null, null, null);
            while (cursor.moveToNext()){
                Subject sub = new Subject();
                sub.setS_name(cursor.getString(cursor.getColumnIndex("NAME")));
                sub.setS_id(cursor.getInt(cursor.getColumnIndex("ID")));
                subjects.add(sub);
//                Toast.makeText(this, userData,Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.e("RegisterActivity",e.getMessage());
        }finally {
            sqLiteDatabase.close();
        }
        return subjects;
    }

    private void setUpGridFor(ArrayList<Subject> itemsAvailable, final String viewType, final Subject selectedSub){
        final SubjectGridAdapter subjectGridAdapter = new SubjectGridAdapter(getApplicationContext(), itemsAvailable, viewType, selectedSub);
        subjectGrid.setAdapter(subjectGridAdapter);
        subjectGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Subject sub = subjects.get(position);
                Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                noteActivity.putExtra("SubjectId:", sub.s_id);
                startActivity(noteActivity);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (viewType == "menu") {
            finish();
//        } else if (viewType == "cart") {
//            viewType = "menu";
//            cartButton.setText("Show Cart");
//            setUpGridFor(items, viewType);
//        } else if (viewType == "payment") {
//            viewType = "cart";
//            cartButton.setText("Show Receipt");
//            setUpGridFor(selectedItems, viewType);
//        } else if (viewType == "receipt") {
////            viewType = "cart";
////            cartButton.setText("Make Payment");
////            setUpGridFor(selectedItems, viewType);
//            finish();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences preferences = this.getSharedPreferences("com.example.gavi.myapplication", Context.MODE_PRIVATE);
        Boolean paymentDone = preferences.getBoolean("paymentDone", false);
        if (paymentDone) {
//            if (selectedItems.size() > 0){
//                viewType = "receipt";
//                cartButton.setText("Done");
//                setUpGridFor(selectedItems, viewType);
//            }
        }
    }

    @Override
    public void onClick(View view) {
//        if (viewType == "menu") {
//            viewType = "cart";
////            cartButton.setText("Make Payment");
//        }
//        else if (viewType == "cart") {
//            //Make Payment
//            SharedPreferences prefs = this.getSharedPreferences("com.example.gavi.myapplication", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putBoolean("paymentDone", false);
//            editor.putString("cardNumber","");
//            editor.commit();
//            startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
//        } else if (viewType == "payment") {
//            viewType = "receipt";
//            cartButton.setText("Done");
//        } else if (viewType == "receipt") {
//            finish();
//        }
//        if (selectedItems.size() > 0){
//            setUpGridFor(selectedItems, viewType);
//        }
    }
}
