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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean login(String userName, String passwd){
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
                Presence presence1 = new Presence(Presence.Type.unavailable);
                presence1.setStatus("OFFLINE");
                Presence presence2 = new Presence(Presence.Type.available);
                presence2.setStatus("ONLINE");
                 getXMPPTCPConnection().login(userName, passwd);    //login TODO

                Log.d("", "login: 设置离线状态");
                getXMPPTCPConnection().sendStanza(presence1);           //设置离线状态获取离线消息

                getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);//设置手动处理好友请求

                //1 同一人，则不获取好友信息 2 不同，先关闭数据库，然后创建数据库，再获取好友信息,还要删除好友列表和会话列表信息 3 之前没登陆过，创建数据库，获取好友信息
                MessageManager.setDataBaseHelp(userName);
                MessageManager.getDataBaseHelp().deleteAllFriendInfo();//删除所有好友信息，随后更新
                MessageManager.clearAllList();//清理所有的list
                MessageManager.initFriend();    //获取好友列表
                MessageManager.setMessageMap(MessageManager.getDataBaseHelp().getMessageHashMap());//数据库获得聊天信息
                MainActivity.bindPacket();//绑定
                MessageManager.getOfflineMessage();
                VCard selfVCard=VCardManager.getUserVcard(userName);
                Log.e("自己VCard", "login: "+selfVCard );
                MessageManager.getSmackUserInfo().setUserName(userName);
                String nicName="";
                nicName=selfVCard.getNickName();
                if(nicName.equals("")){
                    MessageManager.getSmackUserInfo().setNiC(userName);
                }else {
                    MessageManager.getSmackUserInfo().setNiC(nicName);
                }
                MessageManager.getSmackUserInfo().setEmail(selfVCard.getField("email"));
                MessageManager.getSmackUserInfo().setSex(selfVCard.getField("gender"));
                Bitmap bitmap= VCardManager.getUserImage(userName);
                MessageManager.getSmackUserInfo().setHeadBt(bitmap);
                String headBitmapRoad=AlbumUtil.saveHeadBitmap(userName,bitmap);
                Log.d("保存头像路径", "run: "+headBitmapRoad);
                MessageManager.getEditor().putString("userName",userName);
                MessageManager.getEditor().putString("passward",passwd);
                MessageManager.getEditor().putString("NickName",selfVCard.getField("NickName"));
                MessageManager.getEditor().putString("gender",selfVCard.getField("gender"));
                MessageManager.getEditor().putString("email",selfVCard.getField("email"));
                MessageManager.getEditor().putString("userHeadBtRoad",headBitmapRoad);
                MessageManager.getEditor().commit();
                getXMPPTCPConnection().sendStanza(presence2);//设置在线状态
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
                       VCardManager.setSelfInfo(xmpptcpConnection,"NickName",user);
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
