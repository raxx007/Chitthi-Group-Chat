package com.digipodium.chitthi.Models;

public class Upload {
    String name, mImageURL;
    public Upload() {
    }

    public Upload(String name, String mImageURL) {
        this.name = name;
        this.mImageURL = mImageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }
}

