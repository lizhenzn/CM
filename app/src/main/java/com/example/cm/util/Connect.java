package com.example.cm.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewManager;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.AddFriendItem;
import com.example.cm.friend.Cn2Spell;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.SmackUserInfo;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Connect {
    public static final String SERVERNAME="iz92swk4q6t1a7z";       //服务器名	iz92swk4q6t1a7z
    public static final String IP="114.55.255.210";                //服务器IP地址
    private final Context context;
    public static DataBase dataBaseHelp;
    public static SQLiteDatabase db;
    public static List<GroupInfo> groupInfoList;
    public  static SmackUserInfo  smackUserInfo;
    public static boolean haveNewMessage;
    public static HashMap<String,List<com.example.cm.friend.chat.Message>>messageMap;                            //会话列表String对应好友，List<Message>为对应此String好友聊天信息列表
    public  static List<FriendInfo>   friendInfoList;                                                 //会话列表好友名列表
    public  static List<FriendInfo>   contantFriendInfoList;                                                 //联系人列表好友名列表

    public static List<AddFriendItem> addFriendItemList;                         //添加好友详情列表
    public  static Roster roster;
    public static SharedPreferences sharedPreferences;                         //存储上次登录用户信息
    public static SharedPreferences.Editor editor;
    public  static boolean addFriendItemListChanged;
    public static  boolean groupInfoListChanged;
    public static boolean isLogined;  //登陆是否
    public  static boolean isLinked;  //有无网络
    public Connect(Context context){
        this.context=context;
        init();
    }
    public static XMPPTCPConnection xmpptcpConnection=null;
    public static boolean getXMPPTCPConnection(){
                if(xmpptcpConnection==null) {
                    Log.d("XMPPTCP new 之前", ""+xmpptcpConnection);

                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setServiceName(SERVERNAME)
                            .setHost(IP)
                            .setPort(5222)
                            .setSendPresence(false)         //设置false可以接受离线信息
                            .setDebuggerEnabled(true)
                            //.setConnectTimeout(10000)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();
                    xmpptcpConnection = new XMPPTCPConnection(config);
                    Log.d("XMPPTCP new 之后", ""+xmpptcpConnection);

                    try {
                        Log.d("", "getXMPPTCPConnection: connect之前"+xmpptcpConnection);
                        xmpptcpConnection.connect();
                        Log.d("", "getXMPPTCPConnection: connect之后"+xmpptcpConnection);
                        if(xmpptcpConnection.isConnected()){
                           Log.d("*********getXMPPTCP", "getXMPPTCPConnection: connect成功");
                        return true;
                        }
                        else{
                            Log.d("*********getXMPPTCP", "getXMPPTCPConnection: connect失败");
                        return false;
                        }
                    } catch (SmackException e) {
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
        return  true;
    }
    public static boolean login(String userName,String passwd,Context context){
        try {
            if(xmpptcpConnection==null){
                getXMPPTCPConnection();
            }
            if(xmpptcpConnection.isConnected()){//连接说明登陆过

                if(MainActivity.isBinded) {
                    MainActivity.unBindPacket();
                }
            }else{
                if(getXMPPTCPConnection()){
                    Log.d("", "login:连接服务器成功");
                }else{
                    Log.d("", "login:连接服务器失败");
                    return false;
                }
            }
            if(isLogined){
                signOut();//注销登录的
            }

            //xmpptcpConnection.connect();
            if(xmpptcpConnection.isConnected()) {
                //MainActivity.unBindPacket();
                boolean sameUser=false;
                boolean beforeLogined=false;
                String beforeUser=sharedPreferences.getString("userName","");                   //得到之前登陆的人名，与此次登陆的比较
                Presence presence1 = new Presence(Presence.Type.unavailable);
                presence1.setStatus("OFFLINE");
                Presence presence2 = new Presence(Presence.Type.available);
                presence2.setStatus("ONLINE");
                 xmpptcpConnection.login(userName, passwd);    //login TODO
                Log.d("", "login: 设置离线状态");
                xmpptcpConnection.sendStanza(presence1);           //设置离线状态获取离线消息
                MainActivity.bindPacket();//绑定
                getOfflineMessage();
                //addFriendListener();//接收好友请求拒绝删除等信息报

                xmpptcpConnection.sendStanza(presence2);//设置在线状态

                //1 同一人，则不获取好友信息 2 不同，先关闭数据库，然后创建数据库，再获取好友信息,还要删除好友列表和会话列表信息 3 之前没登陆过，创建数据库，获取好友信息
                roster=Roster.getInstanceFor(xmpptcpConnection);
                roster.setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理
                if(Connect.smackUserInfo.getUserName().equals("点击登录")){
                    Connect.dataBaseHelp=new DataBase(context,"DataBaseOf"+userName+".db",null,1,userName);
                    db=dataBaseHelp.getWritableDatabase();
                    Connect.initFriend();   //获取好友列表
                }else{
                    if(!userName.equals(beforeUser))          //不是同一人
                    {
                        //删除前一个人的数据库

                            //Connect.db.execSQL("drop database DataBaseOf"+beforeUser+".db");
                            File file=new File("/data/data/com.example.cm/databases/DataBaseOf"+beforeUser+".db");
                            if(file!=null&&file.exists()) {
                                file.delete();
                                Log.d("", "login: 删除上次登陆人的数据库成功");
                            }

                        //TODO close database opened before
                        Connect.dataBaseHelp=new DataBase(context,"DataBaseOf"+userName+".db",null,1,userName);
                        db=dataBaseHelp.getWritableDatabase();
                        Connect.friendInfoList.clear();
                        Connect.groupInfoList.clear();
                        Connect.messageMap.clear();
                        Connect.addFriendItemList.clear();
                        Connect.initFriend();    //获取好友列表
                    }
                }

                Connect.smackUserInfo.setUserName(userName);
                Bitmap bitmap=Connect.getUserImage(userName);
                Connect.smackUserInfo.setHeadBt(bitmap);
                String headBitmapRoad=AlbumUtil.saveHeadBitmap(userName,bitmap);
                Log.d("保存头像路径", "run: "+headBitmapRoad);
                Connect.editor.putString("userName",userName);
                Connect.editor.putString("passward",passwd);
                Connect.editor.putString("userHeadBtRoad",headBitmapRoad);
                Log.d("登陆后写入的登录名", "run: "+Connect.sharedPreferences.getString("userName",""));
                Connect.editor.putString("userHeadBtRoad",headBitmapRoad);
                Connect.editor.commit();

                //Connect.initChatMessageListener(); //初始化信息监听

                //addFriendListener();   //好友申请监听


            }
            else{
                xmpptcpConnection=null;
                //Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
                return  false;
            }

        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            xmpptcpConnection=null;
            //Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
            return false;
        }
        isLogined=true;
        return true;
    }
    //退出登录
    public static void signOut()  {
                if(xmpptcpConnection!=null){
                    Presence presence=new Presence(Presence.Type.unavailable);
                    presence.setStatus("GONE");
                    try {
                        xmpptcpConnection.sendStanza(presence);
                        xmpptcpConnection.disconnect();  //断开连接
                        xmpptcpConnection=null;
                        isLogined=false;
                        db.close();                 //关闭数据库
                        dataBaseHelp.close();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

    }
    public static boolean register(String user, String passwd, Map<String,String> info){
               if(xmpptcpConnection!=null) {
                   AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
                   try {
                       if(info.size()==0)
                          accountManager.createAccount(user,passwd);
                       else
                           accountManager.createAccount(user,passwd,info);
                   } catch (SmackException.NoResponseException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   } catch (XMPPException.XMPPErrorException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   } catch (SmackException.NotConnectedException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   }
               }else{
                   //Toast.makeText(context,"连接不到服务器",Toast.LENGTH_SHORT).show();
                   return false;
               }
        return true;

    }
    //好友申请监听
    public static void addFriendListener(){
        AndFilter andFilter=new AndFilter(new StanzaTypeFilter(Presence.class));
        StanzaListener stanzaListener=new StanzaListener() {
            @Override
            public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
                if(stanza instanceof Presence){
                    Presence presence=(Presence)stanza;
                    String fromId=presence.getFrom();            //格式是name@IP/Spark
                    String user=fromId.split("@")[0];
                    if(presence.getType().equals(Presence.Type.subscribe)){
                        //请求添加好友
                        AddFriendItem addFriendItem=new AddFriendItem();
                        FriendInfo friendInfo=new FriendInfo();
                        //friendInfo.setHeadBt(getUserImage(fromId.split("/")[0]));
                        friendInfo.setHeadBt(getUserImage(user));
                        friendInfo.setUserName(user);
                        friendInfo.setNicName(user);
                        addFriendItem.setFriendInfo(friendInfo);
                        addFriendItem.setReason(presence.getStatus());
                        addFriendItem.setResult("");//设置空值，否则报Nullpoint error
                        addFriendItemList.add(addFriendItem);
                        addFriendItemListChanged=true;
                        Log.d("Friend ADD", "processPacket: 接收到好友申请"+user);
                    }else if(presence.getType().equals(Presence.Type.subscribed)){
                        //同意添加好友
                        Log.d("", "processPacket: 对方同意添加你");
                        for(int i=0;i<addFriendItemList.size();i++){
                            if(addFriendItemList.get(i).getFriendInfo().getUserName().equals(user)){
                                addFriendItemList.get(i).setResult("对方已同意");
                                addFriend(user,user,new String[]{"Friends"});          //roster添加好友
                                addFriendItemListChanged=true;
                                break;
                            }
                        }
                    }else if(presence.getType().equals(Presence.Type.unsubscribe)){
                        //拒绝订阅
                        Log.d("", "processPacket: 对方拒绝添加你");
                        for(int i=0;i<addFriendItemList.size();i++){
                            if(addFriendItemList.get(i).getFriendInfo().getUserName().equals(user)){
                                addFriendItemList.get(i).setResult("对方已拒绝");
                                addFriendItemListChanged=true;
                                break;
                            }
                        }
                    }else if(presence.getType().equals(Presence.Type.unsubscribed)){//删除之后logd 对方拒绝添加你  对方删除你，对方下线
                        //取消订阅
                        Log.d("", "processPacket: 取消订阅，对方删除你"+user);
                        //删除之后自己也删除此人 调用deleteFriend(userName) 里面包括删除聊天记录，数据库好友信息，好友列表条目等
                        deleteFriend(user);
                        Log.d("", "processPacket: 您删除成功"+user);
                    }else if(presence.getType().equals(Presence.Type.unavailable)){
                        //对方下线
                        Log.d("", "processPacket: 对方下线");
                    }else if(presence.getType().equals(Presence.Type.available)){
                        //对方上线
                        Log.d("", "processPacket: 对方上线");
                    }
                }
            }
        };
        //添加监听
        //xmpptcpConnection.addAsyncStanzaListener(stanzaListener,andFilter);
        xmpptcpConnection.addAsyncStanzaListener(stanzaListener,andFilter);
        Log.d("请求监听", "addFriendListener: 好友请求监听开启...");

    }

    public static void initChatMessageListener() {
        ChatManager manager = ChatManager.getInstanceFor(Connect.xmpptcpConnection);
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void processMessage(Chat chat, Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
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
                            Bitmap bitmap=AlbumUtil.byte2Bitmap(AlbumUtil.encodedStr2byte(encodeImageStr));
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
                             dataBaseHelp.addMessage(message1);                //添加进聊天数据库
                        haveNewMessage=true;
                        //ChatActivity.chatAdapter.notifyDataSetInvalidated();
                        //添加进对应的聊天信息列表
                        boolean contain=false;
                        for(int i=0;i<friendInfoList.size();i++){
                            if(friendInfoList.get(i).getUserName().equals(userFrom)){     //会话列表包含此好友
                                messageMap.get(friendInfoList.get(i).getUserName()).add(message1);  //添加进聊天信息
                                contain=true;
                                Log.d("接收到信息 包含", "processMessage: "+message.getFrom());
                                break;
                            }
                        }
                        Log.d("分割", "processMessage: "+message.getFrom().split("/")[0]);
                        if(!contain){  //会话列表不包含此好友
                            dataBaseHelp.changeChatState(userFrom,1);           //改变数据库中聊天状态
                            FriendInfo friendInfo=new FriendInfo();
                            for(int i=0;i<Connect.groupInfoList.size();i++){//赋值给正在聊天的人
                                for(int j=0;j<Connect.groupInfoList.get(i).getFriendInfoList().size();j++){
                                    if(Connect.groupInfoList.get(i).getFriendInfoList().get(j).getUserName().equals(userFrom)){
                                        Connect.groupInfoList.get(i).getFriendInfoList().get(j).setChated(1);//设置为正在聊天标记
                                        friendInfo=Connect.groupInfoList.get(i).getFriendInfoList().get(j);
                                        break;
                                    }
                                }

                            }

                            Log.d("一 二", "processMessage: "+message.getFrom());
                            friendInfoList.add(friendInfo);
                            List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                            messageList.add(message1);
                            messageMap.put(userFrom,messageList);        //map新加一个
                        }

                        //message.setFrom(message.getFrom().split("/")[0]);
                        //message.setBody(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ChatManagerListener chatManagerListener = new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean arg1) {
                chat.addMessageListener(messageListener);
            }
        };
        manager.addChatListener(chatManagerListener);
    }
    public static void initFriend(){

        Log.d("登录用户",xmpptcpConnection.getUser().toString());

          /*roster=Roster.getInstanceFor(xmpptcpConnection);
          roster.setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理*/
        while(!roster.isLoaded()){
            try {
                roster.reload();
                Log.d("重连Roster",xmpptcpConnection.getUser().toString());

            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",xmpptcpConnection.getUser().toString());

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",xmpptcpConnection.getUser().toString());
            }
        }
        Log.d("重连成功",xmpptcpConnection.getUser().toString());

        Log.d("总好友数", String.valueOf(roster.getEntryCount()));
        Collection<RosterEntry> rosterEntries=roster.getEntries();
        for(RosterEntry entry:rosterEntries){
            FriendInfo friendInfo=new FriendInfo();
            String user=entry.getUser().split("@")[0];
            friendInfo.setUserName(user);
            friendInfo.setNicName(user);
            String pinyin = Cn2Spell.getPinYin(user); // 根据姓名获取拼音
            String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
            if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
                firstLetter = "#";
            }
            friendInfo.setPinyin(pinyin);
            friendInfo.setFirstLetter(firstLetter);
            String sex=getUserVcard(user).getField("sex");
            Log.d("得到子元素名称", "initFriend: "+getUserVcard(user).getChildElementName());
            if(sex==null){
                sex="保密";
            }
            friendInfo.setSex(sex);
            String email=getUserVcard(user).getEmailHome();
            if(email==null){
                email="";
            }
            friendInfo.setEmail(email);
            Log.d("", "initFriend: sex :"+sex+"   email:"+email);
            Bitmap bitmap=getUserImage(user);
            friendInfo.setHeadBt(bitmap);  //设置头像
            String friendHeadBitmapRoad=AlbumUtil.saveHeadBitmap(user,bitmap);  //保存好友头像
            friendInfo.setHeadBtRoad(friendHeadBitmapRoad);
            Log.d("好友头像保存路径", "initFriend: "+friendHeadBitmapRoad);
            //friendInfo.setSex();
            //添加返回一个values对象，须要db添加提交
            dataBaseHelp.addFriendInfo(friendInfo);
            //Log.d("", "initFriend: "+values.toString());
            //db.insert("FriendInfoTable",null,values);//添加进数据库
            contantFriendInfoList.add(friendInfo);
            Log.d("", "initFriend:添加进通讯录   "+contantFriendInfoList.size());
        }
        roster.addRosterListener(new RosterListener() {                                                //TODO 花名册监听
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
    public static void init(){
        haveNewMessage=false;
        messageMap=new HashMap<>();
        friendInfoList=new ArrayList<>();
        contantFriendInfoList=new ArrayList<>();
        addFriendItemList=new ArrayList<>();
        groupInfoList=new ArrayList<>();
        smackUserInfo=new SmackUserInfo();
        isLogined=false;
       // sharedPreferences在刚开始的MainActivity里面初始化
        //isLinked=false;
        //roster = Roster.getInstanceFor(xmpptcpConnection);
    }
    //添加好友指定分组
    public static  boolean addFriend(String userName,String name,String []groups){
        try {
            roster=Roster.getInstanceFor(xmpptcpConnection);
            while (!roster.isLoaded()){
                roster.reload();
            }
            userName=userName+"@"+SERVERNAME;
            roster.createEntry(userName,name,groups);
            //TODO 数据库和好友列表需要更新
            String userName1=userName.split("@")[0];
            FriendInfo friendInfo=new FriendInfo();
            friendInfo.setUserName(userName1);
            friendInfo.setNicName(userName1);
            friendInfo.setSex(getUserVcard(userName.split("@")[0]).getField("sex"));
            friendInfo.setEmail(getUserVcard(userName.split("@")[0]).getEmailHome());
            Bitmap bitmap=getUserImage(userName1);
            String friendBtRoad=AlbumUtil.saveHeadBitmap(userName1,bitmap);
            friendInfo.setHeadBt(bitmap);
            friendInfo.setHeadBtRoad(friendBtRoad);
            friendInfo.setChated(0);
            dataBaseHelp.addFriendInfo(friendInfo);   //数据库添加好友信息
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return  false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
    //删除好友 此处userName没有@后的字符串
    public  static  boolean deleteFriend(String userName){
        try {
            roster=Roster.getInstanceFor(xmpptcpConnection);
            while(!roster.isLoaded()){
                roster.reload();
            }
            userName=userName+"@"+SERVERNAME;
            roster.removeEntry(roster.getEntry(userName));
            Log.d("", "deleteFriend: 删除好友成功");
            //TODO           数据库和好友列表需要更新
            dataBaseHelp.deleteFriendInfo(userName.split("@")[0]);   //数据库删除好友条目
            Log.d("", "deleteFriend: 数据库删除好友条目成功");
            for(int i=0;i<contantFriendInfoList.size();i++){
                if(contantFriendInfoList.get(i).getUserName().equals(userName.split("@")[0])){
                    contantFriendInfoList.remove(i);
                }
            }
            for(int i=0;i<friendInfoList.size();i++){         //删除对应的会话列表条目
                if(friendInfoList.get(i).getUserName().equals(userName.split("@")[0])){
                    friendInfoList.remove(i);
                    messageMap.remove(userName.split("@")[0]);
                    Log.d("", "deleteFriend: 删除好友聊天成功");
                }
            }
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return  false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return  false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return  false;
        }
        return true;
    }
    //新建分组
    public static boolean addGroup(String groupName){
        while (!roster.isLoaded()){
            try {
                roster.reload();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        roster.createGroup(groupName);
        return true;
    }
    /*获取用户头像
    *param userName
    */
    public static Bitmap getUserImage(String user){
        //user=user+"@"+SERVERNAME;
        Bitmap bitmap=null;
        //VCard vCard=new VCard();
            //vCard.load(xmpptcpConnection,user);
            Log.d("获取头像测试", "getUserImage: ");
            byte []bytes=getUserVcard(user).getAvatar();
            ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
            bitmap=BitmapFactory.decodeStream(bais);
        return bitmap;
    }
    /*修改头像
    *@param XMPPConnection
    *@param String 图片的绝对路径
    */
    public static boolean changeImage(XMPPConnection connection,String absoluteRoad)
            throws XMPPException, IOException {

        VCard vcard;
        try {
            //vcard.load(xmpptcpConnection);
            vcard=VCardManager.getInstanceFor(xmpptcpConnection).loadVCard();


        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        File photoFile=new File(absoluteRoad);
        byte []bytes=File2byte(photoFile);
         vcard.setAvatar(bytes);
        try {
            //vcard.save(xmpptcpConnection);
            VCardManager.getInstanceFor(xmpptcpConnection).saveVCard(vcard);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    //得到联系人基本信息
    public static VCard getUserVcard(String user){
        VCard vCard=null;
        //vCard=new VCard();
        try {
            vCard=VCardManager.getInstanceFor(xmpptcpConnection).loadVCard(user+"@"+SERVERNAME);
            //vCard.load(xmpptcpConnection,user+"@"+SERVERNAME);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return vCard;
    }
    //文件转byte[]
    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }
    //设置自身信息函数key,value 昵称key为NickName 性别：gender 生日：birthday
    static public  boolean setInfo(String key,String value){
        boolean isSucceed=false;
        if((key!=null)&&(value!=null)) {
            try {
                VCard vCard = VCardManager.getInstanceFor(xmpptcpConnection).loadVCard();
                if(vCard!=null){
                    if(key.equals("NickName")){
                        vCard.setNickName(value);
                    }else{
                        vCard.setField(key,value);
                    }
                }
                VCardManager.getInstanceFor(xmpptcpConnection).saveVCard(vCard);
                isSucceed=true;
                Log.d("设置自身信息测试", "setInfo: 设置自身信息成功" + vCard.getEmailHome());
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
                isSucceed=false;
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
                isSucceed=false;
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                isSucceed=false;
            }
        }else{
            isSucceed=false;
        }
        return isSucceed;
    }
    //获取离线消息
    //猜想 Message 继承Packet 在Message处理之前先转换成Staza看是不是添加好友删除好友的包
    public static List<com.example.cm.friend.chat.Message> getOfflineMessage(){
        List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
        OfflineMessageManager offlineMessageManager=new OfflineMessageManager(xmpptcpConnection);
        try {
            List<Message> messages=offlineMessageManager.getMessages();
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
}
