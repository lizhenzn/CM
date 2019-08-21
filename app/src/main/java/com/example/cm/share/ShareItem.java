package com.example.cm.share;

import java.io.File;

public class ShareItem {
    private int idHeadImage;
    private String UserName;
    private File ClothesUp;
    private File ClothesDown;
    private String description;
    private int idGiveLike;
    private int idComment;

    public ShareItem(int idHeadImage,String UserName,File ClothesUp, File ClothesDown, String description, int idGiveLike, int idComment) {
        this.idHeadImage = idHeadImage;
        this.UserName = UserName;
        this.ClothesUp = ClothesUp;
        this.ClothesDown = ClothesDown;
        this.description = description;
        this.idGiveLike = idGiveLike;
        this.idComment = idComment;
    }

    int getIdHeadImage() {
        return idHeadImage;
    }

    String getUserName() {
        return UserName;
    }
    File getClothesUp() {
        return ClothesUp;
    }

    File getClothesDown() {
        return ClothesDown;
    }

    String getDescription() {
        return description;
    }

    int getIdGiveLike() {
        return idGiveLike;
    }

    int getIdComment() {
        return idComment;
    }
}
