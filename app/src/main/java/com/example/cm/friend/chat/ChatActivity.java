package com.example.cm.friend.chat;

import android.content.Context;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.util.EmoticonsEditText;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView chatItemLV;
    private RecyclerView emoRecy;
    private EmoticonsEditText inputET;
    private TextView chatFriendTV;                 //聊天界面好友名
    private Button sendBtn;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EmoAdapter emoAdapter;
    private ImageButton emoIB,addIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        initData();
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
    }
    //设置数据
    public  void initData(){

        messageList=new ArrayList<>();
        for(int i=0;i<66;i++){
            Message message=new Message();
            if(i%2==0){
                message.setType(2);
                message.setFrom("friend");
            }else{
                message.setType(1);
            }
            message.setBody("Hello World!"+i);
            messageList.add(message);
        }
        chatAdapter=new ChatAdapter(this,messageList);

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
                    Message message=new Message();
                    message.setType(1);
                    message.setBody(mesBody);
                    message.setFrom("self");
                    message.setTo("friend");
                    messageList.add(message);
                    chatAdapter.notifyDataSetChanged();
                    chatItemLV.smoothScrollToPosition(messageList.size()-1);
                }

            }break;
        }

    }
}
