package com.yuccaworld.yuccaslim;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GuideFragment extends Fragment {
    public GuideFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.guide_fragment, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
}
