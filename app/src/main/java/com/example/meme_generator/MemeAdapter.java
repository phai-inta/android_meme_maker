package com.example.meme_generator;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.ViewHolder> {
    ArrayList<Meme> memes;

    //constructor
    public MemeAdapter(ArrayList<Meme> value) {
        memes = value;
    }

    @NonNull
    @Override
    public MemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row, parent, false);
        //v.setOnClickListener(itemOnclickListener);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MemeAdapter.ViewHolder holder, int position) {
        final Meme currMeme = memes.get(position);
        //ImageView imageView = findViewById(R.id.meme_image);
        //holder.nameView.setText(meme.getName());
        //holder.imageView.setImageBitmap(meme.getUrl());
        //Log.d("onBindViewHolder: ", meme.getUrl());
        Picasso.get().load(currMeme.getUrl()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( "debug onclick: ", currMeme.getName());
                Meme meme = new Meme(currMeme.getId(), currMeme.getName(), currMeme.getUrl());
                Intent intent = new Intent(v.getContext(), CreateMemeActivity.class);
                intent.putExtra("selectedMeme", meme);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (memes != null) {
            return memes.size() ;
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView nameView;
        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.meme_image);
            //nameView = v.findViewById(R.id.meme_name);
        }
    }
}