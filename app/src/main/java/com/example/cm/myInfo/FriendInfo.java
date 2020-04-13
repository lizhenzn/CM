package com.example.cm.myInfo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cm.MainActivity;
import com.example.cm.R;

import java.io.InputStream;

public class FriendInfo implements Comparable<FriendInfo>{
    String userName;   //用户名Jid
    String nicName;    //昵称
    String noteName;   //备注
    Bitmap headBt;     //头像
    String headBtRoad; //头像保存路径
    int chated;      //是否在会话列表 0不在 1在
    String sex;
    String email;
    String pinyin;
    String firstLetter;     //字母排序索引
    public FriendInfo(){
        this.chated=0; //默认为0
        userName="";
        nicName="点击登录";
        noteName="";
        @SuppressLint("ResourceType") InputStream is1 = MainActivity.getInstance().getResources().openRawResource(R.drawable.unlogin);
        headBt = BitmapFactory.decodeStream(is1);
        sex="secrecy";
        email="";
        firstLetter="#";
        pinyin="";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNicName() {
        return nicName;
    }

    public void setNicName(String nicName) {
        this.nicName = nicName;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
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

    public String getHeadBtRoad() {
        return headBtRoad;
    }

    public void setHeadBtRoad(String headBtRoad) {
        this.headBtRoad = headBtRoad;
    }

    public int getChated() {
        return chated;
    }

    public void setChated(int chated) {
        this.chated = chated;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String sortLetters) {
        this.firstLetter = sortLetters;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int compareTo(FriendInfo another) {
        if (firstLetter.equals("#") && !another.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && another.getFirstLetter().equals("#")){
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(another.getPinyin());
        }
    }
}