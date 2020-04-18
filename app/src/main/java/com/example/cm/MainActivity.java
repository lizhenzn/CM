package com.example.cm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.myInfo.ChangePasswordActivity;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.service.PacketListenerService;
import com.example.cm.theme.ThemeActivity;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;
import com.example.cm.wardrobe.WardrobeFragment;

import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import q.rorbin.badgeview.QBadgeView;

//import main.CallBackMethods;
//import main.ImgUploader;
//import main.TransferManager;
public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private static TextView nichengTV;
    private static TextView titleTV;
    private static ImageView navi_head,head_home;
    private RelativeLayout cmImageLayout;
    private final int LOGIN=1;
    private long backPressTime;

    private static ServiceConnection serviceConnection;   //用于连接服务通信
    private static PacketListenerService.MyBinder binder;   //服务中Binder
    private static Context mainActivityContext;   //主活动的Context
    private static Activity activity;
    public static Boolean isBinded;
    private static final String TAG = "MainActivity";
    private View headerView;
    private boolean work;

    private static int clothes_up=-1;
    private static int clothes_down=-1;
    private static boolean choose_flag=false;
    private static FragmentTabHost fragmentTabHost;
    public static QBadgeView naviQBadgeView;
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
        toolbar=(Toolbar)findViewById(R.id.toolabr);
        setSupportActionBar(toolbar);
        init();
        initTabhost();
        setToolbarText("搭配");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }
        //测试代码片，可能存在严重不稳定情况，方法的静态化可能导致一系列问题
        WardrobeFragment.initData();
        //
        //navigationView.setCheckedItem(R.id.setting);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navi_sign_out:{
                        Connect.signOut();   //注销登录
                        setInfo();
                    }break;
                    case R.id.changePassword:{//修改密码
                        Intent intent=new Intent(MainActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                    }break;//修改密码
                    case R.id.cleanCache:{//清除缓存
                        long size=0;
                        File f=getCacheDir();
                        for(File temp:f.listFiles()){
                            if(temp.isFile()){
                                size+=temp.length();
                                temp.delete();
                            }
                        }
                        Toast.makeText(mainActivityContext,
                                "已清除图片缓存"+formetFileSize(size),
                                Toast.LENGTH_SHORT).show();
                    }break;
                    case R.id.navi_edit_selfInfo:{
                        Intent intent=new Intent(MainActivity.this,MyInfoActivity.class);
                        startActivity(intent);
                    }break;
                    case R.id.navi_changeTheme:{
                        Intent intent=new Intent(MainActivity.this, ThemeActivity.class);
                        startActivity(intent);

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
                if(!Connect.isLogined){
                    intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent,LOGIN);
                    overridePendingTransition(R.anim.out_from_left,R.anim.in_from_right);
                }else{
                intent=new Intent(MainActivity.this,MyInfoActivity.class);
                startActivity(intent);
                }


            }
        });
        nichengTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nichengTV.getText().equals("点击登录")){
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(intent,LOGIN);
                    overridePendingTransition(R.anim.out_from_left,R.anim.in_from_right);
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
        if((ThemeColor.changed)&&(ThemeColor.backColorStr!=null)) {
            ThemeColor.setTheme(MainActivity.this,toolbar);
            headerView.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
            ThemeColor.changed=false;
        }
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
    public static Activity getActivity(){
        return activity;
    }

    //初始化各控件
    public void init(){
        work=true;
        isBinded=false;
        mainActivityContext=MainActivity.this;
        activity=MainActivity.this.getParent();
        MessageManager.initAllList();
        ThemeColor.backColorStr=MessageManager.getSharedPreferences().getString("currentBackStr",ThemeColor.defaultBackColorStr);
        Log.e(TAG, "init: 颜色字符串:"+ThemeColor.backColorStr );

        Connect.init();             //初始化适配器列表
        if(!AlbumUtil.checkStorage(this)){
            AlbumUtil.requestStorage(this);        //申请存储读取权限
        }
        naviQBadgeView=new QBadgeView(this);
        //sharedPreferences=getSharedPreferences("myInfo",MODE_PRIVATE);
        //editor=sharedPreferences.edit();

        navigationView=(NavigationView)findViewById(R.id.navi);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        headerView= navigationView.inflateHeaderView(R.layout.navi_header);             //动态加载headerview，为了其中头像点击事件

        if(ThemeColor.backColorStr!=null) {
            ThemeColor.setTheme(MainActivity.this,toolbar);
            headerView.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
        }
        cmImageLayout=findViewById(R.id.cmImage);
        navi_head=(ImageView)headerView.findViewById(R.id.navi_head);
        head_home=(ImageView)findViewById(R.id.head_home);
        nichengTV=(TextView)headerView.findViewById(R.id.nicheng);
        titleTV=(TextView)findViewById(R.id.title_tv);
        toolbar.setTitle("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.setVisibility(View.VISIBLE);
                        cmImageLayout.setVisibility(View.GONE);//3秒后切换
                    }
                });
            }
        }).start();
        //初始化登陆人信息，若此前登陆过，初始化聊天信息和好友信息
        if(MessageManager.getSharedPreferences().contains("userName")){  //有此userName键值，说明登陆过
            MessageManager.getSmackUserInfo().setUserName(MessageManager.getSharedPreferences().getString("userName",""));
            String nicName=MessageManager.getSharedPreferences().getString("NICKNAME","");
            if(nicName.equals("")){
                MessageManager.getSmackUserInfo().setNiC(MessageManager.getSmackUserInfo().getUserName());

            }else{
                MessageManager.getSmackUserInfo().setNiC(nicName);

            }
            MessageManager.getSmackUserInfo().setSex(MessageManager.getSharedPreferences().getString("gender","保密"));
            MessageManager.getSmackUserInfo().setEmail(MessageManager.getSharedPreferences().getString("email",""));
            String cachePasswd=MessageManager.getSharedPreferences().getString("password","");//缓存的密码
            String headBtRoad=MessageManager.getSharedPreferences().getString("userHeadBtRoad","");
            MessageManager.getSmackUserInfo().setHeadBt(BitmapFactory.decodeFile(headBtRoad));
            Log.d(TAG, "init: 设置自己头像之后");
            MessageManager.setDataBaseHelp(MessageManager.getSmackUserInfo().getUserName());

            //TODO        初始化聊天信息和好友信息
            MessageManager.setContantFriendInfoList(MessageManager.getDataBaseHelp().getContantFriendInfoList());
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    Connect.login(MessageManager.getSmackUserInfo().getUserName(), cachePasswd);
                    Log.e(TAG, "run: 上次登录启动自动登录："+MessageManager.getSmackUserInfo().getUserName()+cachePasswd );
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setInfo();
                        }
                    });
                }
            }).start();




        }else{//此前没有登陆过
            MessageManager.getSmackUserInfo().setUserName("点击登录");
            MessageManager.getSmackUserInfo().setNiC("点击登录");
            Bitmap bitmap=BitmapFactory.decodeResource(this.getResources(),R.drawable.unlogin);
            MessageManager.getSmackUserInfo().setHeadBt(bitmap);           //设置为未登陆的照片
            MessageManager.getSmackUserInfo().setEmail("");
            MessageManager.getSmackUserInfo().setSex("secrecy");
        }
        setInfo();        //调用设置头像、昵称函数

    }

    //设置昵称、头像...
    public static void setInfo(){
        navi_head.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());
        head_home.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());
        nichengTV.setText(MessageManager.getSmackUserInfo().getNiC());
    }
    //设置默认信息
    public static void setDefaultSmackInfo(){
        MessageManager.getSmackUserInfo().setNiC("点击登录");
        Bitmap bitmap=BitmapFactory.decodeResource(getInstance().getResources(),R.drawable.unlogin);
        MessageManager.getSmackUserInfo().setHeadBt(bitmap);           //设置为未登陆的照片
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
                naviQBadgeView.bindTarget(tab_iv);
            }
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
                    setInfo();
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
                    Toast.makeText(this,"您拒绝了存储权限，可能会影响使用",Toast.LENGTH_SHORT).show();
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
        if((ThemeColor.changed)&&(ThemeColor.backColorStr!=null)) {
            ThemeColor.setTheme(MainActivity.this,toolbar);
            headerView.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
            ThemeColor.changed=false;
        }
        setInfo();
    }

    @Override
    protected void onDestroy() {
        //退出登录
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Connect.getXMPPTCPConnection()!=null) {
                    Connect.signOut();
                    Connect.setRoster(null);
                    Connect.setXmpptcpConnection(null);
                    Connect.isLogined = false;
                }
            }
        }).start();
        int i=Integer.MIN_VALUE;
        while(Connect.isLogined){i++;}
        if(isBinded)unBindPacket();
        super.onDestroy();
    }
    public static void setFragmentTabHostVisibility( boolean visibility){
        if (visibility) {
            fragmentTabHost.setVisibility(View.VISIBLE);
        } else {
            fragmentTabHost.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(isChoose_flag()){setChoose_flag(false);super.onBackPressed();}
        else {
            if (System.currentTimeMillis() - backPressTime < 2000) {
                finish();
            } else {
                backPressTime = System.currentTimeMillis();
                Toast.makeText(mainActivityContext, "再次按返回键退出", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 静态方法，用于获得计算后的文件大小
     * @param fileS 文件大小
     * @return 格式化后的字符串
     */
    private static String formetFileSize(long fileS)
    {
        DecimalFormat df =new DecimalFormat("#.00");
        String fileSizeString ="";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if(fileS <1024)fileSizeString = df.format((double) fileS) +"B";
        else if(fileS <1048576) fileSizeString = df.format((double) fileS /1024) +"KB";
        else if(fileS <1073741824) fileSizeString = df.format((double) fileS /1048576) +"MB";
        else fileSizeString = df.format((double) fileS /1073741824) +"GB";
        return fileSizeString;

    }
}