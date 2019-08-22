package com.example.cm.share;

import java.io.File;

public class ShareItem {
    private int idHeadImage;
    private String UserName;
    private File ClothesUp;
    private File ClothesDown;
    private  int idClothesUp;
    private  int idClothesDown;
    private String description;
    private int idGiveLike;
    private int idComment;
    private boolean blankItemFlag;

    public ShareItem(int idHeadImage,String UserName,File ClothesUp, File ClothesDown, String description, int idGiveLike, int idComment) {
        this.idHeadImage = idHeadImage;
        this.UserName = UserName;
        this.ClothesUp = ClothesUp;
        this.ClothesDown = ClothesDown;
        this.description = description;
        this.idGiveLike = idGiveLike;
        this.idComment = idComment;
        blankItemFlag=false;
    }

    public ShareItem(int idHeadImage,String UserName,int idClothesUp, int  idClothesDown, String description, int idGiveLike, int idComment) {
        this.idHeadImage = idHeadImage;
        this.UserName = UserName;
        this.idClothesUp = idClothesUp;
        this.idClothesDown = idClothesDown;
        this.description = description;
        this.idGiveLike = idGiveLike;
        this.idComment = idComment;
        blankItemFlag=true;
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
    int getIdClothesUp(){ return idClothesUp;}

    File getClothesDown() {
        return ClothesDown;
    }
    int getIdClothesDown(){return idClothesDown;}
    String getDescription() {
        return description;
    }

    int getIdGiveLike() {
        return idGiveLike;
    }

    int getIdComment() {
        return idComment;
    }

    boolean isBlankItemFlag() {
        return blankItemFlag;
    }
}
