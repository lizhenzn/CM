package com.example.cm.friend;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

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
    private ImageButton addFriendIB;
    private ExpandableListView expandableListView;
    private boolean work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant);
        init();
        Toolbar toolbar=(Toolbar)findViewById(R.id.contant_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        expandableListView.setAdapter(contantAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //判断会话列表有没有此好友
                /*boolean contain=false;
                FriendInfo friendInfo;
                Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).setChated(1); //设置为在聊天列表
                friendInfo=Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition);
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
                    Connect.dataBaseHelp.changeChatState(userName,1);            //改变数据库中聊天状态
                    //friendInfo.setHeadBt(message.getBody());
                    Connect.friendInfoList.add(Connect.friendInfoList.size(),friendInfo);//
                    List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                    ////messageList.add(message1);
                    Connect.messageMap.put(userName,messageList);
                    Log.d("ContantClick点击的好友条目名", "onChildClick: "+userName);
                }*/
                String userName=Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).getUserName();
                Intent intent=new Intent(ContantActivity.this, FriendInfoActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("groupPosition",groupPosition);
                intent.putExtra("childPosition",childPosition);
                startActivity(intent);
                return true;
            }
        });
        //TODO 长按出现添加分组选项
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });

        addFriendIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ContantActivity.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (work) {
                    if(Connect.groupInfoListChanged) {
                        ContantActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contantAdapter.notifyDataSetChanged();
                                Connect.groupInfoListChanged = false;
                            }
                        });
                    }
                }
            }
        }).start();

    }
    public void init(){
        expandableListView=(ExpandableListView)findViewById(R.id.expendLV);
        contantAdapter=new ContantAdapter(this);
        addFriendIB=(ImageButton)findViewById(R.id.addFriendIB);
        work=true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:this.finish();break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (work) {
                    if(Connect.groupInfoListChanged) {
                        ContantActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contantAdapter.notifyDataSetChanged();
                                Connect.groupInfoListChanged = false;
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        work=false;
    }
}
