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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.cm.R;
import com.example.cm.friend.AddFriend.AddFriendActivity;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.MessageManager;

import java.util.Collections;

import q.rorbin.badgeview.QBadgeView;

public class ContantActivity extends AppCompatActivity {

    private ListView listView;
    private SideBar sideBar;
    private boolean work;
    private SortAdapter sortAdapter;
    private ImageButton addIB;
    private QBadgeView addFriendQBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant);
        Toolbar toolbar=(Toolbar)findViewById(R.id.contant_toolabr);
        ThemeColor.setTheme(ContantActivity.this,toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
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
                Intent intent=new Intent(ContantActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });
        addFriendQBadgeView=new QBadgeView(this);
        addFriendQBadgeView.bindTarget(addIB).setBadgeText(" ");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ContantActivity.this,FriendInfoActivity.class);
                String userName= MessageManager.getContantFriendInfoList().get(position).getUserName();
                intent.putExtra("userName",userName);
                intent.putExtra("position",position);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);
            }
        });
        if(sideBar==null){
            Log.d("", "initView:sideBar为空222233333 ");
        }else {
            Log.d("", "initView:contantFriendlist长度为： "+MessageManager.getContantFriendInfoList().size());

            sideBar.setOnStrSelectCallBack((index, selectStr) -> {
                for (int i = 0; i < MessageManager.getContantFriendInfoList().size(); i++) {
                    if (selectStr.equalsIgnoreCase(MessageManager.getContantFriendInfoList().get(i).getFirstLetter())) {
                        listView.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            });
        }

    }

    private void initData() {
        work=true;
        Collections.sort(MessageManager.getContantFriendInfoList()); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        sortAdapter = new SortAdapter(this, MessageManager.getContantFriendInfoList());
        listView.setAdapter(sortAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
                break;
            default:break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        work=true;
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (work) {
                    if(MessageManager.isContantListChanged()) {
                        ContantActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sortAdapter.notifyDataSetChanged();
                                MessageManager.setContantListChanged(false);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
        return;
    }
}
