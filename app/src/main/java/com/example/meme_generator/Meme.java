package com.example.meme_generator;

import android.os.Parcel;
import android.os.Parcelable;

public class Meme implements Parcelable {
    private String id, name, url;

    public Meme(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    protected Meme(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<Meme> CREATOR = new Creator<Meme>() {
        @Override
        public Meme createFromParcel(Parcel parcel) {
            return new Meme(parcel);
        }

        @Override
        public Meme[] newArray(int i) {
            return new Meme[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeString(this.url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public int size() {
        return id.length();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return this.id + "," + this.name + "," + this.url;
    }
}
