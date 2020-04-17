package com.example.cm.friend;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.AddFriend.AddFriendItem;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.theme.ThemeColor;
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
   private String userName,noteName;
   private int position;
   private ImageView friend_headIV;
   private TextView userTV,nicTV,sexTV,noteTV;
   private FriendInfo curFriendInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        init();
        Toolbar toolbar=(Toolbar)findViewById(R.id.friendInfo_toolabr);
        ThemeColor.setTheme(FriendInfoActivity.this,toolbar);
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
                }).addSheetItem("修改备注", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Log.e("", "onClick: 点击修改备注" );
                        Intent intent=new Intent(FriendInfoActivity.this,ChangeFriendNoteActivity.class);
                        intent.putExtra("userName",userName);
                        //intent.putExtra("position",position);
                        startActivity(intent);
                    }
                }).show();

            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send_btn.getText().equals("发消息")) {
                    //判断会话列表有没有此好友
                    boolean contain = false;
                    curFriendInfo.setChated(1);//设置为在聊天列表
                    String userName = curFriendInfo.getUserName();
                    for (int i = 0; i < MessageManager.getFriendInfoList().size(); i++) {
                        if (MessageManager.getFriendInfoList().get(i).getUserName().equals(userName)) {     //会话列表包含此好友
                            //list.get(i).get(message.getFrom()).add(message1);
                            contain = true;
                            Log.d("ContantClick点击的好友条目名", "onChildClick: " + userName);
                            break;
                        }
                    }
                    if (!contain) {  //会话列表不包含此好友
                        MessageManager.getDataBaseHelp().changeChatState(userName, 1);            //改变数据库中聊天状态
                        //friendInfo.setHeadBt(message.getBody());
                        MessageManager.getFriendInfoList().add(MessageManager.getFriendInfoList().size(), curFriendInfo);//
                        List<Message> messageList = new ArrayList<>();
                        ////messageList.add(message1);
                        MessageManager.getMessageMap().put(userName, messageList);
                        Log.d("ContantClick点击的好友条目名", "onChildClick: " + userName);
                    }
                    Intent intent = new Intent(FriendInfoActivity.this, ChatActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("noteName", noteName);
                    startActivity(intent);

                }else{  //此事时添加好友按钮
                    AddFriendItem addFriendItem = new AddFriendItem();
                    addFriendItem.setFriendInfo(curFriendInfo);
                    addFriendItem.setReason("Hello,World!");
                    addFriendItem.setResult("已发送验证");
                    MessageManager.setAddFriendItemListChanged(true);

                    try {
                        while(!Connect.getRoster().isLoaded()){
                            Connect.getRoster().reload();
                        }
                        Connect.getRoster().createEntry(userName+"@"+Connect.SERVERNAME,userName,new String[]{"Friends"});
                        Log.e("ADD", "onClick: 申请发送成功");
                    } catch (Exception e) {//SmackException.NotConnectedException
                        e.printStackTrace();
                        addFriendItem.setResult("申请发送异常，请重试");
                        Toast.makeText(FriendInfoActivity.this, "申请发送异常", Toast.LENGTH_SHORT).show();
                    }
                    MessageManager.getAddFriendItemList().add(addFriendItem);
                }
            }
        });
    }
    public void init(){
        send_btn=(Button)findViewById(R.id.friend_detail_send_btn);
        setting_btn=(TextView)findViewById(R.id.friend_detail_setting);
        noteTV=findViewById(R.id.friend_detail_note_tv);
        userTV=(TextView)findViewById(R.id.friend_detail_user_tv);
        nicTV=(TextView)findViewById(R.id.friend_detail_nic_tv);
        sexTV=(TextView)findViewById(R.id.friend_detail_sex_tv);
        friend_headIV=(ImageView)findViewById(R.id.friend_detail_iv);
        setting_btn.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
        send_btn.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        curFriendInfo=MessageManager.getFriendInfoFromContantList(userName);
        position=intent.getIntExtra("position",-1);
        if(curFriendInfo==null) {  //联系人界面调用，有此好友
            curFriendInfo=VCardManager.getFriendInfo(userName);
            send_btn.setText("添加好友");
        }
        userName=curFriendInfo.getUserName();
        noteName=curFriendInfo.getNoteName();
        friend_headIV.setImageBitmap(curFriendInfo.getHeadBt());
        noteTV.setText("备注:"+noteName);
        userTV.setText("帐号:"+userName);
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

    @Override
    protected void onResume() {
        super.onResume();
        noteTV.setText("备注："+curFriendInfo.getNoteName());
    }
}
