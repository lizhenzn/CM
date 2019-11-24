package com.example.cm.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ListView;

import com.example.cm.friend.Cn2Spell;
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
            +"sex varchar(12) Not Null default '保密',"
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
        values.put("sex",friendInfo.getSex());
        values.put("email",friendInfo.getEmail());
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
    public List<FriendInfo> getContantFriendInfoList(){
        Cursor cursor =Connect.db.query("FriendInfoTable",null,null,null,null,null,null);
        List<FriendInfo> friendInfoList=new ArrayList<>();       //所有的好友信息列表
        if (cursor.moveToFirst()) {
            do{
                FriendInfo friendInfo=new FriendInfo();
                String userName=cursor.getString(cursor.getColumnIndex("userName"));
                String pinyin = Cn2Spell.getPinYin(userName); // 根据姓名获取拼音
                String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
                if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                    firstLetter = "#";
                }
                friendInfo.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                friendInfo.setNicName(cursor.getString(cursor.getColumnIndex("nicName")));
                friendInfo.setPinyin(pinyin);
                friendInfo.setFirstLetter(firstLetter);
                String headBtRoad=cursor.getString(cursor.getColumnIndex("headBtRoad"));
                friendInfo.setHeadBt(BitmapFactory.decodeFile(headBtRoad));
                Log.d("", "getGroupInfoList: 设置好友头像成功"+headBtRoad);
                friendInfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                friendInfo.setChated(cursor.getInt(cursor.getColumnIndex("chated")));
                if(cursor.getInt(cursor.getColumnIndex("chated"))==1){
                    Connect.friendInfoList.add(friendInfo);           //加入会话列表
                    Connect.messageMap.put(friendInfo.getUserName(),getMessageListByName(friendInfo.getUserName()));//同时添加进对应的聊天信息
                }
                friendInfoList.add(friendInfo);
            }while(cursor.moveToNext());
        }

        return friendInfoList;
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
