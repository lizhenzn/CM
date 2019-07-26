package com.example.cm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class ConnectService extends Service {
    public static XMPPTCPConnection connection;
    public ConnectService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化connection
        //getConnection();
        //login("lizhen","zn521128");
    }

    public static XMPPTCPConnection getConnection(){
            try {
                if(connection==null) {
                    XMPPTCPConnectionConfiguration config= XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword("admin","z98745213n")
                            .setXmppDomain("jabber.org")
                            .setHost("127.0.0.1")
                           .setPort(5222)
                           .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled).build();
                    //configBuilder.setCompressionEnabled(false);
                    //configBuilder.setDebuggerEnabled(true);
                    connection=new XMPPTCPConnection(config);
                    //连接
                    connection.connect();
                }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  connection;
    }
    //登录功能
    public static  boolean login(String user,String pwd){
        try {
            connection.login(user,pwd);
            return  true;
        } catch (XMPPException e) {
            e.printStackTrace();
            return  false;

        } catch (SmackException e) {
            e.printStackTrace();
            return  false;

        } catch (IOException e) {
            e.printStackTrace();
            return  false;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return  false;

        }
    }
  //注册功能
    public static boolean register(String username,String pwd){
        try{
            //AccountManager.getInstance(connection).createAccount(username,pwd);
             return true;
        }catch(Exception e){
           return false;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
