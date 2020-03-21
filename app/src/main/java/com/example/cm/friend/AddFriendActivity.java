package com.example.cm.friend;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{
private Button add_btn,search_btn;
private EditText userName_et;
private ImageView headImage;
private TextView userName_tv;
private LinearLayout linearLayout;
private AddFriendAdapter addFriendAdapter;
private ListView addFriendLV;
private boolean work;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar=(Toolbar)findViewById(R.id.add_friend_toolabr);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //初始化控件
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(work) {
                    if (MessageManager.isAddFriendItemListChanged()) {
                        AddFriendActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addFriendAdapter.notifyDataSetChanged();
                                MessageManager.setContantListChanged(false);
                            }
                        });
                    }
                }

            }
        }).start();
    }
    //初始化控件
    public void init(){
        add_btn=(Button)findViewById(R.id.add_friend_btn);
        add_btn.setOnClickListener(this);
        search_btn=(Button)findViewById(R.id.add_friend_search);
        search_btn.setOnClickListener(this);
        headImage=(ImageView)findViewById(R.id.add_friend_headImage);
        headImage.setOnClickListener(this);
        userName_et=(EditText)findViewById(R.id.add_friend_et);
        userName_tv=(TextView)findViewById(R.id.add_friend_userTV);
        userName_tv.setOnClickListener(this);
        linearLayout=(LinearLayout)findViewById(R.id.add_friend_detail_LL);
        addFriendAdapter=new AddFriendAdapter(this);
        addFriendLV=(ListView)findViewById(R.id.add_friend_lv);
        addFriendLV.setAdapter(addFriendAdapter);
        work=true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_friend_btn:{//发送添加请求
                boolean contain=false;
                    for(int j=0;j<MessageManager.getContantFriendInfoList().size();j++){
                        if(MessageManager.getContantFriendInfoList().get(j).getUserName().equals(userName_tv.getText())){
                            contain=true;
                            break;
                        }
                    }

                if(!contain) {
                    String user=String.valueOf(userName_tv.getText());
                    AddFriendItem addFriendItem = new AddFriendItem();
                    FriendInfo friendInfo = new FriendInfo();
                    friendInfo.setUserName(user);
                    friendInfo.setHeadBt(VCardManager.getUserImage(user));
                    addFriendItem.setFriendInfo(friendInfo);
                    addFriendItem.setReason("Hello,World!");
                    addFriendItem.setResult("已发送验证");

                    MessageManager.setContantListChanged(true);

                    try {
                        while(!Connect.getRoster().isLoaded()){
                            Connect.getRoster().reload();
                        }
                        Connect.getRoster().createEntry(user+"@"+Connect.SERVERNAME,user,new String[]{"Friends"});
                        Log.e("ADD", "onClick: 申请发送成功");
                    } catch (Exception e) {//SmackException.NotConnectedException
                        e.printStackTrace();
                        addFriendItem.setResult("申请发送异常，请重试");
                        Toast.makeText(AddFriendActivity.this, "申请发送异常", Toast.LENGTH_SHORT).show();
                    }
                    MessageManager.getAddFriendItemList().add(addFriendItem);
                }else{
                    Toast.makeText(AddFriendActivity.this,"已有此好友",Toast.LENGTH_SHORT).show();
                }
            }break;
            case R.id.add_friend_search:{//查找此用户
                if(Connect.isLogined) {
                    String userName = String.valueOf(userName_et.getText());
                    Bitmap bitmap = VCardManager.getUserImage(userName);
                    if (bitmap != null) {//有此用户
                        linearLayout.setVisibility(View.VISIBLE);
                        headImage.setImageBitmap(bitmap);
                        userName_tv.setText(userName);
                    } else {//没有这个用户
                        Toast.makeText(AddFriendActivity.this, "抱歉，没有这个用户", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddFriendActivity.this,"未登录...",Toast.LENGTH_SHORT).show();
                }
            }break;
            case R.id.add_friend_headImage:{//查看详情

            }break;
            case R.id.add_friend_userTV:{//查看详情

            }break;
            default:break;
        }
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: AddFriendActivity.this.finish();break;
            default:break;
        }
            return true;

    }
    //同意添加好友
    public static boolean agreeAddFriend(String userName)  {

        Presence presence=new Presence(Presence.Type.subscribed);
        presence.setTo(userName+"@"+Connect.SERVERNAME);
        try {
            Connect.getXMPPTCPConnection().sendStanza(presence);
            MessageManager.addFriend(userName);//刷新好友列表
            while(!Connect.getRoster().isLoaded()){
                Connect.getRoster().reload();
            }
            Connect.getRoster().createEntry(userName+"@"+Connect.SERVERNAME,userName,new String[]{"Friends"});
            Log.e("ADD", "onClick: 申请发送成功");
            Log.e("", "agreeAddFriend: 同意添加好友" );
        }catch(SmackException.NotConnectedException e){
            e.printStackTrace();
            return false;
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return true;
    }
    //拒绝添加好友
    public static boolean rejectAddFriend(String userName)  {
        Presence presence=new Presence(Presence.Type.unsubscribed);
        presence.setTo(userName+"@"+Connect.SERVERNAME);
        try {
            Connect.getXMPPTCPConnection().sendStanza(presence);
            Log.e("", "rejectAddFriend: 拒绝添加");
        }catch(SmackException.NotConnectedException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        work=false;
    }
}
