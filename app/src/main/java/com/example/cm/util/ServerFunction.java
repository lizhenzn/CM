package com.example.cm.util;

import android.os.Environment;
import android.util.Log;

import com.example.cm.share.ShareItem;

import java.io.File;
import java.util.ArrayList;

import main.PostInfo;
import main.ShareManager;

public class ServerFunction {
    private static File cacheDir;
    private static ShareManager shareManager;
    private static ShareManager shareManagerForPresent;
    private  ArrayList<PostInfo> currentPostList;
    private  int currentPostPosition;


    public ServerFunction(File cacheDir){
        this.cacheDir = cacheDir;
        shareManager=new ShareManager();
        shareManagerForPresent=new ShareManager();
        currentPostList=null;
        currentPostPosition=0;
    }
    public static ShareManager getShareManager(){
        return shareManager;
    }
    public static ShareManager getShareManagerForPresent(){
        return shareManagerForPresent;
    }
    public static void sendRemark(PostInfo postInfo,String userName,String content,String time){
        shareManager.sendRemark(postInfo.createRemark(content, userName, time));
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
    public PostInfo getPost(){
        return currentPostList.get(currentPostPosition);
    }
    public  void loadPostList(){
        currentPostPosition=0;
        shareManager.resetTransferFlags();
        currentPostList=shareManager.nextPostBatch(cacheDir);
    }
    public boolean nextPost(){
        currentPostPosition++;
        if (currentPostPosition > 9)
            return false;
        if(currentPostPosition>currentPostList.size()-1)
            return false;
        return true;
    }

    public String getUserName(){
        return currentPostList.get(currentPostPosition).getUsername();
    }
    public void loadSmallImg(){
        shareManager.getImage4Posts(cacheDir,currentPostList,true);
    }
    public File getSmallUpImg(){
        Log.d("test", "getSmallUpImg: "+currentPostPosition);
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(0));
    }
    public File getSmallDownImg(){
        return new File(cacheDir.getAbsolutePath()+"/"+"small_"+currentPostList.get(currentPostPosition).getImgs().get(1));
    }

    public static void loadImg(PostInfo postInfo){
        shareManagerForPresent.getImage4Post(cacheDir,postInfo,false);
    }
    public static File getUpImg(PostInfo postInfo){
        return new File(cacheDir.getAbsolutePath()+"/"+postInfo.getImgs().get(0));
    }
    public static File getDownImg(PostInfo postInfo){
        return new File(cacheDir.getAbsolutePath()+"/"+postInfo.getImgs().get(1));
    }
    public String getDescription(){
       return  currentPostList.get(currentPostPosition).getContent();
    }
    public int getCurrentPostPosition(){return currentPostPosition;}

}
