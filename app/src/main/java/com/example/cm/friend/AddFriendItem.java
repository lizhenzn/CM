package com.example.cm.friend;

import com.example.cm.myInfo.FriendInfo;

public class AddFriendItem {
    private FriendInfo friendInfo;        //添加的用户信息
    private String result;                //结果
    private String reason;                //原因

    public FriendInfo getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
