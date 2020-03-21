package com.example.cm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.AddFriendItem;
import com.example.cm.friend.Cn2Spell;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;


import org.jivesoftware.smack.ConnectionConfiguration;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;


import org.jivesoftware.smack.filter.StanzaTypeFilter;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import org.jivesoftware.smack.roster.Roster;

import org.jivesoftware.smack.roster.RosterEntry;

import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;



import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Map;

import javax.net.ssl.SNIServerName;


public class Connect {
    public static final String SERVERNAME="iz92swk4q6t1a7z";       //服务器名	iz92swk4q6t1a7z
    public static final String IP="114.55.255.210";                //服务器IP地址
    private  static Roster roster;
    public static boolean isLogined;  //登陆是否
    public  static boolean isNetAvailable;  //网络是否可用
    private static XMPPTCPConnection xmpptcpConnection=null;
    private Connect(){}
    public static XMPPTCPConnection getXMPPTCPConnection(){
                if(xmpptcpConnection==null) {
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setServiceName(SERVERNAME)
                            .setHost(IP)
                            .setPort(5222)
                            .setSendPresence(false)         //设置false可以接受离线信息
                            .setDebuggerEnabled(true)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();
                    xmpptcpConnection = new XMPPTCPConnection(config);
                    Log.d("XMPPTCP new 之后", ""+xmpptcpConnection);
                    try {
                        Log.d("", "getXMPPTCPConnection: connect之前"+xmpptcpConnection);
                        xmpptcpConnection.connect();
                        return xmpptcpConnection;

                    } catch (SmackException e) {
                        e.printStackTrace();
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
        return  xmpptcpConnection;
    }
    public static Roster getRoster(){         //好友花名册
        if(roster==null){
            if((getXMPPTCPConnection().isConnected())&&(getXMPPTCPConnection().isAuthenticated())){
                roster=Roster.getInstanceFor(getXMPPTCPConnection());
            }
        }
        return roster;
    }
    //用于设置连接为空
    public static void setXmpptcpConnection(XMPPTCPConnection mxmpptcpConnection){
        xmpptcpConnection=mxmpptcpConnection;
    }
    public static void setRoster(Roster mRoster){
        roster=mRoster;
    }
    public static boolean login(String userName,String passwd){
        try {
            while(xmpptcpConnection==null){
                getXMPPTCPConnection();
                Log.e("", "login: 连接服务器");
            }

            if(MainActivity.isBinded) {
                    MainActivity.unBindPacket();
            }
            if(isLogined){
                signOut();//注销登录的
            }

            if(getXMPPTCPConnection().isConnected()) {
                boolean sameUser=false;
                boolean beforeLogined=false;
                String beforeUser=MessageManager.getSharedPreferences().getString("userName","");                 //得到之前登陆的人名，与此次登陆的比较
                Presence presence1 = new Presence(Presence.Type.unavailable);
                presence1.setStatus("OFFLINE");
                Presence presence2 = new Presence(Presence.Type.available);
                presence2.setStatus("ONLINE");
                 getXMPPTCPConnection().login(userName, passwd);    //login TODO
                Log.d("", "login: 设置离线状态");
                getXMPPTCPConnection().sendStanza(presence1);           //设置离线状态获取离线消息
                MainActivity.bindPacket();//绑定
                MessageManager.getOfflineMessage();
                getXMPPTCPConnection().sendStanza(presence2);//设置在线状态
                getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理好友请求

                //1 同一人，则不获取好友信息 2 不同，先关闭数据库，然后创建数据库，再获取好友信息,还要删除好友列表和会话列表信息 3 之前没登陆过，创建数据库，获取好友信息
                MessageManager.setDataBaseHelp(userName);
                MessageManager.getDataBaseHelp().deleteAllFriendInfo();//删除所有好友信息，随后更新
                MessageManager.clearAllList();//清理所有的list
                Connect.initFriend();    //获取好友列表
                MessageManager.setMessageMap(MessageManager.getDataBaseHelp().getMessageHashMap());//数据库获得聊天信息

                MessageManager.getSmackUserInfo().setUserName(userName);
                Bitmap bitmap= VCardManager.getUserImage(userName);
                MessageManager.getSmackUserInfo().setHeadBt(bitmap);
                String headBitmapRoad=AlbumUtil.saveHeadBitmap(userName,bitmap);
                Log.d("保存头像路径", "run: "+headBitmapRoad);
                MessageManager.getEditor().putString("userName",userName);
                MessageManager.getEditor().putString("passward",passwd);
                MessageManager.getEditor().putString("userHeadBtRoad",headBitmapRoad);
                MessageManager.getEditor().putString("userHeadBtRoad",headBitmapRoad);
                MessageManager.getEditor().commit();
                Log.d("登陆后写入的登录名", "run: "+MessageManager.getSharedPreferences().getString("userName",""));
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
                        getXMPPTCPConnection().sendStanza(presence);
                        getXMPPTCPConnection().disconnect();  //断开连接
                        roster=null;
                        xmpptcpConnection=null;
                        isLogined=false;
                        MessageManager.getDb().close();                 //关闭数据库
                        MessageManager.getDataBaseHelp().close();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
    }
    public static boolean register(String user, String passwd, Map<String,String> info){
        XMPPTCPConnection xmpptcpConnection=getXMPPTCPConnection();
               if(xmpptcpConnection.isConnected()) {
                   AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
                   try {
                          accountManager.createAccount(user,passwd);
                          xmpptcpConnection.login(user,passwd);
                       for(Map.Entry<String, String> entry : info.entrySet()){
                           String mapKey = entry.getKey();
                           String mapValue = entry.getValue();
                           VCardManager.setSelfInfo(xmpptcpConnection,mapKey,mapValue);//设置注册信息
                           System.out.println(mapKey+":"+mapValue);
                       }
                       xmpptcpConnection.disconnect();//关闭连接

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
                   } catch (XMPPException e) {
                       e.printStackTrace();
                   } catch (IOException e) {
                       e.printStackTrace();
                   } catch (SmackException e) {
                       e.printStackTrace();
                   }
               }else{
                   //Toast.makeText(context,"连接不到服务器",Toast.LENGTH_SHORT).show();
                   return false;
               }
        return true;

    }
    //从服务器获取好友信息
    public static void initFriend(){

        Log.d("登录用户",xmpptcpConnection.getUser().toString());

          /*roster=Roster.getInstanceFor(xmpptcpConnection);
          roster.setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理*/
        while(!getRoster().isLoaded()){
            try {
                getRoster().reload();
                Log.d("重连Roster",xmpptcpConnection.getUser().toString());

            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",xmpptcpConnection.getUser().toString());

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d("重连异常抛出",xmpptcpConnection.getUser().toString());
            }
        }
        Log.d("重连成功",getXMPPTCPConnection().getUser().toString());

        Log.e("总好友数", String.valueOf(roster.getEntryCount()));
        Collection<RosterEntry> rosterEntries=roster.getEntries();
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
        getRoster().addRosterListener(new RosterListener() {                                                //TODO 花名册监听
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
        isLogined=false;
        isNetAvailable=isNetAvailable;
    }
    //网络是否可用
    public  boolean isIsNetAvailable() {
        boolean isNetUsable = false;
        ConnectivityManager manager = (ConnectivityManager)
                MainActivity.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {    //版本6.0以上
            NetworkCapabilities networkCapabilities =
                    manager.getNetworkCapabilities(manager.getActiveNetwork());
            isNetUsable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Log.e("", "isIsNetAvailable: 版本6.0以上" );
        }else{                                                      //版本6.0以下
            Runtime runtime = Runtime.getRuntime();
            try {
                Process p = runtime.exec("ping -c 3 www.baidu.com");
                int ret = p.waitFor();
                if(ret==0){
                    isNetUsable=true;
                    Log.e("", "isIsNetAvailable: 版本6.0以下" );
                }else{
                    isNetUsable=false;
                    Log.e("", "isIsNetAvailable: 版本6.0以下" );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.e("", "isIsNetAvailable: 网络连接可用？"+isNetUsable );
        return isNetUsable;

    }
}
