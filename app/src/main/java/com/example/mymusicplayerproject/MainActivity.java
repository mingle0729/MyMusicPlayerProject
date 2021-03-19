package com.example.mymusicplayerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final int SEARCH =1 , HOME =2, HEART = 3;
    private BottomNavigationView mainMenuBar;
    private FrameLayout frameLayout;
    private FragmentSearch  fragmentSearch;
    private FragmentHome fragmentHome;
    private FragmentHeart fragmentHeart;
    private Toolbar toolbar;

    /////////////////////////////////////////////////


    /////////////////////////////////////////////////



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //액션바
        setSupportActionBar(toolbar);

        //권한 부여하기
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);
        //객체 찾는 함수
        findViewByIdFunc();

        //이벤트 함수
        eventHandlerFunc();
    }

    public void eventHandlerFunc() {
        //프레그먼트 이용해서 화면 바꾸기
        mainMenuBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSearch:
                        setFragmentChange(SEARCH);
                        break;
                    case R.id.menuHome:
                        setFragmentChange(HOME);
                        break;
                    case R.id.menuHeart:
                        setFragmentChange(HEART);
                        break;
                    default:
                        Log.d("MainActivity","menuBar error"); break;
                }

                return true;
            }
        });
        setFragmentChange(HOME);

    }



    //메인 메뉴바 화면 바꾸는 함수
    public void setFragmentChange(int i) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (i){
            case SEARCH:
                ft.replace(R.id.frameLayout, fragmentSearch);
                ft.commit();
                break;
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

    //객체 찾는 함수
    public void findViewByIdFunc() {
        toolbar = findViewById(R.id.toolbar);

        mainMenuBar = findViewById(R.id.mainMenuBar);
        frameLayout = findViewById(R.id.frameLayout);

        fragmentSearch = new FragmentSearch();
        fragmentHome = new FragmentHome();
        fragmentHeart = new FragmentHeart();
    }
}