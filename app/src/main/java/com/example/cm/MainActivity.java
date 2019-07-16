package com.example.cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.cm.myInfo.MyInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
private DrawerLayout drawerLayout;
private NavigationView navigationView;
private Toolbar toolbar;
private TextView titleTV,nichengTV;
private ImageView navi_head;
private static String path="/sdcard/Clothes/MyInfo/head";
private SharedPreferences sharedPreferences;
private SharedPreferences.Editor editor;

//每一个页面写一次setToolbarText()函数   ******************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().hide();
        init();
        initTabhost();
        setToolbarText("搭配");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }
        navigationView.setCheckedItem(R.id.setting);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    default:
                        break;

                }
                return true;
            }
        });
        navi_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyInfoActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfo();
    }

    @Override   //标题栏按钮按键
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                break;
        }
        return true;
    }


    //设置页面标题
    public void setToolbarText(CharSequence title) {
        titleTV.setText(title);
    }
    //初始化各控件
    public void init(){
        sharedPreferences=getSharedPreferences("myInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        toolbar=(Toolbar)findViewById(R.id.toolabr);
        setSupportActionBar(toolbar);
        navigationView=(NavigationView)findViewById(R.id.navi);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        View headerView= navigationView.inflateHeaderView(R.layout.navi_header);             //动态加载headerview，为了其中头像点击事件
        navi_head=(ImageView)headerView.findViewById(R.id.navi_head);
        nichengTV=(TextView)headerView.findViewById(R.id.nicheng);
        titleTV=(TextView)findViewById(R.id.title_tv);
        toolbar.setTitle("");
        setInfo();        //调用设置头像、昵称函数
    }
    //设置昵称、头像...
    public void setInfo(){
        Bitmap bitmap= BitmapFactory.decodeFile(path+"head.jpg");
        if(bitmap!=null){
            @SuppressWarnings("deprecation")    
            Drawable drawable=new BitmapDrawable(bitmap);
            navi_head.setImageDrawable(drawable);
        }
        else{
            //如果SD卡没有，从服务器获取，然后保存到SD
            Log.d("MyInfo","No head image");

        }
        //从文件读昵称
        String nc=sharedPreferences.getString("user","");
        nichengTV.setHint("昵称："+nc);
    }
    //初始化FragmentTabHost
    public void initTabhost(){
        FragmentTabHost fragmentTabHost=(FragmentTabHost)findViewById(R.id.tabhost);
        //将FragmentTabhost与FrameLayout关联
        fragmentTabHost.setup(getApplicationContext(),getSupportFragmentManager(),R.id.tab_main_content);
        //添加tab和其对应的fragment
        Tabs []tabs=Tabs.values();
        for(int i=0;i<tabs.length;i++){
            Tabs tabs1=tabs[i];
            TabHost.TabSpec tabSpec=fragmentTabHost.newTabSpec(tabs1.getName());

            View indicator=View.inflate(getApplicationContext(),R.layout.tab_view,null);
            ImageView tab_iv=(ImageView)indicator.findViewById(R.id.tab_iv);
            TextView tab_tv=(TextView)indicator.findViewById(R.id.tab_tv);
            Drawable drawable=getApplication().getResources().getDrawable(tabs1.getIcon());
            tab_iv.setImageDrawable(drawable);
            tab_tv.setText(tabs1.getName());
            //setToolbarText(tabs1.getName());         //设置标题
            tabSpec.setIndicator(indicator);
            fragmentTabHost.addTab(tabSpec,tabs1.getaClass(),null);

        }


    }



}
