package com.example.cm.friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.cm.R;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

public class FriendSettingActivity extends AppCompatActivity {
private Button delete_btn;
private Button save_nic_btn;
private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_setting);
        Toolbar toolbar=(Toolbar)findViewById(R.id.friendSetting_toolabr);
        ThemeColor.setTheme(FriendSettingActivity.this,toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        init();
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (!Connect.getRoster().isLoaded()){
                    try {
                        Connect.getRoster().reload();
                    } catch (SmackException.NotLoggedInException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
                userName=userName+"@"+Connect.SERVERNAME;
                try {
                    Connect.getRoster().removeEntry(Connect.getRoster().getEntry(userName));
                    MessageManager.deleteFriend(userName);        //TODO 删除好友
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{this.finish();}break;
            default:break;
        }
        return true;
    }
}
