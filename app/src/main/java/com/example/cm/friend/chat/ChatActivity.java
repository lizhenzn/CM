package com.example.cm.friend.chat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.FriendFragment;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.util.AlbumUtil;
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
    private FriendInfo friendInfo;
    public  static boolean isSend;
    private int friendPosition;
    private boolean work;
    private Chat chat;
    private TextView user_tv;  //正在聊天的人 顶部显示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar=(Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        user_tv=(TextView)toolbar.findViewById(R.id.chat_friendNM);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
        initData();
        work=true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("test", "run: ");
                    while(work) {
                        //isSend=false;
                        //Connect.haveNewMessage=false;
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //chatActivitymessageList=Connect.messageMap.get(userName);
                                chatAdapter.notifyDataSetChanged();
                                chatItemLV.smoothScrollToPosition(Connect.messageMap.get(userName).size()-1);   //聊天界面接收到信息直接自动滑动到末尾
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
        for(int i=0;i<Connect.contantFriendInfoList.size();i++){//赋值给正在聊天的人
            if(Connect.contantFriendInfoList.get(i).getUserName().equals(userName)){
                friendInfo=Connect.contantFriendInfoList.get(i);
                break;
            }

        }
        user_tv.setText(userName);
        //chatActivitymessageList=Connect.messageMap.get(userName);
        if(Connect.messageMap.get(userName).size()>=1)
            chatItemLV.smoothScrollToPosition(Connect.messageMap.get(userName).size()-1);

        if(Connect.isLogined) {
            ChatManager chatManager=ChatManager.getInstanceFor(Connect.xmpptcpConnection);
            while (chatManager == null)
                chatManager = ChatManager.getInstanceFor(Connect.xmpptcpConnection);
            chat = chatManager.createChat(userName + "@" + Connect.SERVERNAME);
            if (chat == null)
                Log.d("222333", "onClick: chat为空");
        }else{
            Toast.makeText(this,"未登录，无法发送消息",Toast.LENGTH_SHORT).show();
        }
    }
    //设置数据
    public  void initData(){
        chatAdapter=new ChatAdapter(this,Connect.messageMap.get(userName),friendInfo);
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
                    if(Connect.isLogined) {
                        try {
                            Date date = new Date();
                            chat.sendMessage(toJson(mesBody, "text", date.getTime()));
                            Message message = new Message();
                            message.setType(1);
                            message.setMessageType("text"); //文本消息
                            message.setBody(mesBody);
                            message.setPhotoRoad("");
                            message.setPhoto(null);
                            message.setFrom(Connect.xmpptcpConnection.getUser().split("@")[0]);
                            message.setTo(userName.split("@")[0]);
                            message.setDate(date.getTime());
                            Connect.dataBaseHelp.addMessage(message);        //数据库添加聊天信息
                            Connect.messageMap.get(userName).add(message);     //添加信息
                            //chatActivitymessageList.add(message);
                            inputET.setText("");              //设置输入框为空
                            isSend = true;
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                            isSend = false;
                        }
                    }else{
                        Toast.makeText(this,"未登录...",Toast.LENGTH_SHORT).show();
                    }


                    //chatAdapter.notifyDataSetChanged();
                    //chatItemLV.smoothScrollToPosition(chatActivitymessageList.size()-1);
                }

            }break;
            case R.id.addIB:{
                if(Connect.isLogined) {         //未登录
                    if (AlbumUtil.checkStorage(ChatActivity.this)) {
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, AlbumUtil.OPEN_ALBUM);
                    } else {
                        AlbumUtil.requestStorage(ChatActivity.this);
                        //Toast.makeText(ChatActivity.this,"You denied the permission",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"未登录...",Toast.LENGTH_SHORT).show();
                }


            }break;
            default:break;
        }


    }
    public String toJson(String string,String string0,Long dateTime) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("data",string);
            jsonObject.put("type",string0);
            jsonObject.put("date",dateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AlbumUtil.OPEN_ALBUM:{
                String absoluteRoad=AlbumUtil.getImageAbsolutePath(data,ChatActivity.this);
                if(absoluteRoad!=null) {            //选择图片
                    Log.d("选择的图片路径", "onActivityResult: " + absoluteRoad);
                    String imageStr = AlbumUtil.getImageStr(absoluteRoad);
                    Log.d("图片字符串", "onActivityResult: " + imageStr);
                    if (imageStr == null)
                        imageStr = "";
                    try {
                        Date date = new Date();
                        chat.sendMessage(toJson(imageStr, "photo", date.getTime()));
                        Message message = new Message();
                        message.setType(1);
                        message.setMessageType("photo"); //图片信息
                        message.setPhotoRoad(absoluteRoad);
                        Bitmap bitmap = BitmapFactory.decodeFile(absoluteRoad);
                        message.setPhoto(bitmap);
                        message.setBody("[图片]");
                        message.setFrom(Connect.xmpptcpConnection.getUser().split("@")[0]);
                        message.setTo(userName.split("@")[0]);
                        message.setDate(date.getTime());
                        Connect.dataBaseHelp.addMessage(message);  //数据库添加聊天信息
                        Connect.messageMap.get(userName).add(message);     //添加信息
                        //chatActivitymessageList.add(message);
                        inputET.setText("");              //设置输入框为空
                        isSend = true;
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                        isSend = false;
                    }
                }

            }break;
            default:break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case AlbumUtil.REQUEST_STORAGE:{
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Intent intent=new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,AlbumUtil.OPEN_ALBUM);
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }

            }break;
            default:break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:this.finish();break;

            default:break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.d("test", "onDestroy: ");
        super.onDestroy();
        work=false;
    }
}
