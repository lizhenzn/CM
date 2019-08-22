package com.example.cm.share;

public class ShareItem {
    private int idHeadImage;
    private String UserName;
    private int idClothesUp;
    private int idClothesDown;
    private String description;
    private int idGiveLike;
    private int idComment;

    public ShareItem(int idHeadImage,String UserName,int idClothesUp, int idClothesDown, String description, int idGiveLike, int idComment) {
        this.idHeadImage = idHeadImage;
        this.UserName = UserName;
        this.idClothesUp = idClothesUp;
        this.idClothesDown = idClothesDown;
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
    int getIdClothesUp() {
        return idClothesUp;
    }

    int getIdClothesDown() {
        return idClothesDown;
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
