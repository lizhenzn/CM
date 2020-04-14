package com.example.cm.friend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.OkHttpClient;

public class AccuseFriendActivity extends AppCompatActivity implements View.OnClickListener {
private EditText accuseET;
private ImageView accuseIV1,accuseIV2,accuseIV3;
private Button submitBtn;
private String userName;
private String[] bitmapStrs;
private String[] absoluteRoads;
private ProgressDialog progressDialog;
private int bitmapCount;
private int currentBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accuse_friend);
        Toolbar toolbar=(Toolbar)findViewById(R.id.accuse_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        init();
    }
    private void init(){
        accuseET=findViewById(R.id.accuse_et);
        accuseIV1=findViewById(R.id.accuse_iv1);
        accuseIV2=findViewById(R.id.accuse_iv2);
        accuseIV3=findViewById(R.id.accuse_iv3);
        submitBtn=findViewById(R.id.accuse_btn);
        accuseIV1.setOnClickListener(this);
        accuseIV2.setOnClickListener(this);
        accuseIV3.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        bitmapStrs=new String[3];
        absoluteRoads=new String[3];
        bitmapCount=0;
        currentBitmap=-1;
        userName=getIntent().getStringExtra("userName");
        progressDialog=new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.accuse_btn:{
                if(Connect.isLogined) {

                        String accuse = accuseET.getText().toString();
                        if(bitmapCount==0){
                            Toast.makeText(this,"图片证明不能为空",Toast.LENGTH_SHORT).show();
                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AccuseFriendActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.setTitle("");
                                            progressDialog.setCancelable(false);
                                            progressDialog.setMessage("发送中...");
                                            progressDialog.show();
                                        }
                                    });
                                    try {
                                        ChatManager chatManager = ChatManager.getInstanceFor(Connect.getXMPPTCPConnection());
                                        Chat chat = chatManager.createChat("administrator" + "@" + Connect.SERVERNAME);
                                        chat.sendMessage(toJson("举报：" + userName +"\n理由："+ accuse, "text", new Date().getTime()));
                                        OkHttpClient okHttpClient=new OkHttpClient();
                                        for(int i=0;i<bitmapCount;i++){
                                            main.ImgManager.ImageTrans(absoluteRoads[i],null,okHttpClient);
                                            Thread.sleep(500);
                                        }
                                        Thread.sleep(1000);
                                        for(int i=0;i<bitmapCount;i++){
                                            chat.sendMessage(toJson(bitmapStrs[i],"photo",new Date().getTime()));
                                        }
                                    } catch (SmackException.NotConnectedException e) {
                                        e.printStackTrace();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        AccuseFriendActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AccuseFriendActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });

                                    }
                                }
                            }).start();

                        }

                }else{
                    Toast.makeText(this,"未登录",Toast.LENGTH_SHORT).show();
                }
            }break;
            case R.id.accuse_iv1:{
                currentBitmap=1;
                chooseBitmap();

            }break;
            case R.id.accuse_iv2:{
                currentBitmap=2;
               chooseBitmap();
            }break;
            case R.id.accuse_iv3:{
                currentBitmap=3;
                chooseBitmap();
            }break;
            default:break;
        }
    }
    public void chooseBitmap(){
        if (AlbumUtil.checkStorage(AccuseFriendActivity.this)) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, AlbumUtil.OPEN_ALBUM);
        } else {
            AlbumUtil.requestStorage(AccuseFriendActivity.this);
            //Toast.makeText(ChatActivity.this,"You denied the permission",Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AlbumUtil.OPEN_ALBUM: {
                String absoluteRoad = AlbumUtil.getImageAbsolutePath(data, AccuseFriendActivity.this);
                if (absoluteRoad != null) {            //选择图片
                    Log.d("选择的图片路径", "onActivityResult: " + absoluteRoad);
                    String[] tempPath=absoluteRoad.split("/");
                    String finalPath=tempPath[tempPath.length-1];
                    Bitmap bitmap=AlbumUtil.getBitmapByPath(absoluteRoad);
                    switch (currentBitmap){
                        case 1:{
                            if(bitmapCount<1){
                                bitmapCount++;
                            }
                            bitmapStrs[0]=finalPath;
                            absoluteRoads[0]=absoluteRoad;
                            accuseIV1.setImageBitmap(bitmap);
                            accuseIV2.setVisibility(View.VISIBLE);
                        }break;
                        case 2:{
                            if(bitmapCount<2){
                                bitmapCount++;
                            }
                            bitmapStrs[1]=finalPath;
                            absoluteRoads[1]=absoluteRoad;
                            accuseIV2.setImageBitmap(bitmap);
                            accuseIV3.setVisibility(View.VISIBLE);
                        }break;
                        case 3:{
                            if(bitmapCount<3){
                                bitmapCount++;
                            }
                            bitmapStrs[2]=finalPath;
                            absoluteRoads[2]=absoluteRoad;
                            accuseIV3.setImageBitmap(bitmap);
                        }
                    }



                }
            }break;
            default:break;
            }
        }


    public String toJson(String string, String string0, Long dateTime) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("data",string);
            jsonObject.put("type",string0);
            jsonObject.put("date",dateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
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
