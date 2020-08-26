package com.example.cm.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.AddFriend.AddFriendItem;
import com.example.cm.friend.Cn2Spell;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.Message;
import com.example.cm.friend.fragment.FriendFragment;
import com.example.cm.friend.fragment.SessionFragment;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.SmackUserInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.theme.ThemeColor;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
public class MessageManager {
    private  static SmackUserInfo smackUserInfo;
    private static boolean haveNewMessage;
    private static HashMap<String, List<Message>> messageMap;                            //会话列表String对应好友，List<Message>为对应此String好友聊天信息列表
    private static HashMap<String,Integer> unReadMessageCount;         //未读消息计数
    private  static List<FriendInfo>   friendInfoList;                                                 //会话列表好友名列表
    private  static List<FriendInfo>   contantFriendInfoList;                                                 //联系人列表好友名列表
    private static List<AddFriendItem> addFriendItemList;                         //添加好友详情列表
    private static SharedPreferences sharedPreferences;                         //存储上次登录用户信息
    private static SharedPreferences.Editor editor;
    private   static boolean addFriendItemListChanged;
    private static DataBase dataBaseHelp;
    private static SQLiteDatabase db;
    private static Bitmap bitmap=null;
    private static boolean contantListChanged;
    private static NotificationManager mNotificationManager;
    private final static String channelID="message";
    private final static String channelDescription="message";
    private static NotificationChannel channel;
    public final static String BASEURL="http://39.105.75.60/image-path/";         //远程图片资源路径公共前缀
    public static void initAllList(){//初始化所有的列表
        haveNewMessage=false;
        contantListChanged=false;
        ThemeColor.changed=true;
        messageMap=new HashMap<>();
        unReadMessageCount=new HashMap<>();
        friendInfoList=new ArrayList<>();
        contantFriendInfoList=new ArrayList<>();
        addFriendItemList=new ArrayList<>();
        smackUserInfo=new SmackUserInfo();
        addFriendItemListChanged=false;
        sharedPreferences= MainActivity.getInstance().getSharedPreferences("LoginedInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        mNotificationManager = (NotificationManager) MainActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel=new NotificationChannel(channelID,channelDescription,NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            mNotificationManager.createNotificationChannel(channel);
        }


        /*Notification.Builder builder = new Notification.Builder(MainActivity.getActivity());
        Intent intent = new Intent(MainActivity.getActivity(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getActivity(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.cm);
        builder.setLargeIcon(BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.drawable.cm));
        builder.setAutoCancel(true);
        builder.setContentTitle("悬挂式通知");
        builder.setFullScreenIntent(pendingIntent, true);
        //mNotificationManager.notify(2, builder.build());*/



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

    public static void setUnReadMessageCount(HashMap<String, Integer> unReadMessageCount) {
        MessageManager.unReadMessageCount = unReadMessageCount;
    }

    public static HashMap<String, Integer> getUnReadMessageCount() {
        return unReadMessageCount;
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
        dataBaseHelp=new DataBase(MainActivity.getInstance(),"DataBaseOf"+userName+".db",null,2);
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
            friendInfo.setNoteName(userName1);
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
                try {
                    Thread.sleep(1000);
                    Log.d("", "initFriend: 循环里等待");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                String note=entry.getName();
                VCard vCard=VCardManager.getUserVcard(user);
                friendInfo.setUserName(user);
                Log.e("", "initFriend: 备注："+note );
                if(note!=null) {
                    friendInfo.setNoteName(note);
                }else{
                    friendInfo.setNoteName(user);
                }
                if(vCard.getNickName()==null){
                    friendInfo.setNicName(user);
                }
                friendInfo.setNicName(vCard.getNickName());
                String pinyin = Cn2Spell.getPinYin(friendInfo.getNoteName()); // 根据姓名获取拼音
                String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
                if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                    firstLetter = "#";
                }
                friendInfo.setPinyin(pinyin);
                friendInfo.setFirstLetter(firstLetter);
                String sex = vCard.getField("gender");
                if (sex == null) {
                    sex = "secrecy";
                }
                friendInfo.setSex(sex);
                String email = vCard.getField("email");
                if (email == null) {
                    email = "";
                }
                friendInfo.setEmail(email);
                Log.e("", "initFriend: gender :" + sex + "   email:" + email);
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
        MessageManager.setContantListChanged(true);
        RosterEntry entry=Connect.getRoster().getEntry("administrator"+"@"+Connect.SERVERNAME);
        if((entry==null)||(!entry.getType().equals(RosterPacket.ItemType.both))){
            try {
                Connect.getRoster().createEntry("administrator"+"@"+Connect.SERVERNAME,"administrator",new String[]{"Friends"});
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
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
                    Log.d("离线消息测试", "getOfflineMessage: "+messages);
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
                Intent intent = new Intent(MainActivity.getInstance(), ChatActivity.class);
                //JSONObject object = new JSONObject(message.getBody());
                //String type = object.getString("type");
                //String data = object.getString("data");

                com.example.cm.friend.chat.Message message1=new com.example.cm.friend.chat.Message();
                String userFrom=message.getFrom().split("@")[0];
                Log.d("TAG接收************"+userFrom, message.getBody());
                JSONObject jsonObject=new JSONObject(message.getBody());
                message1.setMessageType(jsonObject.getString("type"));
                Long time=jsonObject.getLong("date");
                if(jsonObject.getString("type").equals("text")) {
                    message1.setBody(jsonObject.getString("data"));
                    message1.setPhotoRoad("");
                    message1.setPhoto(null);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            message1.setBody("[图片]");
                            //将接收到的字符串解析成bitmap
                            //String encodeImageStr=jsonObject.getString("data");
                            //Bitmap bitmap= AlbumUtil.byte2Bitmap(AlbumUtil.encodedStr2byte(encodeImageStr));
                            try {
                                bitmap=AlbumUtil.getBitmapByUrl(jsonObject.getString("data"),time);
                                Thread.sleep(500);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.e("图片加载成功", "run: " );
                            String savePhotoRoad=AlbumUtil.saveMessageBitmap(bitmap);
                            Log.d("保存接收到的图片路径", "processMessage: "+savePhotoRoad);
                            message1.setPhotoRoad(savePhotoRoad);
                            message1.setPhoto(bitmap);
                        }
                    }).start();
                }
                message1.setFrom(userFrom);
                message1.setTo(message.getTo().split("@")[0]);
                //message1.setDate((Date) jsonObject.get("date"));
                message1.setDate(time);
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
                        intent.putExtra("userName",userFrom);
                        intent.putExtra("noteName",MessageManager.getFriendInfoList().get(i).getNoteName());
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
                            intent.putExtra("userName",userFrom);
                            intent.putExtra("noteName",MessageManager.getContantFriendInfoList().get(j).getNoteName());
                            break;
                        }
                    }


