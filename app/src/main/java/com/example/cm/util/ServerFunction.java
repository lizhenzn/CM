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
    public ShareManager getShareManager(){
        return shareManager;
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
    public void loadSmallImg(){
        shareManager.getImage4Posts(cacheDir,currentPostList,true);
        shareManager.getImage4Posts(cacheDir,currentPostList,true);
    }
    public File getSmallUpImg(){
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(0));
    }
    public File getSmallDownImg(){
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(1));
    }

    public void loadImg(){
        shareManager.getImage4Posts(cacheDir,currentPostList,false);
        shareManager.getImage4Posts(cacheDir,currentPostList,false);
    }
    public File getUpImg(){
        return new File(cacheDir.getAbsolutePath()+"/"+currentPostList.get(currentPostPosition).getImgs().get(0));
    }
    public File getDown(){
        return new File(cacheDir.getAbsolutePath()+"/"+currentPostList.get(currentPostPosition).getImgs().get(1));
    }
    public String getDescription(){
       return  currentPostList.get(currentPostPosition).getContent();
    }

}
