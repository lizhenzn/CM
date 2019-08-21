package com.example.cm.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import main.PostInfo;
import main.ShareManager;

public class ServerFunction {
    private File cacheDir;
    private  ShareManager shareManager;
    private  ArrayList<PostInfo> currentPostList;
    private  int currentPostPosition;

    public ServerFunction(File cacheDir){
        this.cacheDir = cacheDir;
        this.shareManager=new ShareManager();
        currentPostList=null;
        currentPostPosition=0;
    }
    public  boolean sendPost( String title, String content, int img_num, String clothes_up,String clothes_down,  String username, int like_num) {
        PostInfo postInfo = new PostInfo();
        postInfo.setTitle(title);
        postInfo.setContent(content);
        postInfo.setImg_num(img_num);
        ArrayList<String> imgs=new ArrayList<String>();
        imgs.add(clothes_up);
        imgs.add(clothes_down);
        postInfo.setImgs(imgs);
        postInfo.setUsername(username);
        postInfo.setLike_num(like_num);
        postInfo.setTime("2019-08-21 15:02:34");
        return shareManager.sendPost(postInfo);
    }
    public  void refresh(){
        currentPostPosition=0;
        currentPostList=null;
        shareManager.rewind();
    }
    public  void loadPostList(){
        currentPostList=shareManager.nextPostBatch(cacheDir);
        currentPostPosition=0;
    }
    public boolean nextPost(){
        currentPostPosition++;
        if (currentPostPosition > 9)
            return false;
        if(currentPostPosition>currentPostList.size())
            return false;
        return true;
    }

    public String getUserName(){
        return currentPostList.get(currentPostPosition).getUsername();
    }
    public File getSmallUpImg(){
        shareManager.getImage4Post(cacheDir,currentPostList.get(currentPostPosition),true);
        Log.d("test", "getSmallUpImg: "+new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(0)).exists());
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(0));
    }
    public File getSmallDownImg(){
        shareManager.getImage4Post(cacheDir,currentPostList.get(currentPostPosition),true);
        Log.d("test", "getSmallDownImg: "+currentPostList.get(currentPostPosition).getImgs().size());
        Log.d("test", "getSmallDownImg: "+currentPostList.get(currentPostPosition).getImgs().get(0));
        Log.d("test", "getSmallDownImg: "+currentPostList.get(currentPostPosition).getImgs().get(1));
        Log.d("test", "getSmallDownImg: "+"done");
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(1));
    }
    public File getUpImg(){
        shareManager.getImage4Post(cacheDir,currentPostList.get(currentPostPosition),false);
        return new File(cacheDir.getAbsolutePath()+"/"+currentPostList.get(currentPostPosition).getImgs().get(0));
    }
    public File getDown(){
        shareManager.getImage4Post(cacheDir,currentPostList.get(currentPostPosition),false);
        return new File(cacheDir.getAbsolutePath()+"/"+currentPostList.get(currentPostPosition).getImgs().get(1));
    }
    public String getDescription(){
       return  currentPostList.get(currentPostPosition).getContent();
    }

}
