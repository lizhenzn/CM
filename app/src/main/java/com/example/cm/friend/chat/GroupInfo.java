package com.example.cm.friend.chat;

import com.example.cm.myInfo.FriendInfo;

import java.util.List;

public class GroupInfo {
    String groupName;
    List<FriendInfo> friendInfoList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<FriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public void setFriendInfoList(List<FriendInfo> friendInfoList) {
        this.friendInfoList = friendInfoList;
    }
}
