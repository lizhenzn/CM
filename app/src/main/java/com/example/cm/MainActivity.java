package com.example.cm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;
import com.example.cm.myInfo.SmackUserInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.service.PacketListenerService;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.DataBase;
import com.example.cm.util.ServerFunction;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import java.io.IOException;

import q.rorbin.badgeview.QBadgeView;

//import main.CallBackMethods;
//import main.ImgUploader;
//import main.TransferManager;
public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView nichengTV,zhangHaoTV;
    private static TextView titleTV;
    private ImageView navi_head,head_home;
    private static String path="/sdcard/Clothes/MyInfo/head";
    private final int LOGIN=1;
    private QBadgeView naviQBadgeView;
    private static ServiceConnection serviceConnection;   //用于连接服务通信
    private static PacketListenerService.MyBinder binder;   //服务中Binder
    private static Context mainActivityContext;   //主活动的Context
    public static Boolean isBinded;
    private static final String TAG = "MainActivity";

    private static int clothes_up=-1;
    private static int clothes_down=-1;
    private static boolean choose_flag=false;

    private static FragmentTabHost fragmentTabHost;
    public static int getClothes_up(){
        return clothes_up;
    }
    public static void setClothes_up(int up){clothes_up=up;}
    public static int getClothes_down(){ return clothes_down;}
    public static void setClothes_down(int down){clothes_down=down;}
    public static boolean isChoose_flag(){
        return choose_flag;
    }
    public static void setChoose_flag(boolean choose_flag){MainActivity.choose_flag=choose_flag;}
