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
            }
        }catch(Exception e){
            Log.e("SubjectActivity",e.getMessage());
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
                noteActivity.putExtra("SubjectID", String.valueOf(sub.s_id));
                noteActivity.putExtra("ViewType", "createNote");
                startActivity(noteActivity);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