                    Log.d("一 二", "processMessage: "+message.getFrom());
                    MessageManager.getFriendInfoList().add(friendInfo);
                    List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                    messageList.add(message1);
                    MessageManager.getMessageMap().put(userFrom,messageList);        //map新加一个
                }
                if(ChatActivity.currentFriendName==null) {
                    if (unReadMessageCount.containsKey(userFrom)) {
                        unReadMessageCount.put(userFrom, unReadMessageCount.get(userFrom) + 1);
                    } else {//未读列表没有此键
                        unReadMessageCount.put(userFrom, 1);
                    }
                }else{
                    if(ChatActivity.currentFriendName.equalsIgnoreCase(userFrom)){
                        android.os.Message newMessage= android.os.Message.obtain();
                        newMessage.what=3;
                        newMessage.setTarget(ChatActivity.handler);
                        newMessage.sendToTarget();
                    }else{
                        if (unReadMessageCount.containsKey(userFrom)) {
                            unReadMessageCount.put(userFrom, unReadMessageCount.get(userFrom) + 1);
                        } else {//未读列表没有此键
                            unReadMessageCount.put(userFrom, 1);
                        }
                    }
                }
                playRingTone();//提示音
                playVibrate();//震动
                //通知栏提示收到消息
                NotificationCompat.Builder builder=new NotificationCompat.Builder(MainActivity.getInstance(),channelID);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getInstance(),0,intent,0);
                builder.setContentIntent(pendingIntent);
                builder.setContentTitle(message1.getFrom());
                builder.setContentText(message1.getBody());
                builder.setAutoCancel(true)
                        .setSmallIcon(R.drawable.cm)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setFullScreenIntent(pendingIntent,true)
                        .setTicker(message1.getBody())
                        .build();
                mNotificationManager.notify(2,builder.build());
                Log.e("", "mergeMessage: 通知栏显示之后" );
                android.os.Message message2= android.os.Message.obtain();
                message2.what=0;
                message2.setTarget(MainActivity.myHandler);
                message2.sendToTarget();
                android.os.Message message3= android.os.Message.obtain();
                message3.what=1;
                message3.setTarget(FriendFragment.handler);
                message3.sendToTarget();
                android.os.Message message4= android.os.Message.obtain();
                message4.what=2;
                message4.setTarget(SessionFragment.handler);
                message4.sendToTarget();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    * 联系人列表是否包含此好友
    * @param userName
    * @return friendInfo
    * */
    public  static FriendInfo getFriendInfoFromContantList(String userName){
        FriendInfo friendInfo=null;
        for(int i=0;i<contantFriendInfoList.size();i++){
            if(contantFriendInfoList.get(i).getUserName().equalsIgnoreCase(userName)){
                friendInfo=contantFriendInfoList.get(i);
                break;
            }
        }
        return friendInfo;
    }
    /*
    *对会话列表进行排序，按时间最新显示
    * @param messageMap
    */
    public  static HashMap<String,List<Message>> sortMessageMap(HashMap<String,List<Message>> map){
        //從HashMap中恢復entry集合，得到全部的鍵值對集合
        Set<Map.Entry<String,List<Message>>> entey = map.entrySet();
        //將Set集合轉為List集合，為了實用工具類的排序方法
        List<Map.Entry<String,List<Message>>> list = new ArrayList<Map.Entry<String,List<Message>>>(entey);
        //使用Collections工具類對list進行排序
        Collections.sort(list, new Comparator<Map.Entry<String, List<Message>>>() {
            @Override
            public int compare(Map.Entry<String, List<Message>> o1, Map.Entry<String, List<Message>> o2) {
                //倒敘排列
                List<Message> o1List=map.get(o1.getKey());
                List<Message> o2List=map.get(o2.getKey());
                return (int) (o2List.get(o1List.size()-1).getDate()-o1List.get(o2List.size()-1).getDate());
            }
        });
        //創建一個HashMap的子類LinkedHashMap集合
        LinkedHashMap<String,List<Message>> linkedHashMap = new LinkedHashMap<String, List<Message>>();
        //將list中的數據存入LinkedHashMap中
        for(Map.Entry<String,List<Message>> entry:list){
            linkedHashMap.put(entry.getKey(),entry.getValue());
        }
        return linkedHashMap;//赋值给map
    }
    /*
    *获得某人的未读消息数目
    * @param userName
    * @return count
    * */
    public static int getUnReadCountByName(String userName){
        int count=0;
        if(unReadMessageCount.containsKey(userName)){
            count=unReadMessageCount.get(userName);
        }else{
            count=0;
        }
        return count;
    }
    /*
    * 获得未读消息数目
    * @return count
    * */
    public static int getAllUnReadMessageCount(){
        int count=0;
        if(!unReadMessageCount.isEmpty()) {
            Set<String> set = unReadMessageCount.keySet();
            for (String s : set
            ) {
                count = count + getUnReadCountByName(s);
            }
        }
        return count;
    }
    /*
    *
    * 未读消息清零
    * @param userName
    * */
    public static void clearUnReadByName(String userName){
        if(unReadMessageCount.containsKey(userName)){
            unReadMessageCount.put(userName,0);
        }
    }
    /**
     * 播放通知声音
     */
    public static void playRingTone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(MainActivity.getInstance(), uri);
        rt.play();
    }

    /**
     * 手机震动一下
     */
    public static void playVibrate() {
        Vibrator vibrator = (Vibrator) MainActivity.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        long[] vibrationPattern = new long[]{0, 180, 80, 120};
        // 第一个参数为开关开关的时间，第二个参数是重复次数，振动需要添加权限
        vibrator.vibrate(vibrationPattern, -1);
    }

}
