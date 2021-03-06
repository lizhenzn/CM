package com.example.cm.friend.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.cm.R;
import com.example.cm.util.ActionSheetDialog;
import com.example.cm.util.AlbumUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDetail extends AppCompatActivity {
private ImageView imageDetailIV;
private int position;
private String userName;
private String bitmapPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_image_detail);
        init();
    }
    private void init(){
        bitmapPath=getIntent().getStringExtra("bitmapPath");
        if(bitmapPath==null){
            Log.e("", "init: 图片路径为空" );
            this.finish();
        }
        //position=getIntent().getIntExtra("position",0);
        //userName=getIntent().getStringExtra("userName");
        //Bitmap bitmap= MessageManager.getMessageMap().get(userName).get(position).getPhoto();
        Bitmap bitmap= AlbumUtil.getBitmapByPath(bitmapPath);
        imageDetailIV=findViewById(R.id.image_detail_IV);imageDetailIV.setImageBitmap(bitmap);
        imageDetailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageDetailIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO
                new ActionSheetDialog(ImageDetail.this).builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("保存", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //TODO
                                String fileName=new SimpleDateFormat("yyyy-MM-dd.HHmmss").format(new Date())+".jpg";
                                //String path=MessageManager.getMessageMap().get(userName).get(position).getPhotoRoad();
                                File file=new File(bitmapPath);
                                try {
                                    MediaStore.Images.Media.insertImage(ImageDetail.this.getContentResolver(),bitmapPath,fileName,null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                // 最后通知图库更新
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(file);
                                intent.setData(uri);
                                ImageDetail.this.sendBroadcast(intent);
                            }
                        }).show();
                return true;
            }
        });
    }
}
