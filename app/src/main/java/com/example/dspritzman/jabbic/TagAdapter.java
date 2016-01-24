package com.example.dspritzman.jabbic;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * Created by Troy on 1/23/2016.
 */
public class TagAdapter extends BaseAdapter {
    private Context mContext;

    public TagAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            button = new Button(new ContextThemeWrapper(mContext,android.R.style.Widget_Material_Button_Borderless_Colored),null,android.R.style.Widget_Material_Button_Borderless_Colored);
//            imageView.setMaxWidth(parent.getWidth() / 2 - 40);
//            imageView.setMaxHeight(parent.getHeight() / 3 - 40);
            button.setLayoutParams(new GridView.LayoutParams(150, 100));
            button.setPadding(5, 5, 5, 5);
        } else {
            button = (Button) convertView;
        }

        button.setText("Test");
        return button;
    }

    // references to our images
    private Integer[] mThumbIds = {

    };
}
