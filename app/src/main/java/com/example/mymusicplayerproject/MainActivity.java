package com.example.mymusicplayerproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final int  HOME = 1, HEART = 2;
    private RecyclerView  recycleHome, recycleHeart;
    private BottomNavigationView mainMenuBar;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private TextView tvTitle, tvSingerName, tvStartTime, tvEndTime;
    private ImageButton ibSongList, ibSongHeart, ibPrevSong, ibPlay, ibNextSong;
    private ImageView imgAlbum;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private FragmentHome fragmentHome;
    private FragmentHeart fragmentHeart;
    //-----------------------------------------------------------

    private ArrayList<MusicData> musicDataArrayList = new ArrayList<>();
    private ArrayList<MusicData> musicLikeArrayList = new ArrayList<>();

    //-----------------------------------------------------------

    private MusicDB musicDB;
    private MusicAdapter musicHeartAdapter;
    private MusicData musicData;
    private MainActivity mainActivity;

    //-----------------------------------------------------------

    private long backTime = 0L;
    private boolean nowPlaying = false;
    private int index;



    //화면이 멈췄을 때
    @Override
    protected void onPause() {
        super.onPause();
    }

    //화면이 멈췄다가 다시 돌아왔을 때
    @Override
    protected void onResume() {
        super.onResume();
    }

    //어플 종료할 때
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long gapTime = currentTime - backTime;

        if(gapTime >= 0 && gapTime <= 2000){
            super.onBackPressed();
            toastMessage("정상 종료");
        }else{
            backTime = currentTime;
            toastMessage("연속으로 두 번 터치시 어플 종료");
        }
    }

    //화면 설정함수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //액션바
        setSupportActionBar(toolbar);

        findViewByIdFunc();
        //권한 부여하기
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);

        //

        musicDB = MusicDB.getInstance(MainActivity.this);

        musicDataArrayList = musicDB.compareArrayList();
        musicLikeArrayList = musicDB.saveLikeList();

        musicDB.insertQuery(musicDataArrayList);

        //객체 찾는 함수


        //프레그먼트 화면 바꾸기
        changFragment();

        eventHandlerFunc();
    }// end of onCreate


    //이벤트 처리 함수
    private void eventHandlerFunc() {
        ibPlay.setOnClickListener((View v) -> {
            if(nowPlaying == true) {
                nowPlaying = false;
                mediaPlayer.pause();
                ibPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            } else {
                nowPlaying = true;
                mediaPlayer.start();
                ibPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                setSeekBarThread();
            }
        });
        ibPrevSong.setOnClickListener((View v) -> {
            SimpleDateFormat sdfS = new SimpleDateFormat("ss");
            int nowDurationForSec =  Integer.parseInt(sdfS.format(mediaPlayer.getCurrentPosition()));

            mediaPlayer.stop();
            mediaPlayer.reset();
            nowPlaying =false;
            ibPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            try {
                if(nowDurationForSec <=5) {
                    if(index == 0)  {
                        index = musicDataArrayList.size() -1;
                        setPlayerData(index, true);
                    } else {
                        index--;
                        setPlayerData(index, true);
                    }
                } else {
                    setPlayerData(index, true);
                }
            } catch (Exception e) {
                Log.d("Prev", e.getMessage());
            }
        });
        ibNextSong.setOnClickListener((View v) -> {
            mediaPlayer.stop();
            mediaPlayer.reset();
            nowPlaying =false;
            ibPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            try {
                if(index == musicDataArrayList.size() -1) {
                    index = 0;
                    setPlayerData(index, true);
                } else {
                    index++;
                    setPlayerData(index, true);
                }
            } catch (Exception e) {
                Log.d("Next", e.getMessage());
            }
        });

        ibSongHeart.setOnClickListener((View v) -> {
            try {
                if(musicData.getLiked() == 1){
                    musicData.setLiked(0);
                    musicLikeArrayList.remove(musicData);
                    ibSongHeart.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }else{
                    musicData.setLiked(1);
                    musicLikeArrayList.add(musicData);
                    ibSongHeart.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                musicDB.updateQuery(musicDataArrayList);
            } catch (Exception e) {
                toastMessage("곡 선택 요망");
            }
        });
    }


    //좋아요 리스트 정보 받아오기
    public ArrayList<MusicData> getLikeList() {

        musicLikeArrayList = musicDB.saveLikeList();

        if (musicLikeArrayList.isEmpty()) {
            toastMessage("좋아요 리스트 가져오기 실패");
        } else {
            toastMessage("좋아요 리스트 가져오기 성공");
        }

        return musicLikeArrayList;
    }


    //--------------------------------------------------------------------------------

    public ArrayList<MusicData> getMusicDataArrayList() {
        return musicDataArrayList;
    }

    public ArrayList<MusicData> getMusicLikeArrayList() {
        return musicLikeArrayList;
    }

    public MusicAdapter getMusicHeartAdapter() {
        return musicHeartAdapter;
    }


    //--------------------------------------------------------------------------------


    //프레그먼트 이용해서 화면 바꾸기
    public void changFragment() {

        mainMenuBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        setFragmentChange(HOME);
                        break;
                    case R.id.menuHeart:
                        setFragmentChange(HEART);
                        break;
                    default:
                        Log.d("MainActivity", "menuBar error");
                        break;
                }

                return true;
            }
        });
        setFragmentChange(HOME);

    }

    //뮤직플레이어에 데이타 세팅하기
    public void setPlayerData(int position, boolean flag) {
        drawerLayout.openDrawer(Gravity.LEFT);
        index = position;

        mediaPlayer.stop();
        mediaPlayer.reset();

        MusicAdapter musicAdapter = new MusicAdapter( mainActivity );

        if (flag) {
            musicData = musicDataArrayList.get(position);

        } else {
            musicData = musicLikeArrayList.get(position);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

        tvTitle.setText(musicData.getTitle());
        tvSingerName.setText(musicData.getArtists());
        tvStartTime.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));

        if (musicData.getLiked() == 1) {
            ibSongHeart.setActivated(true);
        } else {
            ibSongHeart.setActivated(false);
        }

        if(musicData.getLiked() == 1) {
            ibSongHeart.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            ibSongHeart.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        // 앨범 이미지 세팅
        Bitmap albumImg = musicAdapter.getAlbumImg(this, Integer.parseInt(musicData.getAlbumArt()), 200);
        if (albumImg != null) {
            imgAlbum.setImageBitmap(albumImg);
        } else {
            imgAlbum.setImageResource(R.drawable.music_icon);
        }

        // 음악 재생
        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
        try {
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ibPlay.setImageResource(R.drawable.ic_baseline_pause_24);
            nowPlaying =true;
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            ibPlay.setActivated(true);

            setSeekBarThread();

            // 재생완료 리스너
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicData.setCount(musicData.getCount() + 1);
                    ibNextSong.callOnClick();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //시크바 설정하기
    public void seekBarChange(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // 사용자 조작시, seekbar 이동
                if(b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });
    }

    //시크바 스레드 처리하기
    private void setSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                seekBar.setMax(mediaPlayer.getDuration());

                while (mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStartTime.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                            tvEndTime.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
                        }
                    });
                    SystemClock.sleep(100);
                }
            }
        });
        thread.start();
    }


    //메인 메뉴바 화면 바꾸는 함수
    public void setFragmentChange(int i) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (i) {
            case HOME:
                ft.replace(R.id.frameLayout, fragmentHome);
                ft.commit();
                break;
            case HEART:
                ft.replace(R.id.frameLayout, fragmentHeart);
                ft.commit();
                break;
        }
    }

    //출력 함수
    public void toastMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    //객체 찾는 함수
    public void findViewByIdFunc() {
        toolbar = findViewById(R.id.toolbar);

        mainMenuBar = findViewById(R.id.mainMenuBar);
        frameLayout = findViewById(R.id.frameLayout);
        drawerLayout = findViewById(R.id.drawerLayout);

        recycleHome = findViewById(R.id.recycleHome);
        recycleHeart = findViewById(R.id.recycleHeart);

        fragmentHome = new FragmentHome();
        fragmentHeart = new FragmentHeart();

        ibSongList = findViewById(R.id.ibSongList);
        ibSongHeart = findViewById(R.id.ibSongHeart);
        ibPrevSong = findViewById(R.id.ibPrevSong);
        ibPlay = findViewById(R.id.ibPlay);
        ibNextSong = findViewById(R.id.ibNextSong);
        imgAlbum = findViewById(R.id.imgAlbum);
        seekBar = findViewById(R.id.seekBar);
        tvTitle = findViewById(R.id.tvTitle);
        tvSingerName = findViewById(R.id.tvSingerName);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
    }
}