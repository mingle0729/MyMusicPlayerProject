package com.example.mymusicplayerproject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentHeart extends Fragment {

    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();
    private MusicAdapter musicAdapter;
    private MusicDB musicDB;
    private RecyclerView recycleList;
    private MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_mainfragment3, container, false);

        //객체 찾는 함수
        findViewByIdFunc(view);

        //MP3 파일 관리하는 함수
        musicDB= MusicDB.getInstance(mainActivity.getApplicationContext());
        sdCardList = musicDB.findContentProvMp3List();

        //어뎁터 만들기
        makeAdapter(container);

        //이벤트 함수
        // eventHandlerFunc();

        return view;
    }

    //어뎁터 만들기
    public void makeAdapter(ViewGroup container) {

        musicAdapter = new MusicAdapter(container.getContext(), sdCardList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        recycleList.setLayoutManager(linearLayoutManager);
        recycleList.setAdapter(musicAdapter);

    }

    public void eventHandlerFunc() {

    }

    public void findViewByIdFunc(View view) {
        recycleList = view.findViewById(R.id.recycleList);
    }
}
