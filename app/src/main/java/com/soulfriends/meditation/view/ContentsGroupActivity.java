package com.soulfriends.meditation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.soulfriends.meditation.R;
import com.soulfriends.meditation.viewmodel.MeditationContentsAdapter;
import com.soulfriends.meditation.viewmodel.MeditationContentsViewModel;

import java.util.ArrayList;

public class ContentsGroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    MeditationContentsAdapter adapter;
    private ArrayList<MeditationContentsViewModel> dataSet = new ArrayList<>();

    //private ArrayList<MeditationContents> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_group);


//        meditationContentsViewModel = new ViewModelProvider(this).get(MeditationContentsViewModel.class);
//
//        meditationContentsViewModel.getMeditationContents().observe(this, new Observer<List<MeditationContents>>() {
//            @Override
//            public void onChanged(List<MeditationContents> meditationContents) {
//                adapter.notifyDataSetChanged();
//            }
//        });

        initRecyclerView();


        // 팝업 테스트
        //AlertDialog alertDialog = new AlertDialog(this, getString(R.string.error_login));
        //alertDialog.setCancelable(true);
        //alertDialog.show();
    }

    private void initRecyclerView()
    {

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        dataSet.add(new MeditationContentsViewModel("aa", "aa 001"));
        dataSet.add(new MeditationContentsViewModel("bb", "bb 002"));
        //dataSet.add(new MeditationContents("cc", "cc 003"));
        //dataSet.add(new MeditationContents("dd", "dd 004"));
        //dataSet.add(new MeditationContents("ee", "ee 005"));

        //meditationContentsViewModel.Set(dataSet);

        adapter = new MeditationContentsAdapter(dataSet, this);
        recyclerView.setAdapter(adapter);
    }

}