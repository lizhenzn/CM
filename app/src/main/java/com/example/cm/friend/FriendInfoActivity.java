package com.example.cm.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.ActionSheetDialog;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.List;

public class FriendInfoActivity extends AppCompatActivity {
   private Button send_btn;
   private TextView setting_btn;
   private String userName;
   private int position;
   private ImageView friend_headIV;
   private TextView userTV,nicTV,sexTV;
   private FriendInfo curFriendInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        init();
        Toolbar toolbar=(Toolbar)findViewById(R.id.friendInfo_toolabr);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        }
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(FriendInfoActivity.this)
                        .builder()
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Log.e("", "onClick: 点击删除" );
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
                        }).addSheetItem("举报", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Log.e("", "onClick: 点击举报" );
                        Intent intent=new Intent(FriendInfoActivity.this,AccuseFriendActivity.class);
                        intent.putExtra("userName",userName);
                        startActivity(intent);
                    }
                }).show();

            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断会话列表有没有此好友
                boolean contain=false;
                FriendInfo friendInfo;
                MessageManager.getContantFriendInfoList().get(position).setChated(1); //设置为在聊天列表
                friendInfo=MessageManager.getContantFriendInfoList().get(position);
                String userName=friendInfo.getUserName();
                for(int i=0;i<MessageManager.getFriendInfoList().size();i++){
                    if(MessageManager.getFriendInfoList().get(i).getUserName().equals(userName)){     //会话列表包含此好友
                        //list.get(i).get(message.getFrom()).add(message1);
                        contain=true;
                        Log.d("ContantClick点击的好友条目名", "onChildClick: "+userName);
                        break;
                    }
                }
                if(!contain){  //会话列表不包含此好友
                    MessageManager.getDataBaseHelp().changeChatState(userName,1);            //改变数据库中聊天状态
                    //friendInfo.setHeadBt(message.getBody());
                    MessageManager.getFriendInfoList().add(MessageManager.getFriendInfoList().size(),friendInfo);//
                    List<Message> messageList=new ArrayList<>();
                    ////messageList.add(message1);
                    MessageManager.getMessageMap().put(userName,messageList);
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
        userTV=(TextView)findViewById(R.id.friend_detail_user_tv);
        nicTV=(TextView)findViewById(R.id.friend_detail_nic_tv);
        sexTV=(TextView)findViewById(R.id.friend_detail_sex_tv);
        friend_headIV=(ImageView)findViewById(R.id.friend_detail_iv);
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        position=intent.getIntExtra("position",0);
        curFriendInfo=MessageManager.getContantFriendInfoList().get(position);
        friend_headIV.setImageBitmap(curFriendInfo.getHeadBt());
        userTV.setText("帐号:"+curFriendInfo.getUserName());
        nicTV.setText("用户名："+curFriendInfo.getNicName());
        String sex="保密";
        if(curFriendInfo.getSex().equalsIgnoreCase("male")){
            sex="男";
        }else if(curFriendInfo.getSex().equalsIgnoreCase("female")){
            sex="女";
        }
        sexTV.setText("性别:"+sex);
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
