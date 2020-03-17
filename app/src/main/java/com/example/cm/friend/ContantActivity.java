package com.example.cm.friend;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.SideBar.ISideBarSelectCallBack;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.util.Connect;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import main.UserInfo;
import q.rorbin.badgeview.QBadgeView;

public class ContantActivity extends AppCompatActivity {

    private ListView listView;
    private SideBar sideBar;
    //private ArrayList<FriendInfo> list;
    private boolean work;
    private SortAdapter sortAdapter;
    private ImageButton addIB;
    private QBadgeView addFriendQBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant);
        Toolbar toolbar=(Toolbar)findViewById(R.id.contant_toolabr);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.contant_lv);
        sideBar = (SideBar) findViewById(R.id.contant_sidebar);
        addIB=(ImageButton)findViewById(R.id.contant_addIB);
        addIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendQBadgeView.hide(true);//隐藏红点
                Intent intent=new Intent(ContantActivity.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });
        addFriendQBadgeView=new QBadgeView(this);
        addFriendQBadgeView.bindTarget(addIB).setBadgeText(" ");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ContantActivity.this,FriendInfoActivity.class);
                String userName=Connect.contantFriendInfoList.get(position).getUserName();
                intent.putExtra("userName",userName);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        if(sideBar==null){
            Log.d("", "initView:sideBar为空222233333 ");
        }else {
            Log.d("", "initView:contantFriendlist长度为： "+Connect.contantFriendInfoList.size());

            sideBar.setOnStrSelectCallBack((index, selectStr) -> {
                for (int i = 0; i < Connect.contantFriendInfoList.size(); i++) {
                    if (selectStr.equalsIgnoreCase(Connect.contantFriendInfoList.get(i).getFirstLetter())) {
                        listView.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            });
        }

    }

    private void initData() {
        //list = new ArrayList<>();
       //list= (ArrayList<FriendInfo>) Connect.friendInfoList;
        Collections.sort(Connect.contantFriendInfoList); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        sortAdapter = new SortAdapter(this, Connect.contantFriendInfoList);
        listView.setAdapter(sortAdapter);
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:this.finish();break;


            default:break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (work) {
                    if(Connect.groupInfoListChanged) {
                        ContantActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sortAdapter.notifyDataSetChanged();
                                Connect.groupInfoListChanged = false;
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        work=false;
    }
}
