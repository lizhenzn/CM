package com.example.cm.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ClothesEstimater {
    private Context applicationContext;
    ClothesEstimater(Context applicationContext){
        this.applicationContext=applicationContext;
    }

    public int estimateClothes(String up,String down){
        Bitmap nup=getScaleBitmap(up);
        Bitmap ndown=getScaleBitmap(down);
        if(nup==null||ndown==null)return -1;
        Bitmap bitmap=getJointBitmap(nup,ndown);
        if(bitmap!=null)return estimateGood(bitmap);
        else return -1;
    }
    private int get_max_result(float[] result) {
        float probability = result[0];
        int r = 0;
        for (int i = 0; i < result.length; i++) {
            if (probability < result[i]) {
                probability = result[i];
                r = i;
            }
        }
        Log.d("test", "get_max_result: probability:"+probability);
        return r;
    }
    /**
     * 读取标签文件
     * @return 标签列表
     */
    private ArrayList<String> read_labels(){
        ArrayList<String> list=new ArrayList<>();
        try {
            BufferedReader buffin=new BufferedReader(new InputStreamReader(applicationContext.getAssets().open("retrained_labels.txt")));
            String str;
            while((str=buffin.readLine())!=null){
                list.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    private ByteBuffer read_model_file(){
        try {
            AssetFileDescriptor descriptor=applicationContext.getAssets().openFd("retrained_mobilenetV1.tflite");
            FileChannel in=descriptor.createInputStream().getChannel();
            long start_offset=descriptor.getStartOffset();
            long end_offset=descriptor.getDeclaredLength();
            return in.map(FileChannel.MapMode.READ_ONLY,start_offset,end_offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 加载网络
     * @return 分类器实例
     */
    private Interpreter get_model(){
        ByteBuffer buffer=read_model_file();
        Interpreter interpreter=null;
        if(buffer!=null) interpreter=new Interpreter(buffer);
        if(interpreter!=null)
            interpreter.setNumThreads(4);
        return interpreter;
    }

    private ByteBuffer get_processed_matrix(Bitmap bitmap, int[] ddims){
        ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3] * 4);
        //4通道？这指的是PNG格式？那为什么也能够解码JPEG格式？
        imgData.order(ByteOrder.nativeOrder());
        // get image pixel
        int[] pixels = new int[ddims[2] * ddims[3]];
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, ddims[2], ddims[3], false);
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, ddims[2], ddims[3]);
        //转换为条状int数组
        int pixel = 0;
        for (int i = 0; i < ddims[2]; ++i) {
            for (int j = 0; j < ddims[3]; ++j) {
                final int val = pixels[pixel++];
                imgData.putFloat(((((val >> 16) & 0xFF) - 128f) / 128f));
                imgData.putFloat(((((val >> 8) & 0xFF) - 128f) / 128f));
                imgData.putFloat((((val & 0xFF) - 128f) / 128f));
            }
        }
        //通过移位操作把int数据转换为byte，从而解析为多通道数据
        if (bm.isRecycled()) {
            bm.recycle();
        }
        //这一步意图释放原本的bitmap占据的内存，但不是直接放弃引用，而是通知GC该对象为较弱引用，可能被回收
        return imgData;
    }
    /**
     * 备注：始终无法通过其他方法读取文件，文件名并不能直接解析
     *
     * @return 经过适当缩放的BitMap
     */
    private Bitmap getScaleBitmap(String filename){
        BitmapFactory.Options opt=new BitmapFactory.Options();
        opt.inJustDecodeBounds=true;
        //标准防爆内存操作，开始时只解析图片大小，不解析图片内容
        try {
            BitmapFactory.decodeStream(new FileInputStream(new File(filename)),null,opt);
            //BitmapFactory.decodeStream(getApplicationContext().getAssets().open(filename),null,opt);
            //直接通过文件路径读取不了，通过输入流读取
        } catch (IOException e) {
            e.printStackTrace();
        }
        int bmpWidth = opt.outWidth;
        int bmpHeight = opt.outHeight;

        int maxSize = 500;
        // compress picture with inSampleSize
        opt.inSampleSize = 1;
        while (!(bmpWidth / opt.inSampleSize < maxSize || bmpHeight / opt.inSampleSize < maxSize)) {
            opt.inSampleSize *= 2;
        }//缩放大小
        opt.inJustDecodeBounds = false;
        //只解析大小选项设置为false
        try {
            Bitmap bmp=BitmapFactory.decodeStream(new FileInputStream(new File(filename)),null,opt);
            //Bitmap bmp=BitmapFactory.decodeStream(getApplicationContext().getAssets().open(filename),null,opt);
            if(bmp!=null) return  zoomImg(bmp,224,112);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 利用matrix来约束图片大小
     * @param bm 输入的bitmap
     * @param newWidth 目标宽度
     * @param newHeight 目标高度
     * @return 处理后的bitmap
     */
    private static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    private Bitmap getJointBitmap(Bitmap bitmap_up,Bitmap bitmap_down){
        AlbumUtil.checkStorage(applicationContext);
        Bitmap bitmap=Bitmap.createBitmap(224,224, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        if(bitmap_down!=null&&bitmap_up!=null) {
            canvas.drawBitmap(bitmap_up, 0, 0, null);
            canvas.drawBitmap(bitmap_down, 0, 112, null);
            return bitmap;
        }
        else return null;
    }

    /**
     * @param bitmap 用来判断的已拼接图片,由getJointBitmap结果输入
     * @return 好则为1，不好为0,失败返回-1
     */
    private int estimateGood(Bitmap bitmap){
        List<String> list=read_labels();
        //HERE TO FEED THE IMAGE NAME IN ASSETS FOLDER

        ByteBuffer buff=get_processed_matrix(bitmap,new int[]{1,3,224,224});//bitmap->bytebuffer

        Interpreter interpreter=get_model();
        float[][] labelProbArray = new float[1][list.size()];//考虑到输出的结构，必须用这样的双层结构解析

        if(interpreter==null)return -1;
        else {
            interpreter.run(buff, labelProbArray);
            //时间一般在50ms-70ms，可以接受
            //在虚拟机上性能低，时间在200ms以上，较差
        }

        float[] results = new float[labelProbArray[0].length];
        System.arraycopy(labelProbArray[0], 0, results, 0, labelProbArray[0].length);
        int index=get_max_result(results);
        Log.d("test", "estimateGood: "+results[0]+" "+results[1]);
        if(results[index]>0.95)
            return index;
        else return 0;
    }

}
