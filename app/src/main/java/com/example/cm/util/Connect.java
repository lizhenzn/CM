package com.example.cm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.chat.ChatActivity;
import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.SmackUserInfo;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Connect {
    private final Context context;
    public static List<GroupInfo> groupInfoList;
    public  static SmackUserInfo  smackUserInfo;
    public static boolean haveNewMessage;
    public static HashMap<String,List<com.example.cm.friend.chat.Message>>messageMap;                            //会话列表String对应好友，List<Message>为对应此String好友聊天信息列表
    public  static List<FriendInfo>   friendInfoList;                                                 //会话列表好友名列表
    public Connect(Context context){
        this.context=context;
        init();
    }
    public static XMPPTCPConnection xmpptcpConnection=null;
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
                            //Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                            initListener(); //初始化信息监听
                            initFriend();   //获取好友列表
                            Chat chat=ChatManager.getInstanceFor(xmpptcpConnection).createChat("lizhen@127.0.0.1");
                            chat.sendMessage("Hello!!!");

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
                }
        return  true;
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
    //获取用户头像
    /*public static Drawable getHeadImage(String user){
        byte[] bytes=null;
        try{
            VCard vCard=new VCard();
            ProviderManager.addIQProvider("vCard","vcard-temp",new VCardProvider());

            vCard.load(xmpptcpConnection,user+"@"+xmpptcpConnection.getServiceName());
            if(vCard==null||vCard.getAvatar()==null)
                return null;
            bytes=vCard.getAvatar();

        }catch(Exception e){

        }
        if(bytes==null)
            return null;
        return PhotoFormatTools.byteArrayToDrawable(bytes);
    }*/
    public void initListener() {
        ChatManager manager = ChatManager.getInstanceFor(Connect.xmpptcpConnection);
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                if (!TextUtils.isEmpty(message.getBody())) {
                    try {
                        //JSONObject object = new JSONObject(message.getBody());
                        //String type = object.getString("type");
                        //String data = object.getString("data");
                        Log.d("TAG接收************", message.getBody());
                        com.example.cm.friend.chat.Message message1=new com.example.cm.friend.chat.Message();
                        JSONObject jsonObject=new JSONObject(message.getBody());
                        message1.setBody(jsonObject.getString("data"));
                        message1.setFrom(message.getFrom().split("/")[0]);
                        message1.setTo(message.getTo());
                        message1.setDate(new Date());
                        message1.setType(2);
                         message1.setMessageType(jsonObject.getString("type"));
                        haveNewMessage=true;
                        //ChatActivity.chatAdapter.notifyDataSetInvalidated();
                        //添加进对应的聊天信息列表
                        boolean contain=false;
                        for(int i=0;i<friendInfoList.size();i++){
                            if(friendInfoList.get(i).getUserName().equals(message.getFrom().split("/")[0])){     //会话列表包含此好友
                                messageMap.get(friendInfoList.get(i).getUserName()).add(message1);  //添加进聊天信息
                                contain=true;
                                Log.d("接收到信息 包含", "processMessage: "+message.getFrom());
                                break;
                            }
                        }
                        Log.d("分割", "processMessage: "+message.getFrom().split("/")[0]);
                        if(!contain){  //会话列表不包含此好友
                            FriendInfo friendInfo=new FriendInfo();
                            friendInfo.setUserName(message.getFrom().split("/")[0]);
                            Log.d("一 二", "processMessage: "+message.getFrom());
                            //friendInfo.setHeadBt(message.getBody());
                            friendInfoList.add(friendInfo);
                            List<com.example.cm.friend.chat.Message> messageList=new ArrayList<>();
                            messageList.add(message1);
                            messageMap.put(message.getFrom().split("/")[0],messageList);        //map新加一个
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
    public void initFriend(){
        groupInfoList=new ArrayList<>();
        smackUserInfo=new SmackUserInfo();
        Log.d("登录用户",xmpptcpConnection.getUser().toString());

        Roster roster = Roster.getInstanceFor(xmpptcpConnection);
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
        Collection<RosterGroup> groups = roster.getGroups();
        Log.d("组数", String.valueOf(roster.getGroupCount()));
        for (RosterGroup group : groups) {
            GroupInfo groupInfo=new GroupInfo();
            groupInfo.setGroupName(group.getName());   //设置组名
            List<FriendInfo> friendInfoList=new ArrayList<>();  //此组中的好友
            for(RosterEntry rosterEntry:group.getEntries()){
                FriendInfo friendInfo=new FriendInfo();
                friendInfo.setUserName(rosterEntry.getUser());
                friendInfo.setNicName(rosterEntry.getUser());
                //friendInfo.setHeadBt(R.drawable.chat_add);  //设置头像
                //friendInfo.setSex();
                friendInfo.setGroupName(group.getName());
                friendInfoList.add(friendInfo);   //添加
            }
            groupInfo.setFriendInfoList(friendInfoList);
            groupInfoList.add(groupInfo);   //添加到全局组详情列表

        }
        Log.d("总好友数", String.valueOf(roster.getEntryCount()));
        Collection<RosterEntry> rosterEntryList=roster.getEntries();
        for(RosterEntry rosterEntry:rosterEntryList){

        }

    }
    public void init(){
        haveNewMessage=false;
        messageMap=new HashMap<>();
        friendInfoList=new ArrayList<>();
    }
    public static int getPositionByUserName(String userName){
        int i;
        for(i=0;i<friendInfoList.size();i++){
            if(friendInfoList.get(i).getNicName().equals(userName)){     //会话列表包含此好友
                break;
            }
        }
        if(i>=friendInfoList.size())
            return -1;
        return i;
    }
}
