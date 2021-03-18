package com.example.mymusicplayerproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentHeart extends Fragment {
    private RecyclerView recycleList;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_mainfragment3,container,false);
        findViewByIdFunc();
        eventHandlerFunc();

        return view;
    }

    public void eventHandlerFunc() {

    }

    public void findViewByIdFunc() {
        recycleList = view.findViewById(R.id.recycleList);
    }
}
