package com.example.cm.myInfo;

import android.graphics.Bitmap;

public class FriendInfo{
    String userName;   //用户名Jid
    String nicName;    //昵称
    Bitmap headBt;     //头像
    String groupName;  //所属群组
    String headBtRoad; //头像保存路径
    int chated;      //是否在会话列表 0不在 1在
    String sex;
    String email;
    public FriendInfo(){
        this.chated=0; //默认为0
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
}