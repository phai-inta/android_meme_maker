package com.example.meme_generator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {
    private Meme meme;
    private TextView tv;
    private ArrayList<Meme> memes;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String memeUrl = "https://api.imgflip.com/get_meme/";
    private ProgressBar spinner;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        context = this;
        new RequestTask().execute();
        initialiseUI();
    }

    private void initialiseUI() {
        recyclerView = findViewById(R.id.recycleView);
    }

    private class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            spinner = findViewById(R.id.progressBar);
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params){
            String data = "";
            HttpURLConnection connection = null;

            try {
                URL url = new URL(memeUrl);
                //Log.d("debug: url ", memeUrl);
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
        if (memes != null) {
            //set a layout
            //Log.d("debug size: ", String.valueOf(memes.size()));
            spinner.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setHasFixedSize(true);
            //call the adapter
            adapter = new MemeAdapter(memes);
            recyclerView.setAdapter(adapter);
        } else {
            spinner.setVisibility(View.INVISIBLE);
            AlertDialog.Builder msg = new AlertDialog.Builder(this);
            msg.setTitle("Network Error");
            msg.setMessage("Please try again later, sorry.");
            msg.setCancelable(true);
            msg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent it = new Intent(context, MainActivity.class);
                    context.startActivity(it);
                }
            });
            AlertDialog alert = msg.create();
            alert.show();
            Button okBtn = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            okBtn.setTextColor(Color.BLUE);
        }
    }
}

