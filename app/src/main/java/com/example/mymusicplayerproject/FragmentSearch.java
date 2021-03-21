package com.example.mymusicplayerproject;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentSearch extends Fragment {

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<MusicData>();
    private MusicAdapter musicAdapter;
    private MusicDB musicDB;

    private RecyclerView recycleSearch;
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private MainActivity mainActivity;

    private Fragment musicPlayer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_mainfragment1, container, false);

        //객체 찾는 함수
        findViewByIdFunc(view);

        //MP3 파일 관리하는 함수
        musicDB = MusicDB.getInstance(mainActivity.getApplicationContext());
        musicDataArrayList = musicDB.findContentProvMp3List();

        //어뎁터 만들기
        makeAdapter(container);
        //음악리스트 가져오기
        musicDataArrayList = musicDB.compareArrayList();
        //음악DB저장
        musicDB.insertQuery(musicDataArrayList);
        //어뎁터에 데이터 세팅하기
        settingAdapterDataList(musicDataArrayList);
        //
        eventHandler();

        return view;
    }

    public void eventHandler() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(View view, int position) {
                mainActivity.setPlayerData(position, true);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        
    }

    public void settingAdapterDataList(ArrayList<MusicData> musicDataArrayList) {

        musicAdapter.setMusicList(musicDataArrayList);

        recycleSearch.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();

    }

    public void makeAdapter(ViewGroup container) {

        musicAdapter = new MusicAdapter(container.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        recycleSearch.setLayoutManager(linearLayoutManager);
        recycleSearch.setAdapter(musicAdapter);
        recycleSearch.setLayoutManager(linearLayoutManager);
    }

    public void findViewByIdFunc(View view) {
        searchView = view.findViewById(R.id.searchView);
        recycleSearch = view.findViewById(R.id.recycleSearch);
        drawerLayout = view.findViewById(R.id.drawerLayout);
    }


}
