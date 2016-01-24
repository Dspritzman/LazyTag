package com.example.dspritzman.jabbic;

/**
 * Created by goref_000 on 1/23/2016.
 */
public class Image {
    private String bin;
    private int[] tags;

    public Image(String binin,int[] tagsin){bin = binin;tags = tagsin;}

    public String getBin(){
        return bin;
    }

    public int[] getTags(){
        return tags;
    }


}
