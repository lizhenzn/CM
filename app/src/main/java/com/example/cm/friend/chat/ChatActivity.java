package com.example.cm.friend.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.friend.FriendFragment;
import com.example.cm.util.Connect;
import com.example.cm.util.EmoticonsEditText;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView chatItemLV;
    private RecyclerView emoRecy;
    private EmoticonsEditText inputET;
    private TextView chatFriendTV;                 //聊天界面好友名
    private Button sendBtn;
    public static ChatAdapter chatAdapter;
    private EmoAdapter emoAdapter;
    private ImageButton emoIB,addIB;
    private String userName;
    public static List<Message> chatActivitymessageList;
    public  static boolean isSend;
    private int friendPosition;
    private boolean work;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        initData();
        work=true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("test", "run: ");
                    while(work) {
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatActivitymessageList=Connect.messageMap.get(userName);
                                chatAdapter.notifyDataSetChanged();
                                chatItemLV.smoothScrollToPosition(chatActivitymessageList.size()-1);   //聊天界面接收到信息直接自动滑动到末尾
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }


    //初始化控件
    public void init(){
        chatItemLV=(ListView)findViewById(R.id.chat_itemLV);
        emoRecy=(RecyclerView)findViewById(R.id.recycler_emo);
        inputET=(EmoticonsEditText)findViewById(R.id.inputET);
        sendBtn=(Button)findViewById(R.id.send);
        chatFriendTV=(TextView)findViewById(R.id.chat_friendNM);
        emoIB=(ImageButton)findViewById(R.id.emoIB);
        addIB=(ImageButton)findViewById(R.id.addIB);
        sendBtn.setOnClickListener(this);
        emoIB.setOnClickListener(this);
        addIB.setOnClickListener(this);
        chatFriendTV.setText(userName);
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        Log.d("聊天界面friendName", "init: "+userName);
       /* for(friendPosition=0;friendPosition<Connect.friendInfoList.size();friendPosition++){
            if(Connect.friendInfoList.get(friendPosition).getUserName().equals(userName)){
                break;
            }
        }*/
        //friendPosition=friendPosition-1;
        chatActivitymessageList=Connect.messageMap.get(userName);
        if(chatActivitymessageList.size()>=1)
            chatItemLV.smoothScrollToPosition(chatActivitymessageList.size()-1);
    }
    //设置数据
    public  void initData(){
        chatAdapter=new ChatAdapter(this,chatActivitymessageList);
        chatItemLV.setAdapter(chatAdapter);
        initEmo();

    }
    /**
     * 初始化表情
     */
    private void initEmo() {
        EmoAdapter adapter = new EmoAdapter(this);
        emoRecy.setLayoutManager(new GridLayoutManager(this, 7, LinearLayoutManager.VERTICAL, false));
        emoRecy.setAdapter(adapter);
        adapter.setListener(new EmoAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position, String emo) {
                try {
                    if (inputET != null && !TextUtils.isEmpty(emo)) {
                        int start = inputET.getSelectionStart();
                        CharSequence send_con = inputET.getText().insert(start, emo);
                        inputET.setText(send_con);
                        CharSequence info = inputET.getText();
                        if (info != null) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + emo.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emoIB:{
                //隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if(emoRecy.getVisibility()==View.GONE){
                emoRecy.setVisibility(View.VISIBLE);
                }
                else
                    emoRecy.setVisibility(View.GONE);
            }break;
            case R.id.send:{
                String mesBody= String.valueOf(inputET.getText());
                if(mesBody.length()>0){
                    ChatManager chatManager=ChatManager.getInstanceFor(Connect.xmpptcpConnection);
                    while(chatManager==null)
                        chatManager=ChatManager.getInstanceFor(Connect.xmpptcpConnection);
                    Chat chat=chatManager.createChat(userName);
                    if(chat==null)
                        Log.d("222333", "onClick: chat为空");
                    try {
                        chat.sendMessage(toJson(mesBody,"text"));
                        Message message=new Message();
                        message.setType(1);
                        message.setMessageType("text"); //文本消息
                        message.setBody(mesBody);
                        message.setFrom(Connect.xmpptcpConnection.getUser().split("/")[0]);
                        message.setTo(userName.split("/")[0]);
                        message.setDate(new Date());
                        Connect.messageMap.get(userName).add(message);     //添加信息
                        chatActivitymessageList.add(message);
                        inputET.setText("");              //设置输入框为空
                        isSend=true;
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                        isSend=false;
                    }


                    //chatAdapter.notifyDataSetChanged();
                    //chatItemLV.smoothScrollToPosition(chatActivitymessageList.size()-1);
                }

            }break;
        }


    }
    public String toJson(String string,String string0) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("data",string);
            jsonObject.put("type",string0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    protected void onDestroy() {
        Log.d("test", "onDestroy: ");
        super.onDestroy();
        work=false;
    }
}
