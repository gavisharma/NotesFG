package com.example.foram.notesfg;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    GridView notesGrid;
    String viewNote;
    ArrayList<Note> noteArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        notesGrid = findViewById(R.id.notesGrid);
        notesGrid.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        viewNote = "select";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subjectActivity = new Intent(getApplicationContext(), SubjectActivity.class);
                startActivity(subjectActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new DBHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
//        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        noteArray.clear();
        noteArray = getNotes();
        notesDisplayGrid(noteArray,viewNote);
    }

    public ArrayList<Note> getNotes() {
        dbHelper = new DBHelper((this));
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();
            String noteColumns[] = {"ID", "CONTENT", "CREATIONDATE", "TITLE", "LATITUDE", "LONGITUDE", "IMAGE", "SUB_ID"};
            Cursor cursor = sqLiteDatabase.query("NOTES", noteColumns,null,null,null,null,null);
            while (cursor.moveToNext()){
                Note notes = new Note();
                notes.id = cursor.getInt(cursor.getColumnIndex("ID"));
                notes.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
                notes.setContent(cursor.getString(cursor.getColumnIndex("CONTENT")));
                notes.setDateTime(cursor.getString(cursor.getColumnIndex("CREATIONDATE")));
                noteArray.add(notes);
            }
        }catch(Exception e){
            Log.e("HomeActivity",e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
        return noteArray;
    }

    private void notesDisplayGrid(ArrayList<Note> list, final String viewNote){
        final NoteGridAdaptor noteGridAdaptor = new NoteGridAdaptor(getApplicationContext(), viewNote, list);
        notesGrid.setAdapter(noteGridAdaptor);
        notesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteArray.get(position);
                Intent noteActivity = new Intent(getApplicationContext(), NoteActivity.class);
                noteActivity.putExtra("ViewType", "displayNote");
                noteActivity.putExtra("Note_ID", String.valueOf(note.id));
//                noteActivity.putExtra("Note_ID", 0 + note.id);
                startActivity(noteActivity);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sort_by_subject) {
            // Handle the camera action
        } else if (id == R.id.sort_by_date) {

        } else if (id == R.id.sort_by_title) {

        } else if (id == R.id.add_subject) {

        } else if (id == R.id.remove_subject) {

        } else if (id == R.id.show_subject) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
