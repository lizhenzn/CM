package com.example.cm.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.example.cm.MainActivity;
import com.example.cm.friend.AddFriendItem;
import com.example.cm.friend.Cn2Spell;
import com.example.cm.friend.ContantActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.SmackUserInfo;
import com.example.cm.myInfo.VCardManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
public class MessageManager {
    private  static SmackUserInfo smackUserInfo;
    private static boolean haveNewMessage;
    private static HashMap<String, List<Message>> messageMap;                            //会话列表String对应好友，List<Message>为对应此String好友聊天信息列表
    private  static List<FriendInfo>   friendInfoList;                                                 //会话列表好友名列表
    private  static List<FriendInfo>   contantFriendInfoList;                                                 //联系人列表好友名列表
    private static List<AddFriendItem> addFriendItemList;                         //添加好友详情列表
    private static SharedPreferences sharedPreferences;                         //存储上次登录用户信息
    private static SharedPreferences.Editor editor;
    private   static boolean addFriendItemListChanged;
    private static DataBase dataBaseHelp;
    private static SQLiteDatabase db;
    private static boolean contantListChanged;
    public static void initAllList(){//初始化所有的列表
        haveNewMessage=false;
        messageMap=new HashMap<>();
        friendInfoList=new ArrayList<>();
        contantFriendInfoList=new ArrayList<>();
        addFriendItemList=new ArrayList<>();
        smackUserInfo=new SmackUserInfo();
        addFriendItemListChanged=false;
        sharedPreferences= MainActivity.getInstance().getSharedPreferences("LoginedInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();


    }

    public static List<AddFriendItem> getAddFriendItemList() {
        return addFriendItemList;
    }

    public static void setAddFriendItemList(List<AddFriendItem> addFriendItemList) {
        MessageManager.addFriendItemList = addFriendItemList;
    }

    public static boolean isAddFriendItemListChanged() {
        return addFriendItemListChanged;
    }

    public static void setAddFriendItemListChanged(boolean addFriendItemListChanged) {
        MessageManager.addFriendItemListChanged = addFriendItemListChanged;
    }


    public static List<FriendInfo> getContantFriendInfoList() {
        return contantFriendInfoList;
    }

    public static void setContantFriendInfoList(List<FriendInfo> contantFriendInfoList) {
        MessageManager.contantFriendInfoList = contantFriendInfoList;
    }

    public static SmackUserInfo getSmackUserInfo() {
        return smackUserInfo;
    }

    public static void setSmackUserInfo(SmackUserInfo smackUserInfo) {
        MessageManager.smackUserInfo = smackUserInfo;
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static HashMap<String, List<Message>> getMessageMap() {
        return messageMap;
    }

    public static void setMessageMap(HashMap<String, List<Message>> messageMap) {
        MessageManager.messageMap = messageMap;
    }

    public static boolean isHaveNewMessage() {
        return haveNewMessage;
    }

    public static void setHaveNewMessage(boolean haveNewMessage) {
        MessageManager.haveNewMessage = haveNewMessage;
    }

    public static List<FriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public static void setFriendInfoList(List<FriendInfo> friendInfoList) {
        MessageManager.friendInfoList = friendInfoList;
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static void setDataBaseHelp(String userName) {
        dataBaseHelp=new DataBase(MainActivity.getInstance(),"DataBaseOf"+userName+".db",null,1,userName);
        db=dataBaseHelp.getWritableDatabase();
    }

    public static DataBase getDataBaseHelp() {
        return dataBaseHelp;
    }

    public static boolean isContantListChanged() {
        return contantListChanged;
    }

    public static void setContantListChanged(boolean contantListChanged) {
        MessageManager.contantListChanged = contantListChanged;
    }

    public static void clearAllList(){
        addFriendItemList.clear();
        friendInfoList.clear();
        contantFriendInfoList.clear();
        messageMap.clear();
    }
    //添加好友进数据库和联系人列表
    public static  boolean addFriend(String userName){
        try {
            //TODO 数据库和好友列表需要更新
            String userName1=userName.split("@")[0];
            FriendInfo friendInfo=new FriendInfo();
            friendInfo.setUserName(userName1);
            friendInfo.setNicName(userName1);
            friendInfo.setPinyin(Cn2Spell.getPinYin(userName1));
            friendInfo.setFirstLetter(Cn2Spell.getPinYinFirstLetter(userName1));
            friendInfo.setSex(VCardManager.getUserVcard(userName.split("@")[0]).getField("gender"));
            friendInfo.setEmail(VCardManager.getUserVcard(userName.split("@")[0]).getField("email"));
            Bitmap bitmap=VCardManager.getUserImage(userName1);
            String friendBtRoad=AlbumUtil.saveHeadBitmap(userName1,bitmap);
            friendInfo.setHeadBt(bitmap);
            friendInfo.setHeadBtRoad(friendBtRoad);
            friendInfo.setChated(0);
            contantFriendInfoList.add(friendInfo);//联系人列表添加
            contantListChanged=true;
            getDataBaseHelp().addFriendInfo(friendInfo);   //数据库添加好友信息
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    //删除好友 此处userName没有@后的字符串
    public  static  boolean deleteFriend(String userName){
        try {
            //TODO           数据库和好友列表需要更新
            dataBaseHelp.deleteFriendInfo(userName.split("@")[0]);   //数据库删除好友条目
            Log.d("", "deleteFriend: 数据库删除好友条目成功");
            for(int i=0;i<contantFriendInfoList.size();i++){
                if(contantFriendInfoList.get(i).getUserName().equals(userName.split("@")[0])){
                    contantFriendInfoList.remove(i);
                    contantListChanged=true;
                }
            }
            for(int i=0;i<friendInfoList.size();i++){         //删除对应的会话列表条目
                if(friendInfoList.get(i).getUserName().equals(userName.split("@")[0])){
                    friendInfoList.remove(i);
                    messageMap.remove(userName.split("@")[0]);
                    Log.d("", "deleteFriend: 删除好友聊天成功");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    //从服务器获取好友信息
    public static void initFriend(){

        Log.d("登录用户",Connect.getXMPPTCPConnection().getUser().toString());

          /*roster=Roster.getInstanceFor(xmpptcpConnection);
          roster.setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理*/
        while(!Connect.getRoster().isLoaded()){
            try {
                Connect.getRoster().reload();
                Log.d("重连Roster",Connect.getXMPPTCPConnection().getUser().toString());

            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",Connect.getXMPPTCPConnection().getUser().toString());

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",Connect.getXMPPTCPConnection().getUser().toString());
            }
        }
        Log.d("重连成功",Connect.getXMPPTCPConnection().getUser().toString());

        Log.e("总好友数", String.valueOf(Connect.getRoster().getEntryCount()));
        Collection<RosterEntry> rosterEntries=Connect.getRoster().getEntries();
        for(RosterEntry entry:rosterEntries){
            Log.d("", "initFriend: roster类型："+entry.getType() );
            if(entry.getType().equals(RosterPacket.ItemType.both)) {//双方互相订阅
                Log.e("", "initFriend: 双方互相订阅" );
                FriendInfo friendInfo = new FriendInfo();
                String user = entry.getUser().split("@")[0];
                friendInfo.setUserName(user);
                friendInfo.setNicName(user);
                String pinyin = Cn2Spell.getPinYin(user); // 根据姓名获取拼音
                String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
                if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                    firstLetter = "#";
                }
                friendInfo.setPinyin(pinyin);
                friendInfo.setFirstLetter(firstLetter);
                String sex = VCardManager.getUserVcard(user).getField("gender");
                if (sex == null) {
                    sex = "保密";
                }else if(sex.equals("male")){
                    sex="男";
                }else if(sex.equals("female")){
                    sex="女";
                }else{
                    sex="保密";
                }
                friendInfo.setSex(sex);
                String email = VCardManager.getUserVcard(user).getField("email");
                if (email == null) {
                    email = "";
                }
                friendInfo.setEmail(email);
                Log.e("", "initFriend: sex :" + sex + "   email:" + email);
                Bitmap bitmap = VCardManager.getUserImage(user);
                friendInfo.setHeadBt(bitmap);  //设置头像
                String friendHeadBitmapRoad = AlbumUtil.saveHeadBitmap(user, bitmap);  //保存好友头像
                friendInfo.setHeadBtRoad(friendHeadBitmapRoad);
                Log.d("好友头像保存路径", "initFriend: " + friendHeadBitmapRoad);
                //添加返回一个values对象，须要db添加提交
                MessageManager.getDataBaseHelp().addFriendInfo(friendInfo);
                MessageManager.getContantFriendInfoList().add(friendInfo);
                Log.d("", "initFriend:添加进通讯录   " + MessageManager.getContantFriendInfoList().size());
            }
        }
        Connect.getRoster().addRosterListener(new RosterListener() {                                                //TODO 花名册监听
            @Override
            public void entriesAdded(Collection<String> collection) {
                Log.d("添加监听", "entriesAdded: "+collection.toString());
            }

            @Override
            public void entriesUpdated(Collection<String> collection) {
                Log.d("添加监听", "entriesUpdated: "+collection.toString());
            }

            @Override
            public void entriesDeleted(Collection<String> collection) {
                Log.d("添加监听", "entriesDeleted: "+collection.toString());
            }

            @Override
            public void presenceChanged(Presence presence) {
                Log.d("添加监听", "presenceChanged 状态改变: "+presence.toString());
            }
        });


    }
    //获取离线消息
    //猜想 Message 继承Packet 在Message处理之前先转换成Staza看是不是添加好友删除好友的包
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<com.example.cm.friend.chat.Message> getOfflineMessage(){
        List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
        OfflineMessageManager offlineMessageManager=new OfflineMessageManager(Connect.getXMPPTCPConnection());
        try {
            List<org.jivesoftware.smack.packet.Message> messages=offlineMessageManager.getMessages();
            Log.d("离线消息测试", "getOfflineMessage: "+messages);

            if(messages.size()>0){
                for(int i=0;i<messages.size();i++){
                    Log.d("离线消息", "getOfflineMessage: ");
                    Log.d("离线消息", "getOfflineMessage: ");
                    Log.d("离线消息", "getOfflineMessage: ");
                    Log.d("离线消息测试", "getOfflineMessage: "+messages);
                    Log.d("离线消息", "getOfflineMessage: ");
                    Log.d("离线消息", "getOfflineMessage: ");
                    Log.d("离线消息", "getOfflineMessage: ");
                }
            }
            for(int i=0;i<messages.size();i++){
                org.jivesoftware.smack.packet.Message message=messages.get(i);
                mergeMessage(message);
                //TODO 处理每一条离线信息
            }
            offlineMessageManager.deleteMessages();//删除离线消息
            Log.d("", "getOfflineMessage: 删除离线消息");
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return messageList;
    }
    //将离线消息合并到聊天信息中
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void mergeMessage(org.jivesoftware.smack.packet.Message message){
        if (!TextUtils.isEmpty(message.getBody())) {
            try {
                //JSONObject object = new JSONObject(message.getBody());
                //String type = object.getString("type");
                //String data = object.getString("data");

                com.example.cm.friend.chat.Message message1=new com.example.cm.friend.chat.Message();
                String userFrom=message.getFrom().split("@")[0];
                Log.d("TAG接收************"+userFrom, message.getBody());
                JSONObject jsonObject=new JSONObject(message.getBody());
                message1.setMessageType(jsonObject.getString("type"));
                if(jsonObject.getString("type").equals("text")) {
                    message1.setBody(jsonObject.getString("data"));
                    message1.setPhotoRoad("");
                    message1.setPhoto(null);
                }else{
                    message1.setBody("[图片]");
                    //将接收到的字符串解析成bitmap
                    String encodeImageStr=jsonObject.getString("data");
                    Bitmap bitmap= AlbumUtil.byte2Bitmap(AlbumUtil.encodedStr2byte(encodeImageStr));
                    String savePhotoRoad=AlbumUtil.saveHeadBitmap(null,bitmap);
                    Log.d("保存接收到的图片路径", "processMessage: "+savePhotoRoad);
                    message1.setPhotoRoad(savePhotoRoad);
                    message1.setPhoto(bitmap);

                }
                message1.setFrom(userFrom);
                message1.setTo(message.getTo().split("@")[0]);
                //message1.setDate((Date) jsonObject.get("date"));
                message1.setDate(jsonObject.getLong("date"));
                message1.setType(2);
                MessageManager.getDataBaseHelp().addMessage(message1);                //添加进聊天数据库
                MessageManager.setHaveNewMessage(true);
                //ChatActivity.chatAdapter.notifyDataSetInvalidated();
                //添加进对应的聊天信息列表
                boolean contain=false;
                for(int i=0;i<MessageManager.getFriendInfoList().size();i++){
                    if(MessageManager.getFriendInfoList().get(i).getUserName().equals(userFrom)){     //会话列表包含此好友
                        MessageManager.getMessageMap().get(MessageManager.getFriendInfoList().get(i).getUserName()).add(message1);  //添加进聊天信息
                        contain=true;
                        Log.d("接收到信息 包含", "processMessage: "+message.getFrom());
                        break;
                    }
                }
                Log.d("分割", "processMessage: "+message.getFrom().split("/")[0]);
                if(!contain){  //会话列表不包含此好友
                    MessageManager.getDataBaseHelp().changeChatState(userFrom,1);           //改变数据库中聊天状态
                    FriendInfo friendInfo=null;
                    for(int j=0;j<MessageManager.getContantFriendInfoList().size();j++){
                        if(MessageManager.getContantFriendInfoList().get(j).getUserName().equals(userFrom)){
                            MessageManager.getContantFriendInfoList().get(j).setChated(1);//设置为正在聊天标记
                            friendInfo= MessageManager.getContantFriendInfoList().get(j);
                            break;
                        }
                    }



                    Log.d("一 二", "processMessage: "+message.getFrom());
                    MessageManager.getFriendInfoList().add(friendInfo);
                    List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                    messageList.add(message1);
                    MessageManager.getMessageMap().put(userFrom,messageList);        //map新加一个
                }

                //message.setFrom(message.getFrom().split("/")[0]);
                //message.setBody(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
