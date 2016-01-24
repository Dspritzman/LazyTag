package com.example.dspritzman.jabbic;

import android.net.Uri;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troy on 1/23/2016.
 */
public class ClarifaiMaster {
    private static ClarifaiMaster instance;
    private ClarifaiClient client;

    private final String CLIENT_ID = "aMfh1A5nJkCHC_xREeVmpssK9PXSfZ054RPRlbv6";
    private final String CLIENT_SECRET = "TG6jFD5kvs01sjG0Hf4e_ANUm3-2FbAO2iq9UsuO";
    private final String ACCESS_TOKEN = "NdXfVTEb0fH7tkGjy7WXnHy6Fjzs6J";


    private ClarifaiMaster() {
        client = new ClarifaiClient(CLIENT_ID, CLIENT_SECRET);
    }

    public static ClarifaiMaster getInstance() {
        if (instance == null) {
            instance = new ClarifaiMaster();
        }
        return instance;
    }

    public String[] getTags(File file) {

        List<RecognitionResult> results= client.recognize(new RecognitionRequest(file));
        ArrayList<String> strings = new ArrayList<>();

        for (RecognitionResult r: results) {
            for (Tag t: r.getTags()) {
                strings.add(t.getName());
            }
        }

        return (String[]) strings.toArray();
    }

    public String[] getTags(URI uri) {
        File file = new File(uri);
        return getTags(file);
    }

    public String[] getTags(String URL) {
        List<RecognitionResult> results= client.recognize(new RecognitionRequest(URL));
        ArrayList<String> strings = new ArrayList<>();

        for (RecognitionResult r: results) {
            for (Tag t: r.getTags()) {
                strings.add(t.getName());
            }
        }

        String[] dummy = new String[strings.size()];
        return strings.toArray(dummy);
    }

    public String[] getTags(byte[] bytes) {
        List<RecognitionResult> results= client.recognize(new RecognitionRequest(bytes));
        ArrayList<String> strings = new ArrayList<>();

        for (RecognitionResult r: results) {
            for (Tag t: r.getTags()) {
                strings.add(t.getName());
            }
        }

        String[] dummy = new String[strings.size()];
        return strings.toArray(dummy);
    }
}
