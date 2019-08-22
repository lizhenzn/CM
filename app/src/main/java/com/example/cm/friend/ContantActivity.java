package com.example.cm.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.util.Connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.UserInfo;

public class ContantActivity extends AppCompatActivity {
    private ContantAdapter contantAdapter;
    public static String friendName;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant);
        init();
        expandableListView.setAdapter(contantAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //判断会话列表有没有此好友
                boolean contain=false;
                String userName=Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).getUserName();
                for(int i=0;i<Connect.friendInfoList.size();i++){
                    if(Connect.friendInfoList.get(i).getUserName().equals(userName)){     //会话列表包含此好友
                        //list.get(i).get(message.getFrom()).add(message1);
                        contain=true;
                        Log.d("ContantClick点击的好友条目名", "onChildClick: "+userName);
                        break;
                    }
                }
                if(!contain){  //会话列表不包含此好友
                    FriendInfo friendInfo=new FriendInfo();
                    friendInfo.setUserName(userName);
                    //friendInfo.setHeadBt(message.getBody());
                    Connect.friendInfoList.add(Connect.friendInfoList.size(),friendInfo);
                    List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                    ////messageList.add(message1);
                    Connect.messageMap.put(userName,messageList);
                    Log.d("ContantClick点击的好友条目名", "onChildClick: "+userName);
                }
                friendName=userName;
                Intent intent=new Intent(ContantActivity.this, ChatActivity.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
                return true;
            }
        });

    }
    public void init(){
        expandableListView=(ExpandableListView)findViewById(R.id.expendLV);
        contantAdapter=new ContantAdapter(this);
    }



}
