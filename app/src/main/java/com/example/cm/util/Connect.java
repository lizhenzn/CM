package com.example.cm.util;

import android.content.Context;
import android.widget.Toast;

import com.example.cm.myInfo.LoginActivity;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.Map;

public class Connect {
    private final Context context;
    public Connect(Context context){
        this.context=context;
    }
    private static XMPPTCPConnection xmpptcpConnection=null;
    public boolean login(String user,String passwd){
                if(xmpptcpConnection==null) {
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            //.setUsernameAndPassword("lizhen", "zn521128")
                            .setServiceName("10.0.2.2")
                            .setHost("10.0.2.2")
                            .setPort(5222)
                            .setSendPresence(true)
                            .setDebuggerEnabled(true)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();
                    xmpptcpConnection = new XMPPTCPConnection(config);
                    try {
                        xmpptcpConnection.connect();
                        if(xmpptcpConnection.isConnected()) {
                            Presence presence = new Presence(Presence.Type.available);
                            presence.setStatus("ONLINE");
                            xmpptcpConnection.login(user, passwd);
                            xmpptcpConnection.sendStanza(presence);           //设置在线状态
                            Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            xmpptcpConnection=null;
                            Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
                            return  false;
                        }

                    } catch (SmackException | IOException | XMPPException e) {
                        e.printStackTrace();
                        xmpptcpConnection=null;
                        Toast.makeText(context,"登陆异常",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
        return  true;
    }
    //退出登录
    public void signOut()  {
                if(xmpptcpConnection!=null){
                    Presence presence=new Presence(Presence.Type.unavailable);
                    presence.setStatus("GONE");
                    try {
                        xmpptcpConnection.sendStanza(presence);
                        xmpptcpConnection.disconnect();  //断开连接
                        xmpptcpConnection=null;
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

    }
    public boolean register(String user, String passwd, Map<String,String> info){
               if(xmpptcpConnection!=null) {
                   AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
                   try {
                       if(info.size()==0)
                          accountManager.createAccount(user,passwd);
                       else
                           accountManager.createAccount(user,passwd,info);
                   } catch (SmackException.NoResponseException e) {
                       Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   } catch (XMPPException.XMPPErrorException e) {
                       Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   } catch (SmackException.NotConnectedException e) {
                       Toast.makeText(context,"注册异常",Toast.LENGTH_SHORT).show();
                       e.printStackTrace();
                       return false;
                   }
               }else{
                   Toast.makeText(context,"连接不到服务器",Toast.LENGTH_SHORT).show();
                   return false;
               }
        return true;

    }
}
