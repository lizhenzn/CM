package com.example.cm.share;

import android.graphics.Bitmap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentItem {
    Bitmap HeadImage;
    String Number;
    String UserName;
    String CommentText;

    public CommentItem(Bitmap HeadImage, String Number,String UserName, String CommentText) {
        this.HeadImage = HeadImage;
        this.Number = Number;
        this.UserName = UserName;
        this.CommentText = CommentText;
    }

    Bitmap getHeadImage() {
        return HeadImage;
    }

    String getNumber() {
        return Number;
    }
    String getUserName() {
        return UserName;
    }

    String getCommentText() {
        return CommentText;
    }
}
