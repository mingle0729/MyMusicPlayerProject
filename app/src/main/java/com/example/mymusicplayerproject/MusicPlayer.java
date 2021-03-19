package com.example.mymusicplayerproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

public class MusicPlayer extends Fragment implements View.OnClickListener {

    private TextView tvTitle,tvSingerName,tvStartTime,tvEndTime;
    private ImageButton ibSongList,ibSongHeart,ibPrevSong,ibPlay,ibNextSong;
    private LinearLayout linearLayout;
    private ImageView imgAlbum;
    private SeekBar seekBar;

    private MainActivity mainActivity;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private int index;
    private MusicData musicData ;
    private MusicDB musicDB;
    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();
    private MusicAdapter musicAdapter;

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

        View view = inflater.inflate(R.layout.activity_drawable1,container, false);
        //객체 찾기
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

    }

    public void eventHandlerFunc() {

    }
    @Override
    public void onClick(View view) {

    }

    //객체 찾는 함수
    public void findViewByIdFunc(View view) {

        linearLayout = view.findViewById(R.id.linearLayout);
        ibSongList = view.findViewById(R.id.ibSongList);
        ibSongHeart = view.findViewById(R.id.ibSongHeart);
        ibPrevSong = view.findViewById(R.id.ibPrevSong);
        ibPlay = view.findViewById(R.id.ibPlay);
        ibNextSong = view.findViewById(R.id.ibNextSong);
        imgAlbum = view.findViewById(R.id.imgAlbum);
        seekBar = view.findViewById(R.id.seekBar);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSingerName = view.findViewById(R.id.tvSingerName);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);
    }
}
