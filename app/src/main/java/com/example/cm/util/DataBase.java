package com.example.cm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ListView;

import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.friend.chat.Message;
import com.example.cm.myInfo.FriendInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    private Context mContext;
    private  String user;
    private  final String CREATE_TABLE_FRIENDINFO="create table FriendInfoTable"+"("//user==null//TODO
            +"userName varchar(24) Primary Key Not Null,"
            +"nicName varchar(24),"
            +"groupName varchar(24) Not Null,"
            +"email varchar(20),"
            +"headBtRoad varchar(36),"
            +"chated int"
            +");";
    private   final String CREATE_TABLE_MESSAGE="create table MessageTable"+"("
            +"fromUser varchar(24) Not Null,"
            +"toUser varchar(24) Not Null,"
            +"body varchar(120) Not Null,"
            +"type int,"
            +"messageType varchar(6),"
            +"photoRoad varchar(36),"
            +"date Varchar(36)"
            +");";
    public DataBase(Context context,String name,SQLiteDatabase.CursorFactory factory, int version,String user) {
        super(context, name, factory, version);
        mContext=context;
        this.user=user;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FRIENDINFO);
        db.execSQL(CREATE_TABLE_MESSAGE);
        Log.d("666", "onCreate: 创建数据库成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("drop table if exists FriendInfoTable");
          db.execSQL("drop table if exists MessageTable");
          onCreate(db);
        Log.d("", "onUpgrade: 数据库升级");
    }
    /*
    *给数据库添加好友信息
    * friendInfo
    * void return
     */
    public void addFriendInfo(FriendInfo friendInfo){
        ContentValues values=new ContentValues();
        values.put("userName",friendInfo.getUserName());
        values.put("nicName",friendInfo.getNicName());
        values.put("groupName",friendInfo.getGroupName());
        values.put("headBtRoad",friendInfo.getHeadBtRoad());
        values.put("chated",friendInfo.getChated());
        Connect.db.insert("FriendInfoTable",null,values);
    }
    /*
    *message
    * void return
     */
    public void addMessage(Message message){
        ContentValues values=new ContentValues();
        values.put("fromUser",message.getFrom());
        values.put("toUser",message.getTo());
        values.put("type",message.getType());
        values.put("messageType",message.getMessageType());
        values.put("body",message.getBody());
        values.put("date",message.getDate());
        values.put("photoRoad",message.getPhotoRoad());
        Connect.db.insert("MessageTable",null,values);

    }
    /*得到数据库的聊天记录
    *cursor 传入得到的cursor对象，返回处理后的hashmap
     */
    public HashMap<String, List<Message>> getMessageHashMap(){
        Cursor cursor =Connect.db.query("MessageTable",null,null,null,null,null,null);
        HashMap<String,List<Message>> map=new HashMap<>();
        List<Message> messageList=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
               Message message=new Message();
               message.setFrom(cursor.getString(cursor.getColumnIndex("fromUser")));
               message.setTo(cursor.getString(cursor.getColumnIndex("toUser")));
               message.setBody(cursor.getString(cursor.getColumnIndex("body")));
               message.setType(cursor.getInt(cursor.getColumnIndex("type")));
               String photoRoad=cursor.getString(cursor.getColumnIndex("photoRoad"));
               message.setPhotoRoad(photoRoad);
               message.setDate(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
               message.setPhoto(BitmapFactory.decodeFile(photoRoad));
               message.setMessageType(cursor.getString(cursor.getColumnIndex("messageType")));
               //message.setDate(cursor.getShort(cursor.getColumnIndex("date")));
               //添加进整体聊天记录
               messageList.add(message);
            }while (cursor.moveToNext());
        }
        cursor.close();
        //TODO 分类排序
        while(messageList.size()>0){
            int size=messageList.size();
            int remove[]=new int[size];
            int mType=messageList.get(0).getType();
            String mFrom=messageList.get(0).getFrom();
            String mTo=messageList.get(0).getTo();
            String chatedName;
            String mMessageType=messageList.get(0).getMessageType();
            if(mType==1){ //这条信息是自己发送的消息       from--self  to--other
                chatedName=mTo;
            }else{     //这条信息是别人发送的消息          from--other to self
                chatedName=mFrom;
            }
            List<Message> messageList1=new ArrayList<>();
            for (int i=0;i<size;i++){
                if(((messageList.get(i).getFrom().equals(mFrom))&&(messageList.get(i).getTo().equals(mTo)))||((messageList.get(i).getTo().equals(mFrom))&&(messageList.get(i).getFrom().equals(mTo)))){
                    remove[i]=1;
                    messageList1.add(messageList.get(i));
                }
            }
            //TODO messagelist1 需要排序
            map.put(chatedName,messageList1); //设置聊天记录
            for(int j=size-1;j>=0;j--){
                if(remove[j]==1)
                    messageList.remove(j);
            }
        }
        return map;
    }
    public List<GroupInfo> getGroupInfoList(){
        Cursor cursor =Connect.db.query("FriendInfoTable",null,null,null,null,null,null);
        List<GroupInfo> list=new ArrayList<>();
        List<FriendInfo> friendInfoList=new ArrayList<>();       //所有的好友信息列表
        if (cursor.moveToFirst()) {
            do{
                FriendInfo friendInfo=new FriendInfo();
                friendInfo.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                friendInfo.setNicName(cursor.getString(cursor.getColumnIndex("nicName")));
                String headBtRoad=cursor.getString(cursor.getColumnIndex("headBtRoad"));
                friendInfo.setHeadBt(BitmapFactory.decodeFile(headBtRoad));
                Log.d("", "getGroupInfoList: 设置好友头像成功"+headBtRoad);
                friendInfo.setGroupName(cursor.getString(cursor.getColumnIndex("groupName")));
                friendInfo.setChated(cursor.getInt(cursor.getColumnIndex("chated")));
                if(cursor.getInt(cursor.getColumnIndex("chated"))==1){
                    Connect.friendInfoList.add(friendInfo);           //加入会话列表
                    Connect.messageMap.put(friendInfo.getUserName(),getMessageListByName(friendInfo.getUserName()));//同时添加进对应的聊天信息
                }
                friendInfoList.add(friendInfo);
            }while(cursor.moveToNext());
        }
        //对好友信息列表处理 分为各组 每次循环设第一个好友的组为新组名，每加一个然后删除这个
        while(friendInfoList.size()>0){
            GroupInfo groupInfo=new GroupInfo();
            List<FriendInfo> list1=new ArrayList<>();  //每个组的好友列表
            int size=friendInfoList.size();
            int remove[]=new int[size];

            int j=0;
            Log.d("", "最外部组添加循环:   "+"  ");
            Log.d("", "getGroupInfoList:第 "+j+"次组添加循环时friendlist剩余"+friendInfoList.size());
            groupInfo.setGroupName(friendInfoList.get(0).getGroupName());
            for(int i=0;i<size;i++){                                          //TODO 起初写的是i<friendinfolist.size() 内部每一次都会改变friend info list大小，所以每一次都检测不完
                int x=i;
                Log.d("从数据库得到好友消息", "getGroupInfoList:  "+i+" "+groupInfo.getGroupName());
                Log.d("", "getGroupInfoList:   "+i+"  "+friendInfoList.get(i).toString());
                if(friendInfoList.get(i).getGroupName().equals(groupInfo.getGroupName())){
                    remove[i]=1;         //标记要移除
                    Log.d("进入判断内部", "getGroupInfoList:   "+i+"  "+friendInfoList.get(i).toString());
                    list1.add(friendInfoList.get(i));
                    //friendInfoList.remove(i);//删除
                }
            }
            for(int k=size-1;k>=0;k--){            //从大到小移除，否则出错
                if(remove[k]==1)
                    friendInfoList.remove(k);
            }
            groupInfo.setFriendInfoList(list1);
            list.add(groupInfo);
            j=j+1;
        }
        return list;
    }
    /*
    * 通过userName得到聊天信息列表
    * @param user:String
    * */
    public List<Message> getMessageListByName(String user){
        List<Message> messageList=new ArrayList<>();
        //Cursor cursor =Connect.db.query("MessageTable",null,null,null,null,null,null);
        Cursor cursor=Connect.db.query("MessageTable",null,"toUser=? and type='1' or fromUser=?  and type='2'",new String[]{user,user},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Message message=new Message();
                message.setFrom(cursor.getString(cursor.getColumnIndex("fromUser")));
                message.setTo(cursor.getString(cursor.getColumnIndex("toUser")));
                message.setBody(cursor.getString(cursor.getColumnIndex("body")));
                message.setType(cursor.getInt(cursor.getColumnIndex("type")));
                String photoRoad=cursor.getString(cursor.getColumnIndex("photoRoad"));
                message.setPhoto(BitmapFactory.decodeFile(photoRoad));
                message.setDate(cursor.getLong(cursor.getColumnIndex("date")));
                if(photoRoad.length()>0){
                    message.setPhotoRoad(photoRoad);
                    message.setPhoto(BitmapFactory.decodeFile(photoRoad));
                }
                message.setMessageType(cursor.getString(cursor.getColumnIndex("messageType")));
                //message.setDate(cursor.getShort(cursor.getColumnIndex("date")));
                //添加进整体聊天记录
                messageList.add(message);
            }while (cursor.moveToNext());
        }
        cursor.close();
       /* Cursor cursor1=Connect.db.query("MessageTable",null,"fromUser=? and type=?",new String[]{user,"2"},null,null,null);
        if(cursor1.moveToFirst()){
            do{
                Message message=new Message();
                message.setFrom(cursor1.getString(cursor1.getColumnIndex("fromUser")));
                message.setTo(cursor1.getString(cursor1.getColumnIndex("toUser")));
                message.setBody(cursor1.getString(cursor1.getColumnIndex("body")));
                message.setType(cursor1.getInt(cursor1.getColumnIndex("type")));
                String photoRoad=cursor1.getString(cursor1.getColumnIndex("photoRoad"));
                if(photoRoad.length()>0){
                    message.setPhotoRoad(photoRoad);
                    message.setPhoto(BitmapFactory.decodeFile(photoRoad));
                }
                message.setDate(Long.valueOf(cursor.getString(cursor.getColumnIndex("date"))));
                message.setMessageType(cursor1.getString(cursor1.getColumnIndex("messageType")));
                //message.setDate(cursor.getShort(cursor.getColumnIndex("date")));
                //添加进整体聊天记录
                messageList.add(message);
            }while (cursor1.moveToNext()&&cursor1!=null);
        }
        cursor1.close();*/
        //TODO 排序

        return messageList;
    }
    /*
    *改变数据库FriendInfoTable里chated列的状态
    * param user:String
    * param state:int
    * void:return
    * */
    public  void changeChatState(String user,int state){
        ContentValues values=new ContentValues();
        values.put("chated",state);
        Connect.db.update("FriendInfoTable",values,"userName=?",new String[]{user});
    }
    /*
    *s删除对应好友的聊天记录
    *@param userName
     */
    public void deleteMessage(String userName){
        Connect.db.delete("MessageTable","fromUser=? and type='2' or toUser=? and type='1'",new String[]{userName,userName});
    }
    /*
    * 删除数据库对应的好友条目
    *@param userName
     */
    public void deleteFriendInfo(String userName){
        Connect.db.delete("FriendInfoTable","userName=?",new String[]{userName});
        deleteMessage(userName);
    }
}
