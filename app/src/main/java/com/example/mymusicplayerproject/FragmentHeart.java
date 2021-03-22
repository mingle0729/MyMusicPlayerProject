package com.example.mymusicplayerproject;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentHeart extends Fragment {

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<MusicData>();
    private MusicAdapter musicAdapter;
    private MusicDB musicDB;
    private RecyclerView recycleHeart;
    private DrawerLayout drawerLayout;
    private MainActivity mainActivity;

    private Fragment musicPlayer;

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

        View view = inflater.inflate(R.layout.activity_fragment_heart, container, false);

        //객체 찾아오기
        findViewByIdFunc(view);

        //MP3 파일 관리
        musicDB= MusicDB.getInstance(mainActivity.getApplicationContext());
        musicDataArrayList = musicDB.saveLikeList();

        //어뎁터 만들기
        makeAdapter(container);

        //음악리스트 가져오기
        musicDataArrayList = musicDB.compareArrayList();
        //음악DB저장
        musicDB.insertQuery(musicDataArrayList);
        //어뎁터에 데이터 세팅하기
        settingAdapterDataList(mainActivity.getLikeList());

        //이벤트 처리하기
        eventHandler();

        return view;
    }


    // 객체 찾아오기
    public void findViewByIdFunc(View view) {
        recycleHeart = view.findViewById(R.id.recycleHeart);
        drawerLayout = view.findViewById(R.id.drawerLayout);
    }


    //어뎁터 만들기
    public void makeAdapter(ViewGroup container) {

        musicAdapter = new MusicAdapter(container.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());

        recycleHeart.setLayoutManager(linearLayoutManager);
        recycleHeart.setAdapter(musicAdapter);
        recycleHeart.setLayoutManager(linearLayoutManager);

    }

    //어뎁터에 데이터를 세팅하기
    public void settingAdapterDataList(ArrayList<MusicData> musicDataArrayList) {

        musicAdapter.setMusicList(musicDataArrayList);

        recycleHeart.setAdapter(musicAdapter);

        musicAdapter.notifyDataSetChanged();

    }

    //리스트 클릭시에 뮤직플레이어 띄우기
    public void eventHandler() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override

            public void onItemClick(View view, int position) {
                mainActivity.setPlayerData(position, false);
            }
        });
    }
}
