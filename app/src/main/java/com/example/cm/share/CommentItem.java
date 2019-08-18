package com.example.cm.share;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentItem {
    int idHeadImage;
    String Number;
    String UserName;
    String CommentText;

    public CommentItem(int idHeadImage, String Number,String UserName, String CommentText) {
        this.idHeadImage = idHeadImage;
        this.Number = Number;
        this.UserName = UserName;
        this.CommentText = CommentText;
    }

    int getIdHeadImage() {
        return idHeadImage;
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
