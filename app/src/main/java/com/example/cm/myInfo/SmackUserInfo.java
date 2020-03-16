package com.example.cm.myInfo;

import android.graphics.Bitmap;

import java.util.List;

public class SmackUserInfo {
    String userName;
    String passwd;
    Bitmap headBt;      //头像
    String sex;         //性别
    String height;     //身高
    String email;
    String sortLetter;  //排序用的首字母
    public static List<FriendInfo> friendInfoList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Bitmap getHeadBt() {
        return headBt;
    }

    public void setHeadBt(Bitmap headBt) {
        this.headBt = headBt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public static List<FriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public static void setFriendInfoList(List<FriendInfo> friendInfoList) {
        SmackUserInfo.friendInfoList = friendInfoList;
    }

    public String getSortLetters() {
        return sortLetter;
    }

    public void setSortLetters(String sortLetter) {
        this.sortLetter = sortLetter;
    }
}