//每一个页面写一次setToolbarText()函数   ******************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
        initTabhost();
        setToolbarText("搭配");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        navigationView.setCheckedItem(R.id.setting);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.settin:{
                        navi_head.setImageResource(R.drawable.unlogin);
                    }break;
                    case R.id.navi_sign_out:{
                        Connect.signOut();   //退出登录
                    }break;
                    case R.id.navi_set_head:{   //设置头像
                        if(AlbumUtil.checkStorage(MainActivity.this)){
                            Intent intent=new Intent("android.intent.action.GET_CONTENT");
                            intent.setType("image/*");
                            startActivityForResult(intent,AlbumUtil.OPEN_ALBUM);
                        }else{
                            Toast.makeText(MainActivity.this,"You denied the permission",Toast.LENGTH_SHORT).show();
                        }
                    }break;
                    default:
                        break;

                }
                return true;
            }
        });
        //点击侧滑栏头像，进入信息编辑界面
        navi_head.setOnClickListener(new View.OnClickListener() {
            Intent intent=null;
            @Override
            public void onClick(View v) {
                //如果图片是默认的未登录图片，说明还没有登陆,点击进入登陆活动
                if(navi_head.getDrawable().getCurrent().getConstantState().equals(getResources().getDrawable(R.drawable.unlogin).getConstantState())){
                    intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent,LOGIN);
                }else{
                intent=new Intent(MainActivity.this,MyInfoActivity.class);
                startActivity(intent);
                }

            }
        });
        //点击左上角头像，打开侧滑栏
        head_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
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
    public static void setToolbarText(CharSequence title) {
        titleTV.setText(title);
    }
    public static Context getInstance(){  //返回上下文环境
        return mainActivityContext;
    }

    //初始化各控件
    public void init(){
        isBinded=false;
        mainActivityContext=MainActivity.this;
        MessageManager.initAllList();
        Connect.init();             //初始化适配器列表
        if(!AlbumUtil.checkStorage(this)){
            AlbumUtil.requestStorage(this);        //申请存储读取权限
        }
        naviQBadgeView=new QBadgeView(this);
        //sharedPreferences=getSharedPreferences("myInfo",MODE_PRIVATE);
        //editor=sharedPreferences.edit();
        toolbar=(Toolbar)findViewById(R.id.toolabr);
        setSupportActionBar(toolbar);
        navigationView=(NavigationView)findViewById(R.id.navi);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        View headerView= navigationView.inflateHeaderView(R.layout.navi_header);             //动态加载headerview，为了其中头像点击事件
        navi_head=(ImageView)headerView.findViewById(R.id.navi_head);
        head_home=(ImageView)findViewById(R.id.head_home);
        nichengTV=(TextView)headerView.findViewById(R.id.nicheng);
        zhangHaoTV=(TextView)headerView.findViewById(R.id.zhanghao);
        titleTV=(TextView)findViewById(R.id.title_tv);
        toolbar.setTitle("");
        //初始化登陆人信息，若此前登陆过，初始化聊天信息和好友信息
        if(MessageManager.getSharedPreferences().contains("userName")){  //有此userName键值，说明登陆过
            MessageManager.getSmackUserInfo().setUserName(MessageManager.getSharedPreferences().getString("userName",""));
            String cachePasswd=MessageManager.getSharedPreferences().getString("passward","");//缓存的密码
            String headBtRoad=MessageManager.getSharedPreferences().getString("userHeadBtRoad","");
            MessageManager.getSmackUserInfo().setHeadBt(BitmapFactory.decodeFile(headBtRoad));
            Log.d(TAG, "init: 设置自己头像之后");
            MessageManager.setDataBaseHelp(MessageManager.getSmackUserInfo().getUserName());
            //Connect.smackUserInfo.setSex(Connect.sharedPreferences.getString("userSex",""));
            //Connect.smackUserInfo.setHeight(Connect.sharedPreferences.getString("userHeight","180"));
            //Connect.smackUserInfo.setEmail(Connect.sharedPreferences.getString("userEmail",""));

            //TODO        初始化聊天信息和好友信息
            MessageManager.setContantFriendInfoList(MessageManager.getDataBaseHelp().getContantFriendInfoList());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connect.login(MessageManager.getSmackUserInfo().getUserName(), cachePasswd);
                    Log.e(TAG, "run: 上次登录启动自动登录："+MessageManager.getSmackUserInfo().getUserName()+cachePasswd );
                }
            }).start();




        }else{//此前没有登陆过
            MessageManager.getSmackUserInfo().setUserName("点击登录");
            Bitmap bitmap=BitmapFactory.decodeResource(this.getResources(),R.drawable.unlogin);
            MessageManager.getSmackUserInfo().setHeadBt(bitmap);           //设置为未登陆的照片
        }
        setInfo();        //调用设置头像、昵称函数

    }
    //设置昵称、头像...
    public void setInfo(){
        navi_head.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());
        head_home.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());
        nichengTV.setText(MessageManager.getSmackUserInfo().getUserName());
    }
    //初始化FragmentTabHost
    public void initTabhost(){
        fragmentTabHost=(FragmentTabHost)findViewById(R.id.tabhost);
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
            if(tabs1.getName().equals("会话")) {
                naviQBadgeView.bindTarget(tab_tv);
            }
            naviQBadgeView.setBadgeText("");
            tabSpec.setIndicator(indicator);
            fragmentTabHost.addTab(tabSpec,tabs1.getaClass(),null);
        }

    }
    //绑定监听
    public static void bindPacket(){
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder=(PacketListenerService.MyBinder)service;
                binder.startListerer();//调用包监听中Binder的方法
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBinded=false;
            }
        };
        Intent intent=new Intent(mainActivityContext, PacketListenerService.class);
        mainActivityContext.bindService(intent,serviceConnection,BIND_AUTO_CREATE);    //绑定服务
        isBinded=true;

    }
    //取绑监听
    public static void unBindPacket(){
        if(serviceConnection!=null) {
            mainActivityContext.unbindService(serviceConnection);
            isBinded=false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case LOGIN:{
                if(resultCode==RESULT_OK){
                    zhangHaoTV.setText("账号： "+data.getStringExtra("zhanghao"));
                }

            }break;
            case AlbumUtil.OPEN_ALBUM:{
                String photoRoad=AlbumUtil.getImageAbsolutePath(data,MainActivity.this);
                try {
                    VCardManager.changeImage(Connect.getXMPPTCPConnection(),photoRoad);
                    Toast.makeText(MainActivity.this,"更改头像成功",Toast.LENGTH_SHORT).show();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"更改头像异常",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"更改头像异常",Toast.LENGTH_SHORT).show();
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
                    //Intent intent=new Intent("android.intent.action.GET_CONTENT");
                    //intent.setType("image/*");
                    //startActivityForResult(intent,AlbumUtil.OPEN_ALBUM);
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
            }break;
            case AlbumUtil.REQUEST_CAMERA:{
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        setInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出登录
        if(Connect.getXMPPTCPConnection()!=null) {
            Connect.signOut();
            Connect.isLogined = false;
            Connect.setRoster(null);
            Connect.setXmpptcpConnection(null);



        }}
    public static void setFragmentTabHostVisibility( boolean visibility){
        if (visibility) {
            fragmentTabHost.setVisibility(View.VISIBLE);
        } else {
            fragmentTabHost.setVisibility(View.GONE);
        }
    }
}