package com.example.cm.myInfo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.util.Connect;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class VCardManager {
    /*获取用户头像
     *param userName
     */
    public static Bitmap getUserImage(String user){
        //user=user+"@"+SERVERNAME;
        Bitmap bitmap=null;
        Log.d("获取头像测试", "getUserImage: ");
        byte []bytes=getUserVcard(user).getAvatar();
        if(bytes==null){
            @SuppressLint("ResourceType") InputStream is1 = MainActivity.getInstance().getResources().openRawResource(R.drawable.unlogin);
             bitmap = BitmapFactory.decodeStream(is1);
        }else {
            ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
            bitmap= BitmapFactory.decodeStream(bais);
        }
        return bitmap;
    }
    /*修改头像
     *@param XMPPConnection
     *@param String 图片的绝对路径
     */
    public static boolean changeImage(XMPPConnection connection, String absoluteRoad)
            throws XMPPException, IOException {

        VCard vcard=null;
        try {
            vcard= org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(Connect.getXMPPTCPConnection()).loadVCard();
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
            org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(Connect.getXMPPTCPConnection()).saveVCard(vcard);
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
        try {
            vCard= org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(Connect.getXMPPTCPConnection()).loadVCard(user+"@"+Connect.SERVERNAME);
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
    static public  boolean setSelfInfo(XMPPTCPConnection mXmpptcpConnection, String key, String value){
        boolean isSucceed=false;
        if((key!=null)&&(value!=null)) {
            try {
                VCard vCard = org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(mXmpptcpConnection).loadVCard();
                if(vCard!=null){
                    if(key.equals("NICKNAME")){
                        vCard.setNickName(value);
                    }else{
                        vCard.setField(key,value);
                    }
                }
                org.jivesoftware.smackx.vcardtemp.VCardManager.getInstanceFor(mXmpptcpConnection).saveVCard(vCard);
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

}
