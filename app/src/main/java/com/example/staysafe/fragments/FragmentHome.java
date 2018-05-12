package com.example.staysafe.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.staysafe.MapFragmentView;
import com.example.staysafe.NavDrawerActivity;
import com.example.staysafe.R;
import com.here.android.mpa.common.MapEngine;

public class FragmentHome extends Fragment {

    private MapFragmentView m_mapFragmentView;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        try {
            view =  inflater.inflate(R.layout.fragment_home, container, false);
            m_mapFragmentView = new MapFragmentView(getActivity());
        } catch (Exception e){
            getActivity().finish();
            Intent intent = new Intent(getContext(), NavDrawerActivity.class);
            startActivity(intent);
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MapEngine.isInitialized() && m_mapFragmentView != null){
//            m_mapFragmentView.pauseMapFragment();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MapEngine.isInitialized() && m_mapFragmentView != null){
//            m_mapFragmentView.resumeMapFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Tag", "FragmentA.onDestroyView() has been called.");
        if (MapEngine.isInitialized() && m_mapFragmentView != null){
//            m_mapFragmentView.destroyMapFragment();
        }
    }
}
