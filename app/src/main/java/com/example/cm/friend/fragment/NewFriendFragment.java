package com.example.cm.friend.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.friend.AddFriend.AddFriendAdapter;
import com.example.cm.friend.AddFriend.SearchResultAdapter;
import com.example.cm.friend.AddFriend.SearchResultItem;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFriendFragment extends Fragment implements View.OnClickListener {
    private Button add_btn,search_btn;
    private EditText userName_et;
    private ImageView headImage;
    private TextView userName_tv;
    private LinearLayout linearLayout;
    private AddFriendAdapter addFriendAdapter;
    private SearchResultAdapter searchResultAdapter;
    private ArrayList<SearchResultItem> searchResultItems;
    private ListView addFriendLV,searchResultLV;
    private boolean work;

    public NewFriendFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getActivity(),R.layout.activity_add_friend,null);
        searchResultItems=new ArrayList<>();
        search_btn=(Button)view.findViewById(R.id.add_friend_search);
        search_btn.setOnClickListener(this);
        userName_et=(EditText)view.findViewById(R.id.add_friend_et);
        addFriendAdapter=new AddFriendAdapter(getActivity());
        searchResultAdapter=new SearchResultAdapter(searchResultItems,getActivity());
        addFriendLV=(ListView)view.findViewById(R.id.add_friend_lv);
        searchResultLV=view.findViewById(R.id.search_result_lv);
        addFriendLV.setAdapter(addFriendAdapter);
        searchResultLV.setAdapter(searchResultAdapter);
        work=true;


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        work=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(work) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MessageManager.isAddFriendItemListChanged()) {
                                addFriendAdapter.notifyDataSetChanged();
                                MessageManager.setAddFriendItemListChanged(false); ;
                            }
                        }
                    });
                    synchronized((Object)work){
                        if(!work)break;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        work=false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_friend_search:{//查找此用户
                if(Connect.isLogined) {
                    searchResultItems.clear();//每次查找之前清理上次查询的结果
                    boolean have=false;
                    String userName = String.valueOf(userName_et.getText());
                    if(userName.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "输入不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //************//
                        try {
                            UserSearchManager search = new UserSearchManager(Connect.getXMPPTCPConnection());
                            //此处一定要加上 search.
                            Form searchForm = search.getSearchForm("search." + Connect.getXMPPTCPConnection().getServiceName());
                            Form answerForm = searchForm.createAnswerForm();
                            answerForm.setAnswer("Username", true);
                            answerForm.setAnswer("search", userName);
                            ReportedData data = search.getSearchResults(answerForm, "search." + Connect.getXMPPTCPConnection().getServiceName());
                            List<ReportedData.Row> it = data.getRows();
                            /*if (!it.isEmpty()) {
                                Log.e("", "onClick: 有此用户" );
                                have=true;
                                VCard vCard = VCardManager.getUserVcard(userName);
                                linearLayout.setVisibility(View.VISIBLE);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
                                headImage.setImageBitmap(bitmap);
                                userName_tv.setText(userName);

                            }else{
                                Toast.makeText(AddFriendActivity.this, "抱歉，没有这个用户", Toast.LENGTH_SHORT).show();
                            }*/
                            for(int i=0;i<it.size();i++){
                                SearchResultItem resultItem=new SearchResultItem();
                                List<String> user=it.get(i).getValues("UserName");
                                String u=user.get(0);
                                Bitmap bitmap= VCardManager.getUserImage(u);
                                resultItem.setUser(u);
                                resultItem.setBitmap(bitmap);
                                Log.e("", "onClick: "+i+user );
                                searchResultItems.add(resultItem);
                                have=true;
                            }
                            if(have) {
                                searchResultAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getActivity(), "抱歉，没有与此用户名相似的用户", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        //***********//

                    }
                }else{
                    Toast.makeText(getActivity(),"未登录...",Toast.LENGTH_SHORT).show();
                }
            }break;

            default:break;
        }

    }
    //同意添加好友
    public static boolean agreeAddFriend(String userName)  {

        Presence presence=new Presence(Presence.Type.subscribed);
        presence.setTo(userName+"@"+Connect.SERVERNAME);
        try {
            Connect.getXMPPTCPConnection().sendStanza(presence);
            MessageManager.addFriend(userName);//刷新好友列表
            while(!Connect.getRoster().isLoaded()){
                Connect.getRoster().reload();
            }
            Connect.getRoster().createEntry(userName+"@"+Connect.SERVERNAME,userName,new String[]{"Friends"});
            Log.e("", "agreeAddFriend: 同意添加好友" );
        }catch(SmackException.NotConnectedException e){
            e.printStackTrace();
            return false;
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return true;
    }
    //拒绝添加好友
    public static boolean rejectAddFriend(String userName)  {
        Presence presence=new Presence(Presence.Type.unsubscribed);
        presence.setTo(userName+"@"+Connect.SERVERNAME);
        try {
            Connect.getXMPPTCPConnection().sendStanza(presence);
            Log.e("", "rejectAddFriend: 拒绝添加");
        }catch(SmackException.NotConnectedException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
