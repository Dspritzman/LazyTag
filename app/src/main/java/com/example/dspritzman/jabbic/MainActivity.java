package com.example.dspritzman.jabbic;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements CameraFragment.CameraFragmentListener,
        MainFragment.MainFragmentListener, PhotoReviewFragment.PhotoFragmentListener {

    @Override
    public void UploadButton(Bitmap bitmap, String[] tags, String byteArray)
    {
        addNewImage(byteArray, tags);
        getTopTags();
    }

    @Override
    public void CancelButton()
    {
        makeCameraFragment();
    }

    @Override
    public void cameraButton()
    {
        makeCameraFragment();
    }

    @Override
    public void searchButton()
    {
        makeSearchFragment();
    }


    @Override
    public void onPictureTaken(Uri picture) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(picture.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bmRotated = rotateBitmap(bitmap, orientation);
        final Bitmap dstBmp;

        if (bmRotated.getWidth() >= bmRotated.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    bmRotated,
                    bmRotated.getWidth()/2 - bmRotated.getHeight()/2,
                    0,
                    bmRotated.getHeight(),
                    bmRotated.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    bmRotated,
                    0,
                    bmRotated.getHeight()/2 - bmRotated.getWidth()/2,
                    bmRotated.getWidth(),
                    bmRotated.getWidth()
            );
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dstBmp.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        final byte[] byteArray = stream.toByteArray();

        String[] tags;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ClarifaiMaster master = ClarifaiMaster.getInstance();

                final String[] results = master.getTags(byteArray);
                if (results != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < results.length; ++i) {
                                //Toast.makeText(getActivity(), results[i], Toast.LENGTH_SHORT).show();
                                Log.d("MyCameraApp", results[i]);
                            }
                        }
                    });


                    String temp = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    //addNewImage(temp, results);
                    makePhotoFragment(dstBmp, results, temp);
                }
            }
        }).start();



    }

    public boolean addNewImage(final String bin, final String[] tags){
        final boolean[] success = new boolean[1];
        success[0] = false;
        final Firebase ref = new Firebase("https://flickering-inferno-3473.firebaseio.com");
        Firebase images = ref.child("Images");
        images.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " images");
                long imageCount = dataSnapshot.getChildrenCount();
                int ourID = (int) imageCount + 1;
                //submit tags
                int[] tagIndexes = submitTags(tags, ourID);
                Image newImage = new Image(bin, tagIndexes);
                String IDname = ourID + "";
                Firebase newRef = ref.child("Images").child(IDname);
                newRef.setValue(newImage);
                success[0] = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                success[0] = false;
            }
        });
        return success[0];
    }

    public void getTopTags(){
        final Firebase ref = new Firebase("https://flickering-inferno-3473.firebaseio.com");
        Query queryRef = ref.child("Tags").orderByKey().limitToFirst(16);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String tag = (String) dataSnapshot.child("tag").getValue();
                //System.out.println(dataSnapshot.child("tag"));
                final int[] concurrentAdds = new int[1];
                concurrentAdds[0] = 0;
                String[] tagsInDB = new String[(int)dataSnapshot.getChildrenCount()];
                int curIndex = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    //System.out.println(child.child("tag").getValue());
                    tagsInDB[curIndex] = (String)child.child("tag").getValue();
                    curIndex++;
                }
                //System.out.println(tagsInDB.length);
                //for (String s : tagsInDB) {
                //    System.out.print(s);
                //}

                updateMainFragment(tagsInDB);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public int[] submitTags(final String[] tags, final int imageID){
        final int[] returnTagIDs = new int[tags.length];
        final int[] returnTagIDsIndex = new int[1];
        returnTagIDsIndex[0] = 0;
        final Firebase ref = new Firebase("https://flickering-inferno-3473.firebaseio.com");
        Query queryRef = ref.child("Tags").orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String tag = (String) dataSnapshot.child("tag").getValue();
                //System.out.println(dataSnapshot.child("tag"));
                final int[] concurrentAdds = new int[1];
                concurrentAdds[0] = 0;
                String[] tagsInDB = new String[(int) dataSnapshot.getChildrenCount()];
                int curIndex = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    System.out.println(child.child("tag").getValue());
                    tagsInDB[curIndex] = (String) child.child("tag").getValue();
                    curIndex++;
                }
                for (String inputTags : tags) {
                    System.out.print(inputTags);
                    boolean exists = false;
                    for (String DBTags : tagsInDB) {
                        System.out.println(" =? " + DBTags);
                        if (inputTags.equals(DBTags)) {
                            exists = true;
                            int index = (java.util.Arrays.asList(tagsInDB).indexOf(DBTags) + 1);
                            System.out.println("tag " + inputTags + " exists as: " + index);
                            //returnTagIDs[returnTagIDsIndex[0]] = index;
                            final Firebase newRef = ref.child("Tags").child(index + "").child("images");
                            newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int count = (int) dataSnapshot.getChildrenCount();
                                    newRef.child(count + "").setValue(imageID);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                            Firebase newerRef = ref.child("Images").child(imageID + "").child("tags").child(returnTagIDsIndex[0] + "");
                            newerRef.setValue(index);
                            returnTagIDsIndex[0]++;
                        }
                    }
                    if (!exists) {
                        //create new tag item
                        final String finalInputTags = inputTags;
                        System.out.println("tag " + inputTags + " is new");
                        ref.child("Tags").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //System.out.println("There are " + dataSnapshot.getChildrenCount() + " images");
                                long imageCount = dataSnapshot.getChildrenCount();
                                int ourID = (int) imageCount + 1;
                                String IDname = (ourID + concurrentAdds[0]) + "";
                                Tag newTag = new Tag(tag, imageID);
                                Firebase newRef = ref.child("Tags").child(IDname);
                                int[] imageIDs = {imageID};
                                newRef.child("images").setValue(imageIDs);
                                newRef.child("tag").setValue(finalInputTags);
                                //newRef.setValue(newTag);
                                //returnTagIDs[returnTagIDsIndex[0]] = ourID + concurrentAdds[0];
                                //returnTagIDsIndex[0]++;
                                Firebase newerRef = ref.child("Images").child(imageID + "").child("tags").child(returnTagIDsIndex[0] + "");
                                newerRef.setValue(ourID + concurrentAdds[0]);
                                returnTagIDsIndex[0]++;
                                concurrentAdds[0]++;
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                        //return!
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return returnTagIDs;
    }



    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    CameraFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        mFragment = new CameraFragment();

        if (savedInstanceState == null) {
            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment, "MAIN2")
                    .commit();*/

            getTopTags();
        }

    }

    public void updateMainFragment(String[] tags)
    {
        makeMainFragment(tags);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    FRAGMENT SWAP METHODS
     */

    private void makePhotoFragment(Bitmap bitmap, String[] tags, String byteArray) {
        PhotoReviewFragment fragment = new PhotoReviewFragment();
        //mFragment = fragment;

        fragment.newPicture(bitmap, tags, byteArray);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "MAIN").commit();

    }

    private void makeCameraFragment() {
        CameraFragment fragment = new CameraFragment();
        mFragment = fragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "CAMERA").commit();

    }

    /*
    private void makeUploadFragment() {
        UploadFragment fragment = new UploadFragment();
        //mFragment = fragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "UPLOAD").addToBackStack(null).commit();
    }*/

    private void makeSearchFragment() {
        SearchFragment fragment = new SearchFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "SEARCH").addToBackStack(null).commit();
    }

    private void makeMainFragment(String[] tags) {
        MainFragment fragment = new MainFragment();
        fragment.setTags(tags);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "MAIN").addToBackStack(null).commit();
    }

    /*
    private void makeImageViewFragment() {
        ImageViewFragment fragment = new ImageViewFragment();
        //mFragment = fragment;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "IMAGE_VIEW").addToBackStack(null).commit();
    }*/

    /*
    ACTION LISTENERS
     */

    public void onMain(View view) {
        //makeMainFragment();
    }
    public void onCamera(View view) {
        makeCameraFragment();
    }

   /* public void onImageView(View view) {
        makeImageViewFragment();
    }

    public void onSearch(View view) {
        makeSearchFragment();
    }

    public void onUpload(View view) {
        makeUploadFragment();
    }
    */

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CameraFragment cameraFragment = new CameraFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentHolder, cameraFragment);
        transaction.addToBackStack(null);

        //Commit the transaction
        transaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onPictureTaken(Uri pictureUri)
    {

        Log.d("MyCameraApp", "Start photo fragment");

        PhotoReviewFragment photoFragment = new PhotoReviewFragment();


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragmentHolder, photoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/
}



 