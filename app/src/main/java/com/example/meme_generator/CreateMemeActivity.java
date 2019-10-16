package com.example.meme_generator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CreateMemeActivity extends AppCompatActivity {
    private Meme meme;
    private String inputTop, inputBottom, inputID, returnUrl;
    private Button createBtn;
    private Context context;
    private ArrayList<Meme> memeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
        context = this;
        initialiseUI();
        executePost();
    }

    private void initialiseUI() {
        meme = getIntent().getParcelableExtra("selectedMeme");

        ImageView imageView = findViewById(R.id.imageView);
        Picasso.get().load(meme.getUrl()).into(imageView);
        System.out.println("debug selected url" + meme.getUrl());
        TextView caption = findViewById(R.id.caption);
        caption.setText(meme.getName());
    }

    private void executePost() {
        createBtn = findViewById(R.id.createButton);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText topText = findViewById(R.id.inputTop);
                EditText bottomText = findViewById(R.id.inputBottom);
                inputTop = topText.getText().toString();
                inputBottom = bottomText.getText().toString();
                inputID = meme.getId();
                new RequestTask().execute();
                //Intent intent = new Intent(view.getContext(), CompleteActivity.class);
                //intent.putExtra("customMemeUrl", returnUrl);
                //view.getContext().startActivity(intent);
            }
        });
    }

    private class RequestTask extends AsyncTask<String, String, String> {
        String memeUrl = "https://api.imgflip.com/caption_image?";
        HttpURLConnection connection = null;
        String data ="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //https://api.imgflip.com/caption_image?username=phai.intathep&password=password&template_id=112126428&text0=Hey&text1=Yo
            String param = "https://api.imgflip.com/caption_image?username=phai.intathep&password=password";
            param += "&template_id=" + inputID;
            param += "&text0=" + inputTop;
            param += "&text1=" + inputBottom;
            //Log.d("debug: url with params ", param);

            try {
                URL url = new URL(param);
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
                //Log.d("debug data: ", data);
                JSONObject root = new JSONObject(data);
                JSONObject dataObject = root.getJSONObject("data");
                returnUrl = dataObject.getString("url");
                Log.d("debug send custom url: ", returnUrl);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            memeArray = new ArrayList<>();
            meme = new Meme((String.valueOf(memeArray.size()+1)), "custom", returnUrl);
            memeArray.add(meme);
            Log.d("debug custom2 url: ", returnUrl);
            //Log.d("debug url: ", returnUrl + String.valueOf(meme.size()));

            Intent intent = new Intent(context, CompleteActivity.class);
            intent.putExtra("customMeme", meme);
            context.startActivity(intent);
            //after();
//            Intent intent = new Intent(context, CompleteActivity.class);
//            //intent.setClass(getApplicationContext(), CompleteActivity.class);
//            intent.putExtra("customMemeUrl", returnUrl);
//            context.startActivity(intent);

        }
    }
}
//    Meme meme = new Meme(currMeme.getId(), currMeme.getName(), currMeme.getUrl());
////    Intent intent = new Intent(v.getContext(), CreateMemeActivity.class);
////                intent.putExtra("selectedMeme", meme);
////                        v.getContext().startActivity(intent);