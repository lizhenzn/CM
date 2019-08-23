package com.example.cm.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.cm.myInfo.MyInfoActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

import java.util.Base64.Encoder;

import static java.lang.System.in;

//相册工具类
public class AlbumUtil {
    public static final int REQUEST_STORAGE=6;
    public static final int OPEN_ALBUM=7;
    //申请访问存储权限 传入参数当前活动
    //检测是否有存储权限
    public static boolean checkStorage(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            return false;
        return  true;
    }
    //申请访问存储权限 传入参数当前活动
    public static void requestStorage(Context context){

            //需要自己在活动里面重写 onRequestPermissionsResult()接收结果 requestCode为AlbumUtil.REQUEST_STORAGE
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE);

    }

    //处理返回的结果
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getImageAbsolutePath(Intent data,Context context){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(context,uri)){
            //如果是Document类型文件，则通过Document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection,context);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null,context);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null,context);
        }else if("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
        //displayImage(imagePath,context);
    }
    public static String getImagePath(Uri uri,String selection,Context context){
        String path=null;
        //通过Uri和selection获取图片真实路径
        Cursor cursor=context.getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return  path;
    }
    public static void displayImage(String imagePath,Context context){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            //head_left_iv.setImageBitmap(bitmap);
            //setPicToView(bitmap);
        }else
            Toast.makeText(context,"Failed to get image",Toast.LENGTH_SHORT).show();
    }
    //把图像保存进文件
    public static void saveBitmap(Bitmap bitmap){
        String sdStatus= Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED))//检测SD是否可用
            return;
        FileOutputStream b=null;
        File file=new File(MyInfoActivity.path);
        file.mkdirs();//创建文件夹
        String fileName=MyInfoActivity.path+"head.jpg";
        try{
            b=new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,b);//把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try{
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getImageStr(String absoluteRoad){
        String imageStr=null;
        File file=new File(absoluteRoad);
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            imageStr=new String(Base64.encodeToString(data,0,data.length,Base64.NO_WRAP));
            //imageStr = new String(Base64.getEncoder().encode(data));
            return imageStr;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("", "getImageStr: 图片转换成字符串异常");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("", "getImageStr: 图片转换成字符串异常");
        }

        return imageStr;
    }
    //Base64 decode后的字符串转byte[]
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static  byte[] encodedStr2byte(String encodeStr){
        byte []bytes=null;
        bytes=Base64.decode(encodeStr,Base64.NO_WRAP);
        //bytes=Base64.getDecoder().decode(encodeStr);
        return bytes;
    }
    //byte[]转Bitmap
    public static Bitmap byte2Bitmap(byte[] bytes){
        Bitmap bitmap=null;
        if(bytes.length!=0){
            bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        return bitmap;
    }

}
