package com.example.dspritzman.jabbic;

/**
 * Created by goref_000 on 1/23/2016.
 */
public class Tag {
    private String tag;
    private int[] images;

    public Tag(String tagin, int image){tag = tagin; images = new int[] {image};}

    //public Tag(String tagin, int[] imagesin){tag = tagin;images=imagesin;}

    public String getTag(){
        return tag;
    }

    public int[] getImages(){
        return images;
    }

}
