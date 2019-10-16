package com.example.meme_generator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.meme_generator.Meme;
import com.squareup.picasso.Picasso;

public class HomepageActivity extends AppCompatActivity {
    private Meme meme;
    private TextView tv;
    private ArrayList<Meme> memes;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String memeUrl = "https://api.imgflip.com/get_memes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        initialiseUI();
        new RequestTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addItem:
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialiseUI() {
        recyclerView = findViewById(R.id.recycleView);
    }

    private class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params){
            String data = "";
            HttpURLConnection connection = null;

            try {
                URL url = new URL(memeUrl);
                Log.d("debug: url ", memeUrl);
                connection = (HttpURLConnection) url.openConnection();

                int code = connection.getResponseCode();
                if (code != 200) {
                    throw new IOException("Invalid response: " + code);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = "";
                while((line = reader.readLine()) != null) {
                    data = data + line;
                }
                //Log.d("debug: data", data);

                JSONObject root = new JSONObject(data);
                JSONObject dataObject = root.getJSONObject("data");
                JSONArray memeArray = dataObject.getJSONArray("memes");
                memes = new ArrayList<>();

                for (int i=90; i< memeArray.length(); i++) {
                    JSONObject memeObject = memeArray.getJSONObject(i);
                    meme = new Meme(memeObject.getString("id"), memeObject.getString("name"), memeObject.getString("url"));
                    memes.add(meme);
                }
                //Log.d( "debug url: ", memes.get(1).getUrl());
                //Log.d("debug size: ", String.valueOf(memes.size()));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            //tv.setText(memes.get(1).getName());
            super.onPostExecute(result);
            displayMeme();
            //Log.d("debug: onPostExecute ", result);
        }
    }

    private void displayMeme() {
        if (memes.size() > 0) {
            //set a layout manager
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setHasFixedSize(true);
            //call the adapter
            adapter = new MemeAdapter(memes);
            recyclerView.setAdapter(adapter);
        }
    }
}

