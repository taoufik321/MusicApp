package com.application.musicapp.fragment;

/**
 * Created by Taoufik on 25-12-2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.musicapp.R;

public class Fragment3 extends Fragment {
    public Fragment3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment3, container, false);
        return rootView;
    }
}