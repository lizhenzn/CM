package com.example.cm.myInfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressLint("SdCardPath")
public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView head_left_iv,head_right_iv;
    private View head_view,xingBie_view,shenGao_view,niCheng_view;
    private EditText mine_et;
    private Bitmap head;
    private Button button;
    private TextView xingBie_tv,shenGao_tv,nicheng_tv;
    private NumberPicker numberPicker;
    private PopupWindow popupWindow;
    private String xingBie[]=new String[]{"男","女","保密"};
    public static String path="/sdcard/Clothes/MyInfo/head";
    public static final int OPEN_ALBUM=1;
    //SharedPreferences sharedPreferences;
    //SharedPreferences.Editor editor;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info);
        initView();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String nc= MessageManager.getSmackUserInfo().getUserName();
        nicheng_tv.setHint(nc);
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
        shenGao_view=(View)findViewById(R.id.shengao_view);
        shenGao_tv=(TextView)findViewById(R.id.shengao_tv);
        niCheng_view=(View)findViewById(R.id.nichen_view);
        nicheng_tv=(TextView)findViewById(R.id.nichen_tv);
        head_view.setOnClickListener((View.OnClickListener) this);
        xingBie_view.setOnClickListener(this);
        shenGao_view.setOnClickListener(this);
        niCheng_view.setOnClickListener(this);
        nicheng_tv.setOnClickListener(this);
        //设置信息
        head_left_iv.setImageBitmap(MessageManager.getSmackUserInfo().getHeadBt());

        String nc=MessageManager.getSmackUserInfo().getUserName();
        nicheng_tv.setHint(nc);
        xingBie_tv.setHint(MessageManager.getSmackUserInfo().getSex());
        shenGao_tv.setHint(MessageManager.getSmackUserInfo().getHeight());

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
            case R.id.shengao_view:
                if(Connect.isLogined) {
                    shenGaoChoose();
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nichen_view:
                if(Connect.isLogined) {
                    Intent intent = new Intent(MyInfoActivity.this, EditNiChengActivity.class);
                    String nc = nicheng_tv.getHint().toString();
                    intent.putExtra("niCheng", nc);
                    startActivityForResult(intent, 2);
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showXingBieChoose() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(xingBie, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                xingBie_tv.setHint(xingBie[which]);
                String gender=null;
                if(xingBie[which].equals("男")){
                    gender="male";
                }else if(xingBie[which].equals("女")){
                    gender="female";
                }else{
                    gender="secrecy";
                }
                VCardManager.setSelfInfo(Connect.getXMPPTCPConnection(),"gender",gender);
                MessageManager.getSmackUserInfo().setSex(xingBie[which]);
                Log.e("", "onClick: 修改性别后："+gender );
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void shenGaoChoose(){
        View contentView= View.inflate(MyInfoActivity.this,R.layout.numberpicker,null);
        View rootView=View.inflate(MyInfoActivity.this,R.layout.my_info,null);
        final NumberPicker numberPicker=(NumberPicker)contentView.findViewById(R.id.number_picker);
        final int[] data = new int[1];
        numberPicker.setMaxValue(200 );
        numberPicker.setMinValue(0);
        numberPicker.setValue(175);
        PopupWindow popupWindow=new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String data=String.valueOf(numberPicker.getValue());
                VCardManager.setSelfInfo(Connect.getXMPPTCPConnection(),"height",data);
                Log.e("", "onValueChange: 修改身高后："+data );
                MessageManager.getSmackUserInfo().setHeight(data);
                shenGao_tv.setHint(data);
            }
        });


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
                        head_left_iv.setImageBitmap(bitmap);   //设置头像
                        try {
                            VCardManager.changeImage(Connect.getXMPPTCPConnection(),imagePath);
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
                    nicheng_tv.setHint(MessageManager.getSmackUserInfo().getNiC());
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
