package com.example.cm;

import com.example.cm.friend.fragment.FriendFragment;
import com.example.cm.match.MatchFragment;
import com.example.cm.share.ShareFragment;
import com.example.cm.wardrobe.WardrobeFragment;

public enum Tabs {
    Match(0,"搭配",R.drawable.match, MatchFragment.class),
    Share(1,"广场",R.drawable.share, ShareFragment.class),
    Friend(2,"会话",R.drawable.friend, FriendFragment.class),
    Wardrobe(3,"衣柜",R.drawable.wardrobe, WardrobeFragment .class);

    private int i;
    private String name;
    private int icon;
    private Class<?> aClass;
    Tabs(int i,String name,int icon,Class<?> aClass){
        this.i=i;
        this.name=name;
        this.icon=icon;
        this.aClass=aClass;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }
}
