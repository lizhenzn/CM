package com.example.cm.myInfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.ActionSheetDialog;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

@SuppressLint("SdCardPath")
public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView head_left_iv,head_right_iv;
    private View head_view,xingBie_view,niCheng_view;
    private TextView xingBie_tv,email_tv,nicheng_tv,account_tv;
    public static String path="/sdcard/Clothes/MyInfo/head";
    public static final int OPEN_ALBUM=1;
    private String gender;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info);
        Toolbar toolbar=findViewById(R.id.myInfo_toolbar);
        ThemeColor.setTheme(MyInfoActivity.this,toolbar);
        setSupportActionBar(toolbar);
        initView();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String nc= MessageManager.getSmackUserInfo().getNiC();
        nicheng_tv.setText(nc);
        account_tv.setText(MessageManager.getSmackUserInfo().getUserName());
        email_tv.setText(MessageManager.getSmackUserInfo().getEmail());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView(){
        //初始化控件
        Toolbar toolbar=(Toolbar)findViewById(R.id.myInfo_toolbar);
        setSupportActionBar(toolbar);
        head_view=(View)findViewById(R.id.head_view);
        head_left_iv=(ImageView)findViewById(R.id.head_left_iv);
        head_right_iv=(ImageView)findViewById(R.id.head_right_iv);
        xingBie_view=(View)findViewById(R.id.xingbie_view);
        xingBie_tv=(TextView)findViewById(R.id.xingbie_tv);
        account_tv=(TextView)findViewById(R.id.account_tv);
        email_tv=(TextView)findViewById(R.id.email_tv);
        niCheng_view=(View)findViewById(R.id.nichen_view);
        nicheng_tv=(TextView)findViewById(R.id.nichen_tv);
        head_view.setOnClickListener((View.OnClickListener) this);
        xingBie_view.setOnClickListener(this);
        email_tv.setOnClickListener(this);
        niCheng_view.setOnClickListener(this);
        nicheng_tv.setOnClickListener(this);
        //设置信息
        head_left_iv.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());

        String nc=MessageManager.getSmackUserInfo().getNiC();
        nicheng_tv.setText(nc);
        String gender=MessageManager.getSmackUserInfo().getSex();
        if(gender.equals("male")){
            xingBie_tv.setText("男");
        }else if(gender.equals("female")){
            xingBie_tv.setText("女");
        }else{
            xingBie_tv.setText("保密");
        }
        account_tv.setText(MessageManager.getSmackUserInfo().getUserName());
        email_tv.setText(MessageManager.getSmackUserInfo().getEmail());
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.head_view:
                if(Connect.isLogined) {
                    if (!AlbumUtil.checkStorage(MyInfoActivity.this))
                        AlbumUtil.requestStorage(MyInfoActivity.this);
                    else
                        openAlbum();
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.xingbie_view:
                if(Connect.isLogined) {
                    showXingBieChoose();
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nichen_view:
                if(Connect.isLogined) {
                    Intent intent = new Intent(MyInfoActivity.this, EditNiChengActivity.class);
                    String nc = nicheng_tv.getText().toString();
                    intent.putExtra("niCheng", nc);
                    startActivityForResult(intent, 2);
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.email_tv:{
                if(Connect.isLogined) {
                    Intent intent=new Intent(MyInfoActivity.this,EditEmailActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
            }break;
            default:break;
        }
    }

    private void showXingBieChoose() {
        new ActionSheetDialog(MyInfoActivity.this)
                .builder()
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .addSheetItem("男", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        gender="male";
                        xingBie_tv.setText("男");
                        Log.e("", "onClick: 点击"+"男" );
                    }
                }).addSheetItem("女", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                gender="female";
                xingBie_tv.setText("女");
                Log.e("", "onClick: 点击"+"女" );
            }
        }).addSheetItem("保密", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                gender="secrecy";
                xingBie_tv.setText("保密");
                Log.e("", "onClick: 点击"+"保密" );
            }
        }).show();
        VCardManager.setSelfInfo(Connect.getXMPPTCPConnection(),"gender", gender);
        MessageManager.getSmackUserInfo().setSex(gender);
    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        switch (requestCode){
            case AlbumUtil.REQUEST_STORAGE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }break;
            default:break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case OPEN_ALBUM:
                if(resultCode==RESULT_OK){
                    String imagePath=AlbumUtil.getImageAbsolutePath(data,MyInfoActivity.this);//调用工具类处理返回的数据  得到图片绝对路径
                    if(imagePath!=null){
                        Bitmap bitmap= BitmapFactory.decodeFile(imagePath);

                        try {
                            VCardManager.changeImage(Connect.getXMPPTCPConnection(),imagePath);
                            head_left_iv.setImageBitmap(bitmap);   //设置头像
                            MessageManager.getSmackUserInfo().setHeadBt(bitmap);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //AlbumUtil.saveBitmap(bitmap);
                    }else
                        Toast.makeText(MyInfoActivity.this,"Failed to get image",Toast.LENGTH_SHORT).show();

                }
                break;
            case 2:
                if(requestCode==RESULT_OK) {
                    //String nic=sharedPreferences.getString("user","");
                    nicheng_tv.setText(MessageManager.getSmackUserInfo().getNiC());
                }
                break;
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


}
