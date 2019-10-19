package com.example.meme_generator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class CompleteActivity extends AppCompatActivity {
    private Meme meme;
    private String url, mname, id;
    private ArrayList<Meme> memes;
    private Bitmap bitmap;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        context = this;
        initialiseUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.save:
//                save();
//                return true;
            case R.id.share:
                share();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseUI(){
        meme = getIntent().getParcelableExtra("customMeme");

        if (meme != null) {
            //Log.d("debug received url: ", meme.getUrl() + ":" + meme.getName());
            url = meme.getUrl();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.memeFrag, MemeDisplayFragment.newInstance(url));
            transaction.commit();
            //setSharedIntent();
        } else {
            Toast.makeText(this.getApplicationContext(),
                    String.format("Something went wrong, please try again later."), Toast.LENGTH_SHORT).show();
        }
    }

    private void share() {
        ImageView imageView = findViewById(R.id.customView);
        new GetImageFromURL(imageView).execute(url);
    }

    private class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {
        ImageView img;

        public GetImageFromURL(ImageView img) {
            this.img = img;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String urlDisplay = url[0];
            bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
            setSharedIntent(bitmap);
            //Log.d( "debug bitmap width: ", String.valueOf(bitmap.getWidth()));
        }
    }

    /*
        Reference:
        https://stackoverflow.com/questions/9049143/android-share-intent-for-a-bitmap-is-it-possible-not-to-save-it-prior-sharing
     */
    private void setSharedIntent(Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File memePath = new File(context.getCacheDir(), "images");
        File newFile = new File(memePath, "image.png");
        Uri uri = FileProvider.getUriForFile(context, "com.example.meme_generator.fileprovider", newFile);
        if (uri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(uri, getContentResolver().getType(uri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share your meme"));
        }
    }

    /*
     *  Save function is not yet implemented!
     *
    private void save() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View promptView = inflater.inflate(R.layout.input_layout, null);
        final AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
        alertBox.setTitle("Save your meme");
        alertBox.setView(promptView);

        alertBox.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText name = promptView.findViewById(R.id.inputName);
                        mname = name.getText().toString();
                        writeToFile();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alertBox.create();
        alert.show();

        //customise buttons
        Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setTextColor(Color.BLUE);
        Button negative = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        negative.setTextColor(Color.RED);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) positive.getLayoutParams();
        layoutParams.weight = 10;
        positive.setLayoutParams(layoutParams);
        negative.setLayoutParams(layoutParams);
    }

    private void writeToFile() {
        memes = new ArrayList<>();
        String fileName = "customMemes.txt";
        String contentToWrite = "";

        Random r = new Random();
        int rand = r.nextInt((500-1)+1)+1;
        String randID = String.valueOf(rand);
        id = mname+randID;

        meme = new Meme(id, mname, url);
        memes.add(meme);
        contentToWrite += memes.toString();

        try {
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(contentToWrite.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this.getApplicationContext(),
                String.format("Save your new meme successfully"), Toast.LENGTH_SHORT).show();
    }
    */
}
