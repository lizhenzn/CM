package com.example.cm.friend.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.friend.FriendPagerAdapter;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.ChatListAdapter;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.MessageManager;


import java.util.ArrayList;
import java.util.List;

import static com.example.cm.MainActivity.setToolbarText;

public class FriendFragment extends Fragment  implements View.OnClickListener {
    private ViewPager viewPager;
    private FriendPagerAdapter friendPagerAdapter;
    private List<Fragment> fragmentlist;
    private TextView sessionTV,contantTV,newFriendTV;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.friend);
        setToolbarText("会话");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

           FragmentManager fragmentManager;

    {
        //调用子FragmentManager替换Fragment
        fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }
        sessionTV=getActivity().findViewById(R.id.session_tv);
        contantTV=getActivity().findViewById(R.id.contant_tv);
        newFriendTV=getActivity().findViewById(R.id.newFriend_tv);
    fragmentlist=new ArrayList<>();
    View view=View.inflate(getActivity(),R.layout.friend,null);
        sessionTV=view.findViewById(R.id.session_tv);
        contantTV=view.findViewById(R.id.contant_tv);
        newFriendTV=view.findViewById(R.id.newFriend_tv);
        viewPager=view.findViewById(R.id.viewPaper);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
        fragmentlist.add(new SessionFragment());
        fragmentlist.add(new ContantFragment());
        fragmentlist.add(new NewFriendFragment());
        friendPagerAdapter=new FriendPagerAdapter(fragmentManager,fragmentlist);
        viewPager.setAdapter(friendPagerAdapter);
        viewPager.setCurrentItem(0);
        setChoosed(0);
   /*
        //长按显示置顶删除等
        chatLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            String []choose={"置顶","删除"};
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setSingleChoiceItems(choose, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {




                    }
                });
                builder.setCancelable(true);
                builder.setIcon(R.drawable.button);
                builder.show();
                Log.d("friend", "onItemClick: ");
                return true;
            }
        });*/
         return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.session_tv:{
                viewPager.setCurrentItem(0);
                setChoosed(0);
            }break;
            case R.id.contant_tv:{
                viewPager.setCurrentItem(1);
                setChoosed(1);
            }break;
            case R.id.newFriend_tv:{
                viewPager.setCurrentItem(2);
                setChoosed(2);
            }break;
            default:break;
        }
    }


    private class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            setChoosed(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
    /*
    * 选中某个页面后设置
    * @param i
    */
    private void setChoosed(int i){
        sessionTV.setTextColor(Color.BLACK);
        contantTV.setTextColor(Color.BLACK);
        newFriendTV.setTextColor(Color.BLACK);
        switch (i){
            case 0:{
                setToolbarText("会话");
                sessionTV.setTextColor(Color.parseColor(ThemeColor.backColorStr));
            }break;
            case 1:{
                setToolbarText("好友");
                contantTV.setTextColor(Color.parseColor(ThemeColor.backColorStr));
            }break;
            case 2:{
                setToolbarText("新朋友");
                newFriendTV.setTextColor(Color.parseColor(ThemeColor.backColorStr));
            }break;
            default:break;
        }
    }

}
