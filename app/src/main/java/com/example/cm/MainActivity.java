package com.example.cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;

import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

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
private SharedPreferences sharedPreferences;
private SharedPreferences.Editor editor;
private final int LOGIN=1;
    private static final String TAG = "MainActivity";
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
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
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
        Log.d("获取头像并设置", "onCreate: 设置侧滑栏头像之前");

        //head_home.setImageDrawable(Connect.getUserImage(Connect.xmpptcpConnection.getUser().split("/")[0]));
        Log.d("获取头像并设置", "onCreate: 设置侧滑栏头像之后");

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
        head_home=(ImageView)findViewById(R.id.head_home);
        nichengTV=(TextView)headerView.findViewById(R.id.nicheng);
        zhangHaoTV=(TextView)headerView.findViewById(R.id.zhanghao);
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
            head_home.setImageDrawable(drawable);
            //navi_head.setImageDrawable(Connect.getHeadImage("lizhen"));
            //head_home.setImageDrawable(Connect.getHeadImage("lizhen"));
            //toolbar.setNavigationIcon(drawable);
            //ImageView homeIM=(ImageView)findViewById(android.R.id.home);
           // homeIM.setImageDrawable(drawable);
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
            tabSpec.setIndicator(indicator);
            fragmentTabHost.addTab(tabSpec,tabs1.getaClass(),null);

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
                    Connect.changeImage(Connect.xmpptcpConnection,photoRoad);
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
                    Intent intent=new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,AlbumUtil.OPEN_ALBUM);
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出登录
        if(Connect.xmpptcpConnection!=null){
            Connect.signOut();
            Connect.xmpptcpConnection=null;
        }
    }
}
