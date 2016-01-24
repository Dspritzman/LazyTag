package com.example.dspritzman.jabbic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoReviewFragment extends Fragment {

    PhotoFragmentListener PCallback;

    // Container Activity must implement this interface
    public interface PhotoFragmentListener {
        public void UploadButton(Bitmap bitmap, String[] tags, String byteArray);
        public void CancelButton();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            PCallback = (PhotoFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private Uri fileUri;
    private Bitmap bitmap;
    private String[] tags;
    private String byteArray;

    View view;
    public PhotoReviewFragment()
    {

    }


    public void newPicture(Bitmap picture, String[] tags, String byteArray)
    {
        bitmap = picture;
        this.tags = tags;
        this.byteArray = byteArray;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        view = inflater.inflate(R.layout.fragment_photo_review, container, false);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.tag_scrolls);

        view.setHorizontalScrollBarEnabled(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);

        layout.setHorizontalScrollBarEnabled(false);
        for (String s: tags) {
            Button b = new Button(this.getActivity());
//
            b.setLayoutParams(params);
            b.setBackgroundColor(getResources().getColor(R.color.button));
            b.setText(s);
            b.setPadding(5,5,5,5);
            layout.addView(b);
        }

        CardView cardView = (CardView) view.findViewById(R.id.photoFrag);
        cardView.setPreventCornerOverlap(false);

        ImageView imageView = (ImageView) view.findViewById(R.id.ReviewImageView);
        imageView.setImageBitmap(bitmap);

        Button uploadButton = (Button) view.findViewById(R.id.Upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                PCallback.UploadButton(bitmap, tags, byteArray);
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.Cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                PCallback.CancelButton();
            }
        });

        return view;
    }

}
