package com.example.foram.notesfg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class NoteGridAdaptor extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList notes;
    String viewNote;

    NoteGridAdaptor(Context context, String viewNote, ArrayList notes) {
        inflater = (LayoutInflater.from(context));
        this.context = context;
        this.notes = notes;
        this.viewNote = viewNote;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {return null;}

    @Override
    public long getItemId(int position) {return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.notes_grid_layout,null);

        Note note = (Note)notes.get(position);
        Log.v("Displying the notes",String.valueOf(position) + "" + note.getTitle());
        TextView titleNote = convertView.findViewById(R.id.name);
        TextView contentNote = convertView.findViewById(R.id.content);
        TextView dateNote = convertView.findViewById(R.id.date);

        titleNote.setText(""+note.getTitle());
        contentNote.setText(""+note.getContent());
        dateNote.setText(""+note.getDateTime());
        return convertView;
    }
}
