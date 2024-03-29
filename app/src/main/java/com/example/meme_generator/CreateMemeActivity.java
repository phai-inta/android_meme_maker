package com.example.meme_generator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        //System.out.println("debug selected url " + meme.getUrl());
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
                if (inputBottom.isEmpty() && inputTop.isEmpty()) {
                    isTextEmpty();
                } else {
                    new RequestTask().execute();
                }
            }
        });
    }

    private void isTextEmpty() {
        AlertDialog.Builder msg = new AlertDialog.Builder(this);
        msg.setTitle("Confirm");
        msg.setMessage("Are you sure you want to proceed without any text?");
        //msg.setCancelable(true);
        msg.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new RequestTask().execute();
            }
        });
        msg.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = msg.create();
        alert.show();
        //msg.show();
        Button continueBtn = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        continueBtn.setTextColor(Color.BLUE);
    }

    private class RequestTask extends AsyncTask<String, String, String> {
        HttpURLConnection connection = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String data ="";
            String param = "";
            if (inputTop.isEmpty() && inputBottom.isEmpty()) {
                returnUrl = meme.getUrl();
                Log.d("debug: no param ", returnUrl);
            } else {
                param = "https://api.imgflip.com/caption_image?username=phai.intathep&password=password";
                param += "&template_id=" + inputID;
                param += "&text0=" + inputTop;
                param += "&text1=" + inputBottom;
                Log.d("debug: url with params ", param);

                try {
                    URL url = new URL(param);
                    connection = (HttpURLConnection) url.openConnection();

                    int code = connection.getResponseCode();
                    if (code != 200) {
                        throw new IOException("Invalid response: " + code);
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        data = data + line;
                    }
                    JSONObject root = new JSONObject(data);
                    JSONObject dataObject = root.getJSONObject("data");
                    returnUrl = dataObject.getString("url");
                    Log.d("debug send custom url: ", returnUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return returnUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            memeArray = new ArrayList<>();
            meme = new Meme((String.valueOf(memeArray.size())), "custom", returnUrl);
            memeArray.add(meme);
            Intent intent = new Intent(context, CompleteActivity.class);
            intent.putExtra("customMeme", meme);
            context.startActivity(intent);
        }
    }
}