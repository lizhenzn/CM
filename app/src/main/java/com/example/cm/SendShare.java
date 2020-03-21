package com.example.cm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cm.match.MatchFragment;
import com.example.cm.myInfo.SmackUserInfo;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;
import com.example.cm.util.ServerFunction;
import com.example.cm.wardrobe.WardrobeFragment;

import java.util.ArrayList;

import main.PostInfo;
import main.ShareManager;
//2020-3-21 11:22
public class SendShare extends AppCompatActivity {
    private static final String TAG = "SendShare";
    private View view;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_share);
        context=this;
        //上衣图片
        ImageView clothes_up = findViewById(R.id.clothes_up);
        Log.d(TAG, "onCreate: clothes_up and clothes_down="+MainActivity.getClothes_up()+","+MainActivity.getClothes_down());
        if(MainActivity.getClothes_up()!=-1){
            clothes_up.setImageBitmap(WardrobeFragment.photoList1.get(MainActivity.getClothes_up()));
        }
        //下衣图片
        ImageView clothes_down = findViewById(R.id.clothes_down);
        if(MainActivity.getClothes_down()!=-1){
            clothes_down.setImageBitmap(WardrobeFragment.photoList2.get(MainActivity.getClothes_down()));
        }
        //发送按钮
        Button send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText=findViewById(R.id.input);
                String text=editText.getText().toString();
                ShareManager shareManager=new ShareManager();
                PostInfo postInfo=new PostInfo();
                postInfo.setLike_num(0);
                postInfo.setUsername( MessageManager.getSmackUserInfo().getUserName());
                String clothes_up=WardrobeFragment.getImgRealPath(MainActivity.getClothes_up(),WardrobeFragment.TYPE_UP,MainActivity.getInstance());
                String clothes_down=WardrobeFragment.getImgRealPath(MainActivity.getClothes_down(),WardrobeFragment.TYPE_DOWN,MainActivity.getInstance());
                ArrayList<String> clothes=new ArrayList<String>();
                clothes.add(0,clothes_up);
                clothes.add(1,clothes_down);
                postInfo.setImgs(clothes);
                postInfo.setImg_num(2);
                postInfo.setContent(text);
                postInfo.setTitle("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        shareManager.sendPost(postInfo);
                        finish();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.getInstance(),"成功发送",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
