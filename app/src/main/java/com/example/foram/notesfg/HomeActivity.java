package com.example.foram.notesfg;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MenuInflater;
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
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    GridView notesGrid;
    String viewNote;

    private static int SORT_NORMAL  = 0;
    private static int SORT_DATE    = 1;
    private static int SORT_TITLE   = 2;
    private static int SORT_SUBJECT = 3;

    ArrayList<Note> noteArray = new ArrayList<>();

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        noteArray.clear();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v("Query String",""+query);
            try {
                dbHelper = new DBHelper((this));
                sqLiteDatabase = dbHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT ID, CONTENT, CREATIONDATE, TITLE, LATITUDE, LONGITUDE, IMAGE, SUB_ID FROM NOTES where CONTENT like '%" + query + "%' OR TITLE like '%" + query + "%'", null);
                while (cursor.moveToNext()){
                    Note notes = new Note();
                    notes.id = cursor.getInt(cursor.getColumnIndex("ID"));
                    notes.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
                    notes.setContent(cursor.getString(cursor.getColumnIndex("CONTENT")));
                    notes.setDateTime(cursor.getString(cursor.getColumnIndex("CREATIONDATE")));
                    notes.setImage(cursor.getString(cursor.getColumnIndex("IMAGE")));
                    notes.setSubID(cursor.getLong(cursor.getColumnIndex("SUB_ID")));
                    noteArray.add(notes);
                }
            } catch(Exception e){
                Log.e("HomeActivity",e.getMessage());
            } finally {
                sqLiteDatabase.close();
                notesDisplayGrid(noteArray, viewNote);
            }
        }
    }

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
        sqLiteDatabase.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        noteArray.clear();
        noteArray = getNotes(SORT_NORMAL);
        notesDisplayGrid(noteArray,viewNote);
    }

    public ArrayList<Note> getNotes(int searchPattern) {
        dbHelper = new DBHelper((this));
        try{
            sqLiteDatabase = dbHelper.getReadableDatabase();
            String noteColumns[] = {"ID", "CONTENT", "CREATIONDATE", "TITLE", "LATITUDE", "LONGITUDE", "IMAGE", "SUB_ID"};
            Cursor cursor = sqLiteDatabase.query("NOTES", noteColumns,null,null,null,null,null);
            if (searchPattern == SORT_TITLE) {
                cursor = sqLiteDatabase.query("NOTES", noteColumns,null,null,null,null,"TITLE ASC");
            } else if (searchPattern == SORT_SUBJECT){
                cursor = sqLiteDatabase.query("NOTES", noteColumns,null,null,null,null,"SUB_ID ASC");
            } else if (searchPattern == SORT_DATE){
                cursor = sqLiteDatabase.query("NOTES", noteColumns,null,null,null,null,"CREATIONDATE ASC");
            }
            while (cursor.moveToNext()){
                Note notes = new Note();
                notes.id = cursor.getInt(cursor.getColumnIndex("ID"));
                notes.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
                notes.setContent(cursor.getString(cursor.getColumnIndex("CONTENT")));
                notes.setDateTime(cursor.getString(cursor.getColumnIndex("CREATIONDATE")));
                notes.setImage(cursor.getString(cursor.getColumnIndex("IMAGE")));
                notes.setSubID(cursor.getLong(cursor.getColumnIndex("SUB_ID")));
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
//        getMenuInflater().inflate(R.menu.home, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                noteArray.clear();
                getNotes(SORT_NORMAL);
                notesDisplayGrid(noteArray, viewNote);
                return true;
            }
        });
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
            noteArray.clear();
            getNotes(SORT_SUBJECT);
            notesDisplayGrid(noteArray, viewNote);
        } else if (id == R.id.sort_by_date) {
            noteArray.clear();
            getNotes(SORT_DATE);
            notesDisplayGrid(noteArray, viewNote);
        } else if (id == R.id.sort_by_title) {
            noteArray.clear();
            getNotes(SORT_TITLE);
            notesDisplayGrid(noteArray, viewNote);
        } else if (id == R.id.add_subject) {

        } else if (id == R.id.remove_subject) {

        } else if (id == R.id.show_subject) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
