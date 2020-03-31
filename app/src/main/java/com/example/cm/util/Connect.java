package com.example.cm.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.friend.AddFriendItem;
import com.example.cm.friend.Cn2Spell;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;


import org.jivesoftware.smack.ConnectionConfiguration;


import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
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
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;

import org.jivesoftware.smack.roster.RosterEntry;

import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SNIServerName;

import static android.support.v4.content.ContextCompat.getSystemService;


public class Connect {
    public static final String SERVERNAME="iz92swk4q6t1a7z";       //服务器名	iz92swk4q6t1a7z
    public static final String IP="114.55.255.210";                //服务器IP地址
    private  static Roster roster;
    public static boolean isLogined;  //登陆是否
    public  static boolean isNetAvailable;  //网络是否可用
    private static XMPPTCPConnection xmpptcpConnection=null;
    private static ConnectionListener connectionListener=null;
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
        if(connectionListener==null) {
            connectionListener = new ConnectionListener() {
                @Override
                public void connected(XMPPConnection xmppConnection) {
                    Log.e("", "connected: 连接成功");
                }

                @Override
                public void authenticated(XMPPConnection xmppConnection, boolean b) {
                    Log.e("", "authenticated: 登陆成功");
                }

                @Override
                public void connectionClosed() {
                    Log.e("", "connectionClosed: 连接关闭");

                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    Log.e("", "connectionClosedOnError: 连接异常断开");
                    if (e instanceof XMPPException) {
                        XMPPException.StreamErrorException xe = (XMPPException.StreamErrorException) e;
                        StreamError error = xe.getStreamError();
                        StreamError.Condition condition;
                        if (error != null) {
                            condition = error.getCondition();
                            if (condition.equals(StreamError.Condition.conflict)) {
                                Log.e("", "connectionClosedOnError:别处登录冲突 ");
                                //TODO 弹出框提示 强制下线

                                MessageManager.getSmackUserInfo().setNiC("点击登录");
                                //Connect.signOut();
                                Connect.isLogined = false;
                                setRoster(null);
                                setXmpptcpConnection(null);
                                MessageManager.getDb().close();
                                MessageManager.getDataBaseHelp().close();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Resources resources = MainActivity.getInstance().getResources();
                                        DisplayMetrics dm = resources.getDisplayMetrics();
                                        int width = dm.widthPixels;
                                        int height = dm.heightPixels;
                                        Looper.prepare();
                                        Dialog dialog = new Dialog(MainActivity.getInstance(), R.style.LoginConflictDialog);
                                        View view = View.inflate(MainActivity.getInstance(), R.layout.conflict, null);
                                        Button ensureBtn=(Button)view.findViewById(R.id.coflict_ensure_btn);

                                        dialog.setContentView(view);
                                        dialog.setCanceledOnTouchOutside(false);
                                        view.setMinimumHeight((int) (height * 0.23f));
                                        Window dialogWindow = dialog.getWindow();
                                        dialogWindow.setWindowAnimations(R.style.DialogAnimation);//设置Dialog出现退出动画
                                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                                        lp.width = (int) (width * 0.9f);
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        lp.gravity = Gravity.CENTER;
                                        dialogWindow.setAttributes(lp);
                                        ((Activity)MainActivity.getInstance()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("", "run: 冲突" );
                                                MainActivity.setInfo();
                                                //dialog.show();
                                                connectionListener=null;


                                            }
                                        });
                                        dialog.show();
                                        ensureBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ((Activity)MainActivity.getInstance()).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }});
                                        Looper.loop();
                                    }
                                }).start();


                            }
                        }
                    }
                }

                @Override
                public void reconnectionSuccessful() {
                    Log.e("", "reconnectionSuccessful: 重连成功");
                    if (xmpptcpConnection.isAuthenticated()) {
                        try {
                            xmpptcpConnection.login();
                            //重连之后登录
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void reconnectingIn(int i) {

                }

                @Override
                public void reconnectionFailed(Exception e) {
                    Log.e("", "reconnectionFailed: 重连失败");
                }
            };
            xmpptcpConnection.addConnectionListener(connectionListener);
            Log.e("", "getXMPPTCPConnection: 添加连接监听" );
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

    /*
    *param userName
    * param passwd
    * @return 0:服务器连接异常 1:登陆异常 2：登陆成功 3：密码错误
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int login(String userName, String passwd){
        try {
            isLogined=false;
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
                Log.e("", "login: "+MessageManager.getMessageMap() );
                MessageManager.setHaveNewMessage(true);
                Log.e("", "login: 数据库获取聊天消息之后" );
                MessageManager.getOfflineMessage();
                MainActivity.bindPacket();//绑定

                VCard selfVCard=VCardManager.getUserVcard(userName);
                Log.e("自己VCard", "login: "+selfVCard );
                MessageManager.getSmackUserInfo().setUserName(userName);
                Log.e("", "login: "+selfVCard.getNickName() );
                if(selfVCard.getNickName()==null){
                    Log.e("", "login: 为空" );
                    MessageManager.getSmackUserInfo().setNiC(userName);
                }else {
                    MessageManager.getSmackUserInfo().setNiC(selfVCard.getNickName());
                }
                if(selfVCard.getField("email")!=null){
                    MessageManager.getSmackUserInfo().setEmail(selfVCard.getField("email"));
                }
                if(selfVCard.getField("gender")!=null) {
                    MessageManager.getSmackUserInfo().setSex(selfVCard.getField("gender"));
                }
                if(selfVCard.getField("height")!=null) {
                    MessageManager.getSmackUserInfo().setHeight(selfVCard.getField("height"));
                }
                Bitmap bitmap= VCardManager.getUserImage(userName);
                MessageManager.getSmackUserInfo().setHeadBt(bitmap);
                String headBitmapRoad=AlbumUtil.saveHeadBitmap(userName,bitmap);
                Log.d("保存头像路径", "run: "+headBitmapRoad);
                MessageManager.getEditor().putString("userName",userName);
                MessageManager.getEditor().putString("password",passwd);
                MessageManager.getEditor().putString("NICKNAME",MessageManager.getSmackUserInfo().getNiC());
                MessageManager.getEditor().putString("gender",MessageManager.getSmackUserInfo().getSex());
                MessageManager.getEditor().putString("email",MessageManager.getSmackUserInfo().getEmail());
                MessageManager.getEditor().putString("height",MessageManager.getSmackUserInfo().getHeight());
                MessageManager.getEditor().putString("userHeadBtRoad",headBitmapRoad);
                MessageManager.getEditor().commit();
                getXMPPTCPConnection().sendStanza(presence2);//设置在线状态
                Log.d("登陆后写入的登录名", "run: "+MessageManager.getSharedPreferences().getString("userName",""));
            }
            else{
                MainActivity.setDefaultSmackInfo();
                if(xmpptcpConnection!=null){
                    xmpptcpConnection.removeConnectionListener(connectionListener);
                    connectionListener=null;
                    xmpptcpConnection.disconnect();
                    xmpptcpConnection=null;
                }
                //Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
                return  0;
            }

        } catch (SmackException | IOException  e) {
            e.printStackTrace();
            MainActivity.setDefaultSmackInfo();
            if(xmpptcpConnection!=null){
                xmpptcpConnection.removeConnectionListener(connectionListener);
                connectionListener=null;
                xmpptcpConnection.disconnect();
                xmpptcpConnection=null;
            }
            //Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
            return 1;
        }
        catch (XMPPException.XMPPErrorException e){
            MainActivity.setDefaultSmackInfo();
            if(xmpptcpConnection!=null){
                xmpptcpConnection.removeConnectionListener(connectionListener);
                connectionListener=null;
                xmpptcpConnection.disconnect();
                xmpptcpConnection=null;
            }
            XMPPError error=e.getXMPPError();
            if(error.getCondition().equals(XMPPError.Condition.item_not_found)) {  //密码错误
                return 3;
            }else{
                return 1;
            }
        }catch (XMPPException e){
            e.printStackTrace();
            MainActivity.setDefaultSmackInfo();
            if(xmpptcpConnection!=null){
                xmpptcpConnection.removeConnectionListener(connectionListener);
                connectionListener=null;
                xmpptcpConnection.disconnect();
                xmpptcpConnection=null;
            }
            return 1;
        }
        isLogined=true;
        return 2;
    }
    //退出登录
    public static void signOut()  {
        if(xmpptcpConnection!=null){
            Presence presence=new Presence(Presence.Type.unavailable);
            presence.setStatus("GONE");
            try {
                getXMPPTCPConnection().sendStanza(presence);
                getXMPPTCPConnection().removeConnectionListener(connectionListener);
                Log.e("", "signOut: 移除连接监听" );
                getXMPPTCPConnection().disconnect();  //断开连接
                roster=null;
                connectionListener=null;
                xmpptcpConnection=null;
                connectionListener=null;
                isLogined=false;
                MainActivity.setDefaultSmackInfo();
                MessageManager.getDb().close();                 //关闭数据库
                MessageManager.getDataBaseHelp().close();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
    /* @user 用户账号
    * @passwd 密码
    * @return 0、服务器无反应 1、注册成功 2、已有此账号 3、注册失败
    * */
    public static int register(String user, String passwd, Map<String,String> info){
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
                       VCardManager.setSelfInfo(xmpptcpConnection,"NICKNAME",user);
                       xmpptcpConnection.disconnect();//关闭连接
                   } catch (SmackException.NoResponseException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return 0;
                   } catch (XMPPException.XMPPErrorException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       XMPPError error=e.getXMPPError();
                       if(error.getCondition().equals(XMPPError.Condition.conflict)) {  //已有此账号，冲突
                           Log.e("", "register: 已有此账号，冲突" );
                           return 2;
                       }else{
                           return 3;
                       }
                   } catch (SmackException.NotConnectedException e) {
                       //Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return 0;
                   } catch (XMPPException e) {
                       e.printStackTrace();
                       return 3;
                   } catch (IOException e) {
                       e.printStackTrace();
                       return 3;
                   } catch (SmackException e) {
                       e.printStackTrace();
                       return 3;
                   }
               }else{
                   //Toast.makeText(context,"连接不到服务器",Toast.LENGTH_SHORT).show();
                   return 0;
               }
        return 1;

    }

    public static void init(){
        isLogined=false;
        isNetAvailable=isNetAvailable();
    }
    //网络是否可用
    public static boolean isNetAvailable() {
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
