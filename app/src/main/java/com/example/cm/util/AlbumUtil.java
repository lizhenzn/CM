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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.AccuseFriendActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


//相册工具类
public class AlbumUtil {
    public static final int REQUEST_STORAGE=6;
    public static final int REQUEST_CAMERA=8;
    public static final int OPEN_ALBUM=7;
    public static String FRIENDHEADFILEROAD="/data/data/com.example.cm/cache/friendHead";          //好友头像存储文件 本应用的cache中
    private static String MESSAGEBITMAP="/data/data/com.example.cm/cache/messageBitmap";            //聊天信息图片存储文件
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
    public static boolean checkCamera(Context context){
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            return false;
        return  true;
    }
    public static void requestCamera(Context context){
        //需要自己在活动里面重写 onRequestPermissionsResult()接收结果 requestCode为AlbumUtil.REQUEST_STORAGE
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }


    //处理返回的结果
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getImageAbsolutePath(Intent data,Context context){
        String imagePath=null;
        if(data==null){
            Log.d("", "getImageAbsolutePath: 没有选择图片");
            return null;
        }
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
    /*把头像、或者聊天记录图像保存进文件
    * param user
    * param bitmap
    * param return bitmapRoad
    */
    public static String saveHeadBitmap(String user,Bitmap bitmap){
        String fileRoad=null;
        String filename=null;
        fileRoad=FRIENDHEADFILEROAD+"/"+MessageManager.getSmackUserInfo().getUserName();
        filename=user+"head.jpg";

        String sdStatus= Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {//检测SD是否可用
            Log.d("", "saveHeadBitmap: SD卡不可用");
            return null;
        }
        FileOutputStream b=null;
        File file=new File(fileRoad,filename);          //创建保存头像的文件
        if(!file.exists()){                      //文件不存在就创建
            try {
                if(file.getParentFile()!=null&&!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{                                    //存在就已经保存过，直接返回
            Log.d("", "saveHeadBitmap: 存在"+file.getAbsolutePath());
            if(file.delete()) {
                try {
                    Log.e("", "saveHeadBitmap: 删除存在的好友头像成功" );
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try{
            b=new FileOutputStream(file,false);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,b);//把数据写入文件
            b.flush();
            b.close();
            Log.e("", "saveHeadBitmap: 保存图片位置"+file.getAbsolutePath() );
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    /*
    * @param bitmap
    * @return path
    * */
    public  static String saveMessageBitmap(Bitmap bitmap){
        String path=null;
        String fileRoad=MESSAGEBITMAP+"/"+MessageManager.getSmackUserInfo().getUserName();
        String fileName=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
        Log.e("", "saveMessageBitmap: "+fileName );
        File file=new File(fileRoad,fileName);
        if(!file.exists()){
            try {
                if(file.getParentFile()!=null&&!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream b=null;
            b=new FileOutputStream(file,false);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,b);//把数据写入文件
            b.flush();
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("", "saveHeadBitmap: 保存图片位置"+file.getAbsolutePath() );
        path=file.getAbsolutePath();

        return path;
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
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("", "getImageStr: 图片转换成字符串异常");
            return null;
        }finally {
            Log.e("", "getImageStr: 图片字符串长度"+imageStr.length() );
            if(file.exists()){
                file.delete();
            }
        }


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
    /*
    * @param path 图片路径
    * @return bitmap bitmap图
    * */
    public static Bitmap getBitmapByPath(String path){
        Bitmap bitmap=null;
        if(path!=null){
            File file=new File(path);
            if(file.exists()) {
                Uri filePath = Uri.fromFile(file);
                bitmap = BitmapFactory.decodeFile(filePath.getPath());
            }else{
                bitmap=BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.drawable.photo_load_error);
            }
        }else{
            bitmap=BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.drawable.photo_load_error);
        }

        return bitmap;
    }
    /*
    * @param finalPath
    * @param time
    * @return bitmap
    * */
    public static Bitmap getBitmapByUrl(String finalPath,long time){
        Bitmap bitmap=null;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
        Date date=new Date(time);
        String dateStr=simpleDateFormat.format(date);
        try {
            URL url=new URL(MessageManager.BASEURL+dateStr+"/"+finalPath);
            InputStream inputStream=url.openStream();
            bitmap=BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }




}
