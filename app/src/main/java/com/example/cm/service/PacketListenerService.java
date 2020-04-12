package com.example.cm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.cm.friend.AddFriend.AddFriendItem;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
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

public class PacketListenerService extends Service {
    private  MyBinder binder=new MyBinder();
    public PacketListenerService() {
       // binder=new MyBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("服务创建", "onCreate: ");
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("服务销毁", "onDestroy: ");
    }
    public class MyBinder extends Binder{
        public void startListerer(){
            Log.d("包监听开始", "startListerer: ");
            addFriendListener();
            initChatMessageListener();
        }
  }
    //好友申请监听
    public  void addFriendListener(){
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
                        if(!Connect.getRoster().contains(user+"@"+ Connect.SERVERNAME)) {  //接收到好友申请，花名册没有该用户
                            AddFriendItem addFriendItem = new AddFriendItem();
                            FriendInfo friendInfo = new FriendInfo();
                            //friendInfo.setHeadBt(getUserImage(fromId.split("/")[0]));
                            friendInfo.setHeadBt(VCardManager.getUserImage(user));
                            friendInfo.setUserName(user);
                            friendInfo.setNicName(user);
                            addFriendItem.setFriendInfo(friendInfo);
                            addFriendItem.setReason(presence.getStatus());
                            addFriendItem.setResult("");//设置空值，否则报Nullpoint error
                            MessageManager.getAddFriendItemList().add(addFriendItem);
                            MessageManager.setAddFriendItemListChanged(true);
                            Log.e("Friend ADD", "processPacket: 接收到好友申请" + user);
                        }else{                                 //接收到好友申请，花名册有该用户
                            Presence presence1=new Presence(Presence.Type.subscribed);
                            presence1.setTo(user+"@"+Connect.SERVERNAME);
                            Connect.getXMPPTCPConnection().sendStanza(presence1);
                            Log.e("", "processPacket: 申请添加好友后对方同意且申请添加你为好友" );
                            MessageManager.addFriend(user);//联系人列表添加好友
                        }
                    }else if(presence.getType().equals(Presence.Type.subscribed)){
                        //同意添加好友
                        Log.e("", "processPacket: 对方同意添加你");
                        for(int i=0;i<MessageManager.getAddFriendItemList().size();i++){
                            if(MessageManager.getAddFriendItemList().get(i).getFriendInfo().getUserName().equals(user)){
                                MessageManager.getAddFriendItemList().get(i).setResult("对方已同意");
                                //addFriend(user,user,new String[]{"Friends"});          //roster添加好友
                                MessageManager.setAddFriendItemListChanged(true);
                                break;
                            }
                        }
                    }else if(presence.getType().equals(Presence.Type.unsubscribed)){
                        //拒绝订阅
                        Log.e("", "processPacket: 对方拒绝添加你");
                        for(int i=0;i< MessageManager.getAddFriendItemList().size();i++){
                            if(MessageManager.getAddFriendItemList().get(i).getFriendInfo().getUserName().equals(user)){
                                MessageManager.getAddFriendItemList().get(i).setResult("对方已拒绝");
                                MessageManager.setAddFriendItemListChanged(true);
                                break;
                            }
                        }
                        while(!Connect.getRoster().isLoaded()){
                            try {
                                Connect.getRoster().reload();
                            } catch (SmackException.NotLoggedInException e) {
                                e.printStackTrace();
                            }
                        }
                        if(Connect.getRoster().contains(user+"@"+Connect.SERVERNAME)){
                            try {
                                Connect.getRoster().removeEntry(Connect.getRoster().getEntry(user+"@"+Connect.SERVERNAME));
                                Log.e("", "processPacket: roster删除条目成功" );
                            } catch (SmackException.NotLoggedInException e) {
                                e.printStackTrace();
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(presence.getType().equals(Presence.Type.unsubscribe)){//删除之后logd 对方拒绝添加你  对方删除你，对方下线
                        //取消订阅
                        Log.e("", "processPacket: 取消订阅，对方删除你"+user);
                        while(!Connect.getRoster().isLoaded()){
                            try {
                                Connect.getRoster().reload();
                            } catch (SmackException.NotLoggedInException e) {
                                e.printStackTrace();
                            }
                        }
                        if(Connect.getRoster().contains(user+"@"+Connect.SERVERNAME)){
                            try {
                                Connect.getRoster().removeEntry(Connect.getRoster().getEntry(user+"@"+Connect.SERVERNAME));
                            } catch (SmackException.NotLoggedInException e) {
                                e.printStackTrace();
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            }
                        }
                        //删除之后自己也删除此人 调用deleteFriend(userName) 里面包括删除聊天记录，数据库好友信息，好友列表条目等
                        MessageManager.deleteFriend(user);
                        Log.e("", "processPacket: 您删除成功"+user);
                    }else if(presence.getType().equals(Presence.Type.unavailable)){
                        //对方下线
                        Log.e("", "processPacket: 对方下线");
                    }else if(presence.getType().equals(Presence.Type.available)){
                        //对方上线
                        Log.e("", "processPacket: 对方上线");
                    }
                }
            }
        };
        //添加监听
        Connect.getXMPPTCPConnection().addAsyncStanzaListener(stanzaListener,andFilter);
        Log.e("请求监听", "addFriendListener: 好友请求监听开启...");

    }
    //监听聊天信息
    public  void initChatMessageListener() {
        ChatManager manager = ChatManager.getInstanceFor(Connect.getXMPPTCPConnection());
        //设置信息的监听
        final ChatMessageListener messageListener = new ChatMessageListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void processMessage(Chat chat, Message message) {
                //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
                MessageManager.mergeMessage(message);
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


}
