package com.example.cm.myInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText user_et,passwd_et,insurePasswd_et,email_et;
    private TextView sex_tv;
    private String sex;
    private Button register_btn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Toolbar toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        ThemeColor.setTheme(RegisterActivity.this,toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        init();
    }

    private void init() {
        user_et=(EditText)findViewById(R.id.register_user_et);
        passwd_et=(EditText)findViewById(R.id.register_passwd_et);
        insurePasswd_et=(EditText)findViewById(R.id.register_insurePasswd_et);
        email_et=(EditText)findViewById(R.id.register_email_et_);
        sex_tv=(TextView)findViewById(R.id.register_sex_tv);
        sex_tv.setOnClickListener(this);
        register_btn=(Button)findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
        progressDialog=new ProgressDialog(RegisterActivity.this);

    }

    //性别选择
    private void sexChoose(){
        new ActionSheetDialog(RegisterActivity.this)
                .builder()
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .addSheetItem("男", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        sex="male";
                        sex_tv.setText("男");
                        Log.e("", "onClick: 点击"+"男" );
                    }
                }).addSheetItem("女", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                sex="female";
                sex_tv.setText("女");
                Log.e("", "onClick: 点击"+"女" );
            }
        }).addSheetItem("保密", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                sex="secrecy";
                sex_tv.setText("保密");
                Log.e("", "onClick: 点击"+"保密" );
            }
        }).show();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_sex_tv:sexChoose();break;
            case R.id.register_btn:{
                if((TextUtils.isEmpty(user_et.getText().toString().trim()))
                    ||(TextUtils.isEmpty(passwd_et.getText().toString().trim()))
                    ||(TextUtils.isEmpty(insurePasswd_et.getText().toString().trim()))
                        ||(TextUtils.isEmpty(email_et.getText().toString().trim()))
                        ||(TextUtils.isEmpty(sex_tv.getText()))){
                    Toast.makeText(this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                    break;
                }
                if(!passwd_et.getText().toString().trim().equals(insurePasswd_et.getText().toString().trim())){
                    Toast.makeText(this,"密码不一致",Toast.LENGTH_SHORT).show();
                    break;
                }
                String reg="[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]+$";
                if(!email_et.getText().toString().matches(reg)){  //判断邮箱格式
                    Toast.makeText(this,"邮箱格式错误",Toast.LENGTH_SHORT).show();
                    break;
                }
                String user= String.valueOf(user_et.getText());
                String passwd= String.valueOf(passwd_et.getText());
                String insurePasswd= String.valueOf(insurePasswd_et.getText());

                String email= String.valueOf(email_et.getText());
                Map <String,String> map=new HashMap<>();
                map.put("email",email);
                map.put("gender",sex);
                map.put("birthday","");
                map.put("height","0");
                map.put("NICKNAME",user);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setTitle("");
                                progressDialog.setMessage("注册中...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                            }
                        });
                        //@return 0、服务器无反应 1、注册成功 2、已有此账号 3、注册失败
                        int result=Connect.register(user,passwd,map);
                        switch (result){
                            case 0:{
                                Toast.makeText(RegisterActivity.this,"服务器无反应",Toast.LENGTH_SHORT).show();
                            }break;
                            case 1:{
                                Log.d("22233", "run: 注册成功");
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }break;
                            case 2:{ //TODO

                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"已有此账号",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }break;
                            case 3:{
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"注册异常",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }break;
                            default:break;
                        }
                    }
                }).start();

            }break;
            default:break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                RegisterActivity.this.finish();
            }break;
            default:break;
        }
        return true;
    }



}
