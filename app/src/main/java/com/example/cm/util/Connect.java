package com.example.cm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.AddFriendItem;
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
    public static List<GroupInfo> groupInfoList;
    public  static SmackUserInfo  smackUserInfo;
    public static boolean haveNewMessage;
    public static HashMap<String,List<com.example.cm.friend.chat.Message>>messageMap;                            //会话列表String对应好友，List<Message>为对应此String好友聊天信息列表
    public  static List<FriendInfo>   friendInfoList;                                                 //会话列表好友名列表
    public static List<AddFriendItem> addFriendItemList;                         //添加好友详情列表
    public Connect(Context context){
        this.context=context;
        init();
    }
    public static XMPPTCPConnection xmpptcpConnection=null;
    public static boolean getXMPPTCPConnection(){
                if(xmpptcpConnection==null) {
                    Log.d("XMPPTCP new 之前", ""+xmpptcpConnection);

                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            //.setUsernameAndPassword("lizhen", "zn521128")
                            .setServiceName(SERVERNAME)
                            .setHost(IP)
                            .setPort(5222)
                            .setSendPresence(true)
                            .setDebuggerEnabled(true)
                            //.setConnectTimeout(10000)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();
                    xmpptcpConnection = new XMPPTCPConnection(config);
                    Log.d("XMPPTCP new 之后", ""+xmpptcpConnection);

                    try {
                        xmpptcpConnection.connect();
                        if(xmpptcpConnection.isConnected())
                           Log.d("*********getXMPPTCP", "getXMPPTCPConnection: connect成功");
                        else
                            Log.d("*********getXMPPTCP", "getXMPPTCPConnection: connect失败");
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
    public static boolean login(String userName,String passwd){
        try {
            //xmpptcpConnection.connect();
            if(xmpptcpConnection.isConnected()) {
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus("ONLINE");
                xmpptcpConnection.login(userName, passwd);
                xmpptcpConnection.sendStanza(presence);           //设置在线状态
                //Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show();
                Connect.initListener(); //初始化信息监听
                Connect.initFriend();   //获取好友列表
                //Chat chat=ChatManager.getInstanceFor(xmpptcpConnection).createChat("lizhen@127.0.0.1");
                //chat.sendMessage("Hello!!!");

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
    public static void initListener() {
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
                        Log.d("TAG接收************", message.getBody());
                        com.example.cm.friend.chat.Message message1=new com.example.cm.friend.chat.Message();

                        JSONObject jsonObject=new JSONObject(message.getBody());
                        message1.setMessageType(jsonObject.getString("type"));
                        if(jsonObject.getString("type").equals("text")) {
                            message1.setBody(jsonObject.getString("data"));
                        }else{
                            message1.setBody("[图片]");
                            //将接收到的字符串解析成bitmap
                            String encodeImageStr=jsonObject.getString("data");
                            Bitmap bitmap=AlbumUtil.byte2Bitmap(AlbumUtil.encodedStr2byte(encodeImageStr));
                            message1.setPhoto(bitmap);
                            //message1.setPhoto(jsonObject.getString("photo"));
                           //message1.setPhoto(new BitmapFactory(R.drawable.unlogin));
                        }
                             message1.setFrom(message.getFrom().split("/")[0]);
                             message1.setTo(message.getTo());
                             //message1.setDate((Date) jsonObject.get("date"));
                             message1.setDate(new Date());
                             message1.setType(2);
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
    public static void initFriend(){
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
                friendInfo.setHeadBt(getUserImage(rosterEntry.getUser().split("/")[0]));  //设置头像
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
        addFriendItemList=new ArrayList<>();
    }
    /*获取用户头像
    *param userName
    */
    public static Bitmap getUserImage(String user){
        Drawable drawable=null;
        Bitmap bitmap=null;
        VCard vCard=new VCard();
        try {
            vCard.load(xmpptcpConnection,user);
            vCard.getAvatar();
            Log.d("获取头像测试", "getUserImage: "+vCard.toString());
            byte []bytes=vCard.getAvatar();
            ByteArrayInputStream bais=new ByteArrayInputStream(vCard.getAvatar());
            bitmap=BitmapFactory.decodeStream(bais);
            BitmapDrawable bitmapDrawable=new BitmapDrawable(bitmap);
            drawable=bitmapDrawable;

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("**/***//**获取头像", "getUserImage: 获取头像异常");
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d("**/***//**获取头像", "getUserImage: 获取头像异常");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d("**/***//**获取头像", "getUserImage: 获取头像异常");
        }
        return bitmap;
    }
    /*修改头像
    *param XMPPConnection
    * *param String 图片的绝对路径
    */
    public static void changeImage(XMPPConnection connection,String absoluteRoad)
            throws XMPPException, IOException {

        VCard vcard = new VCard();
        try {
            vcard.load(xmpptcpConnection);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        File photoFile=new File(absoluteRoad);
        byte []bytes=File2byte(photoFile);
         vcard.setAvatar(bytes);
        try {
            vcard.save(xmpptcpConnection);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

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
