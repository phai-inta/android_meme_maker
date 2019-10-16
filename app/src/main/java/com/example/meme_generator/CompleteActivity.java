package com.example.meme_generator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class CompleteActivity extends AppCompatActivity {
    private Meme meme;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        initialiseUI();
    }

    private void initialiseUI(){
        meme = getIntent().getParcelableExtra("customMeme");
        if (meme != null) {
            Log.d("debug received url: ", meme.getUrl() + ":" + meme.getName());
            ImageView imageView = findViewById(R.id.yourMeme);
            Picasso.get().load(meme.getUrl()).into(imageView);
        } else {
            Log.d( "debug received url: ", "null url");
            Toast.makeText(this.getApplicationContext(),
                    String.format("Something went wrong, please try again later."), Toast.LENGTH_SHORT).show();
        }
    }
}
