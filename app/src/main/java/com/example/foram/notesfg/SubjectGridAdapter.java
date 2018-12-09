package com.example.foram.notesfg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SubjectGridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String selectedSubject;
    ArrayList subjects;
    String viewType;

    SubjectGridAdapter(Context context, ArrayList subjects, String viewType){
        inflater = (LayoutInflater.from(context));
        this.context         = context;
        this.subjects        = subjects;
        this.viewType        = viewType;
        this.selectedSubject = "";
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.subject_grid_layout, null);

        Subject subject = (Subject)subjects.get(i);

        ImageView selectedImage = view.findViewById(R.id.subjectSelected);
        TextView subjectName = view.findViewById(R.id.subjectName);
        selectedImage.setVisibility(View.VISIBLE);
        if (viewType == "select") {
            if (selectedSubject.equalsIgnoreCase(subject.s_name)) {
                    selectedImage.setImageResource(R.drawable.ic_selected);
            }
            subjectName.setText("          "+subject.getS_name());
        }

        return null;
    }
}
