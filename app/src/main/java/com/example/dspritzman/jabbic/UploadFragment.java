package com.example.dspritzman.jabbic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Troy on 1/23/2016.
 */
public class UploadFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView( inflater, container, savedInstanceState );

        View rootView = inflater.inflate(R.layout.upload_view, container, false);

        return rootView;
    }
}
