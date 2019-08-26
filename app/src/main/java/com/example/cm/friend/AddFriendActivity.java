package com.example.cm.friend;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.example.cm.util.Connect;

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
                while(work){
                    AddFriendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addFriendAdapter.notifyDataSetChanged();
                        }
                    });
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
                AddFriendItem addFriendItem=new AddFriendItem();
                FriendInfo friendInfo=new FriendInfo();
                friendInfo.setUserName(String.valueOf(userName_tv.getText()));
                friendInfo.setHeadBt(Connect.getUserImage(userName_tv.getText()+"@"+Connect.SERVERNAME));
                addFriendItem.setFriendInfo(friendInfo);
                addFriendItem.setReason("Hello,World!");
                Connect.addFriendItemList.add(addFriendItem);
            }break;
            case R.id.add_friend_search:{//查找此用户
                String userName= String.valueOf(userName_et.getText());
                Bitmap bitmap=Connect.getUserImage(userName+"@"+Connect.SERVERNAME);
                if(bitmap!=null){//有此用户
                    linearLayout.setVisibility(View.VISIBLE);
                    headImage.setImageBitmap(bitmap);
                    userName_tv.setText(userName);
                }else{//没有这个用户
                    Toast.makeText(AddFriendActivity.this,"抱歉，没有这个用户",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        work=false;
    }
}
