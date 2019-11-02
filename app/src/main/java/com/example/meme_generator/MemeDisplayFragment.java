package com.example.meme_generator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MemeDisplayFragment extends Fragment {

    public MemeDisplayFragment() {
        // Required empty public constructor
    }

    public static MemeDisplayFragment newInstance(String url) {
        MemeDisplayFragment fragment = new MemeDisplayFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        //Log.d("debug frag: ", url);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meme_display, container, false);
        String url = getArguments().getString("url");

        ImageView img = view.findViewById(R.id.customView);
        Picasso.get().load(url).into(img);
        return view;
    }
}
