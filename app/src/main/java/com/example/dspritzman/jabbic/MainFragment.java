package com.example.dspritzman.jabbic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Troy on 1/23/2016.
 */
public class MainFragment extends Fragment {

    MainFragmentListener MCallback;

    // Container Activity must implement this interface
    public interface MainFragmentListener {
        public void cameraButton();
        public void searchButton();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            MCallback = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private String[] tags = {"People", "Cold", "Mountains", "Trees", "Nature", "Snow", "Dave Small", "Rare Pepes"};

    private int expectedImages = 0;
    private int count = 0;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();


    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView( inflater, container, savedInstanceState );

        View rootView = inflater.inflate(R.layout.main_view, container, false);

        Button cameraButton = (Button) rootView.findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                MCallback.cameraButton();
            }
        });

        Button searchButton = (Button) rootView.findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                MCallback.searchButton();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        GridView gridview = (GridView) getView().findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.tag_scrolls);
        for (String s: tags) {
            Button b = new Button(getActivity());
            b.setText(s);
            layout.addView(b);
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClarifaiMaster master = ClarifaiMaster.getInstance();
                        final String[] results = master.getTags("http://www.cise.ufl.edu/~dts/personal/who/CamelBack.jpg");
                        if (results != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0; i < results.length; ++i) {
                                        Toast.makeText(getActivity(), results[i], Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }).start();

            }
        });

    }

    public synchronized void setExpectedImages(int num) {
        expectedImages = num;
        count = 0;
        images.clear();

    }

    public synchronized boolean receieveImage(String img) {
        byte[] imageBytes = Base64.decode(img.getBytes(), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        images.add(bm);

        if (++count == expectedImages) {
            //do something to handle setting the view.
        }

        return true;
    }

}
