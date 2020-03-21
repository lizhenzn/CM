package com.example.cm.myInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText user_et,passwd_et,insurePasswd_et,email_et;
    private TextView birthday_tv1,birthday_tv2,birthday_tv3,sex_tv;
    private String birthday,sex;
    private Button register_btn;
    private LinearLayout register_headImage_LL;
    private ImageView headImage;
    private String absoluteRoad;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Toolbar toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        init();
    }

    private void init() {
        register_headImage_LL=(LinearLayout)findViewById(R.id.register_headimage_LL);
        user_et=(EditText)findViewById(R.id.register_user_et);
        passwd_et=(EditText)findViewById(R.id.register_passwd_et);
        insurePasswd_et=(EditText)findViewById(R.id.register_insurePasswd_et);
        email_et=(EditText)findViewById(R.id.register_email_et_);
        birthday_tv1=(TextView)findViewById(R.id.register_birthday_tv1);
        birthday_tv2=(TextView)findViewById(R.id.register_birthday_tv2);
        birthday_tv3=(TextView)findViewById(R.id.register_birthday_tv3);
        sex_tv=(TextView)findViewById(R.id.register_sex_tv);
        register_btn=(Button)findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
        headImage=(ImageView)findViewById(R.id.register_headImage);
        progressDialog=new ProgressDialog(RegisterActivity.this);
        birthday_tv1.setText("2019");
        birthday_tv2.setText("1");
        birthday_tv3.setText("1");

    }
    //身高选择
    private void birthdayChoose(){
        View contentView= View.inflate(RegisterActivity.this,R.layout.numberpicker_birthday,null);
        View rootView=View.inflate(RegisterActivity.this,R.layout.register,null);
        final NumberPicker numberPicker_year=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayYear);
        final NumberPicker numberPicker_month=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayMonth);
        final NumberPicker numberPicker_day=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayDay);
        numberPicker_year.setMaxValue(2019);
        numberPicker_year.setMinValue(1990);
        numberPicker_year.setValue(2019);
        numberPicker_month.setMaxValue(12);
        numberPicker_month.setMinValue(1);
        numberPicker_month.setValue(1);
        numberPicker_day.setMaxValue(30);
        numberPicker_day.setMinValue(1);
        numberPicker_day.setValue(1);
        PopupWindow popupWindow=new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
        numberPicker_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                birthday_tv1.setText(String.valueOf(numberPicker_year.getValue()));

            }
        });
        numberPicker_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                birthday_tv2.setText("."+String.valueOf(numberPicker_month.getValue()));

            }
        });
        numberPicker_day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                birthday_tv3.setText("."+String.valueOf(numberPicker_day.getValue()));

            }
        });
         birthday=birthday_tv1.getText()+"."+birthday_tv2.getText()+"."+birthday_tv3.getText();


    }
    //性别选择
    private void sexChoose(){
        String []xingBie={"男","女","保密"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(xingBie, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sex_tv.setHint(xingBie[which]);
                sex=String.valueOf(sex_tv.getText());
                Log.e("", "onClick: 您选择的："+sex );
                if(sex.equals("男")){
                    sex="male";
                }else if(sex.equals("女")){
                    sex="female";
                }else{
                    sex="secrecy";
                }
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_headimage_LL:{
                //检测存储权限，若有直接打开相册，否则申请权限
                if(AlbumUtil.checkStorage(RegisterActivity.this)){
                    Intent intent=new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,AlbumUtil.OPEN_ALBUM);
                }else{
                    AlbumUtil.requestStorage(RegisterActivity.this);
                }
            }break;
            case R.id.register_birthday_TIL:birthdayChoose();break;
            case R.id.register_sex_TIL:sexChoose();break;
            case R.id.register_btn:{
                String user= String.valueOf(user_et.getText());
                String passwd= String.valueOf(passwd_et.getText());
                String insurePasswd= String.valueOf(insurePasswd_et.getText());

                String email= String.valueOf(email_et.getText());
                Map <String,String> map=new HashMap<>();
                map.put("email",email);
                map.put("gender",sex);
                map.put("birthday",birthday);
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
                            if(Connect.register(user,passwd,map)){//注册成功然后设置选择的头像，若设置失败，则注销此用户
                                //TODO 注册后不能登录，要先设置头像，空指针错误
                                Log.d("22233", "run: 注册成功");
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                            }else{
                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"注册异常",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case AlbumUtil.OPEN_ALBUM:{
                if(resultCode==RESULT_OK){
                    absoluteRoad=AlbumUtil.getImageAbsolutePath(data,RegisterActivity.this);
                    Bitmap bitmap= BitmapFactory.decodeFile(absoluteRoad);
                    headImage.setImageBitmap(bitmap);
                }else{
                    Toast.makeText(this,"获取图片异常",Toast.LENGTH_SHORT).show();
                }
            }break;
            default:break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            default:break;
        }
    }
}
