package com.example.cm.friend.AddFriend;

import android.graphics.Bitmap;

public class SearchResultItem {
    private String user;
    private Bitmap bitmap;

    public void setUser(String user) {
        this.user = user;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUser() {
        return user;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
