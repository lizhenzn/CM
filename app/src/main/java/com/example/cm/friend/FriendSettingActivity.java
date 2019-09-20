package com.example.cm.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cm.R;
import com.example.cm.util.Connect;

public class FriendSettingActivity extends AppCompatActivity {
private Button delete_btn;
private Button save_nic_btn;
private String userName;
private int groupPosition,childPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_setting);
        init();
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect.deleteFriend(userName);        //TODO 删除好友
            }
        });
        save_nic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public  void init(){
        delete_btn=(Button)findViewById(R.id.friend_setting_delete_friend);
        save_nic_btn=(Button)findViewById(R.id.friend_setting_save);
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        groupPosition=intent.getIntExtra("groupPosition",0);
        childPosition=intent.getIntExtra("childPosition",0);
    }
}
