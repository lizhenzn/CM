package com.example.cm.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.util.Connect;

import java.util.ArrayList;
import java.util.List;


public class FriendInfoActivity extends AppCompatActivity {
   private Button send_btn;
   private TextView setting_btn;
   private String userName;
   private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        init();
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendInfoActivity.this, FriendSettingActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断会话列表有没有此好友
                boolean contain=false;
                FriendInfo friendInfo;
                Connect.contantFriendInfoList.get(position).setChated(1); //设置为在聊天列表
                friendInfo=Connect.contantFriendInfoList.get(position);
                String userName=friendInfo.getUserName();
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
                    List<Message> messageList=new ArrayList<>();
                    ////messageList.add(message1);
                    Connect.messageMap.put(userName,messageList);
                    Log.d("ContantClick点击的好友条目名", "onChildClick: "+userName);
                }
                //String userName= Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).getUserName();
                Intent intent=new Intent(FriendInfoActivity.this, ChatActivity.class);
                intent.putExtra("userName",userName);
                startActivity(intent);

            }
        });
    }
    public void init(){
        send_btn=(Button)findViewById(R.id.friend_detail_send_btn);
        setting_btn=(TextView)findViewById(R.id.friend_detail_setting);
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        position=intent.getIntExtra("position",0);
    }
}
