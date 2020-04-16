package com.example.cm.friend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class FriendPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private List<Fragment> list;
    public FriendPagerAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        this.fm=fm;
        this.list=list;
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
