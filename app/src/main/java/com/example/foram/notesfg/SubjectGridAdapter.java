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

public class SubjectGridAdapter extends BaseAdapter implements View.OnClickListener{

    Context context;
    LayoutInflater inflater;
    Subject selectedSubject;
    ArrayList subjects;
    String viewType;

    SubjectGridAdapter(Context context, ArrayList subjects, String viewType, Subject selectedSub){
        inflater             = (LayoutInflater.from(context));
        this.context         = context;
        this.subjects        = subjects;
        this.viewType        = viewType;
        this.selectedSubject = selectedSub;
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

        Subject sub = (Subject)subjects.get(i);
        Log.v("Subject Details", String.valueOf(i) + " " + sub.getS_name());
        TextView subjectName = view.findViewById(R.id.subjectName);
        subjectName.setText("          "+sub.getS_name());
//        if (viewType == "select") {
//            if (selectedSubject != null){
//                if (selectedSubject.equals(sub)) {
//                    selectedImage.setImageResource(R.drawable.ic_selected);
//                } else{
//                    selectedImage.setImageResource(0);
//                }
//            }  else{
//                selectedImage.setImageResource(0);
//            }
//        }
        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
